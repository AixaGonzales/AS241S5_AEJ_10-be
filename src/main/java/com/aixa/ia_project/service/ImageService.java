package com.aixa.ia_project.service;

import com.aixa.ia_project.model.ImageRequest;
import com.aixa.ia_project.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);
    
    private final WebClient webClient;
    private final ImageRepository imageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.rapidapi.key}")
    private String apiKey;

    @Value("${api.rapidapi.text-to-image.host}")
    private String host;

    public ImageService(WebClient webClient, ImageRepository imageRepository) {
        this.webClient = webClient;
        this.imageRepository = imageRepository;
    }

    public Mono<String> generateImage(String prompt) {
        String escapedPrompt = prompt.replace("\"", "\\\"").replace("\n", " ");
        String requestBody = String.format("""
            {
              "prompt": "%s",
              "style_id": 4,
              "size": "1-1"
            }
            """, escapedPrompt);

        log.debug("Generando imagen con prompt: {}", prompt);

        return webClient.post()
                .uri("https://" + host + "/aaaaaaaaaaaaaaaaaiimagegenerator/quick.php")
                .header("Content-Type", "application/json")
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", host)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        log.debug("Respuesta imagen: {}", response);
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode finalResult = root.path("final_result");

                        if (finalResult.isArray() && finalResult.size() > 0) {
                            String imageUrl = finalResult.get(0)
                                    .path("origin")
                                    .asText();
                            
                            if (imageUrl == null || imageUrl.isEmpty()) {
                                return Mono.error(new RuntimeException("La URL de la imagen está vacía"));
                            }

                            ImageRequest entity = new ImageRequest(prompt, imageUrl);
                            return imageRepository.save(entity)
                                    .thenReturn(imageUrl);
                        } else {
                            return Mono.error(new RuntimeException("No se encontró imagen en la respuesta"));
                        }
                    } catch (Exception e) {
                        log.error("Error procesando respuesta de imagen: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Error procesando respuesta de la API de imágenes", e));
                    }
                })
                .doOnError(error -> log.error("Error en generación de imagen: {}", error.getMessage()));
    }
}