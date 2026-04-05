package com.aixa.ia_project.service;

import com.aixa.ia_project.model.ImageRequest;
import com.aixa.ia_project.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImageService {

    private final WebClient webClient;
    private final ImageRepository imageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.rapidapi.key}")
    private String apiKey;

    @Value("${api.rapidapi.text-to-image.host}")
    private String host;

    public ImageService(WebClient.Builder builder, ImageRepository imageRepository) {
        this.webClient = builder
                .baseUrl("https://ai-text-to-image-generator-flux-free-api.p.rapidapi.com")
                .build();
        this.imageRepository = imageRepository;
    }

    public Mono<String> generateImage(String prompt) {
        return webClient.post()
                .uri("/aaaaaaaaaaaaaaaaaiimagegenerator/quick.php")
                .header("Content-Type", "application/json")
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", host)
                .bodyValue("""
                {
                  "prompt": "%s",
                  "style_id": 4,
                  "size": "1-1"
                }
                """.formatted(prompt))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);

                        JsonNode finalResult = root.path("final_result");

                        if (finalResult.isArray() && finalResult.size() > 0) {
                            String imageUrl = finalResult.get(0)
                                    .path("origin")
                                    .asText();

                            ImageRequest entity = new ImageRequest(prompt, imageUrl);

                            return imageRepository.save(entity)
                                    .thenReturn(imageUrl);
                        } else {
                            return Mono.error(new RuntimeException("No se encontró imagen en la respuesta"));
                        }

                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error procesando respuesta de la API"));
                    }
                });
    }
}