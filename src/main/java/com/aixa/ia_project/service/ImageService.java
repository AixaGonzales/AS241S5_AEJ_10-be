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
    private final String apiKey;
    private final String host;

    // ✅ CORREGIDO: apiKey y host inyectados por constructor (antes eran null al usarse)
    public ImageService(
            WebClient webClient,
            ImageRepository imageRepository,
            @Value("${api.rapidapi.key}") String apiKey,
            @Value("${api.rapidapi.text-to-image.host}") String host) {
        this.webClient = webClient;
        this.imageRepository = imageRepository;
        this.apiKey = apiKey;
        this.host = host;
        log.debug("ImageService inicializado con host: {}", host);
    }

    // ✅ Solo llama a la IA y retorna la URL de imagen
    // El guardado en MongoDB lo hace el Controller
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
                            String imageUrl = finalResult.get(0).path("origin").asText();

                            if (imageUrl == null || imageUrl.isEmpty()) {
                                return Mono.error(new RuntimeException("La URL de la imagen esta vacia"));
                            }

                            log.debug("URL de imagen obtenida: {}", imageUrl);
                            // ✅ Solo retorna la URL, NO guarda en MongoDB
                            return Mono.just(imageUrl);
                        } else {
                            return Mono.error(new RuntimeException("No se encontro imagen en la respuesta: " + response));
                        }
                    } catch (Exception e) {
                        log.error("Error procesando respuesta de imagen: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Error procesando respuesta de la API de imagenes: " + e.getMessage()));
                    }
                })
                .doOnError(error -> log.error("Error en generacion de imagen: {}", error.getMessage()));
    }
}