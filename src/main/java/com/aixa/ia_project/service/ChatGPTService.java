package com.aixa.ia_project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChatGPTService {

    private static final Logger log = LoggerFactory.getLogger(ChatGPTService.class);

    private final WebClient webClient;
    private final String apiKey;
    private final String host;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatGPTService(
            WebClient.Builder webClientBuilder,
            @Value("${api.rapidapi.key}") String apiKey,
            @Value("${api.rapidapi.chatgpt.host}") String host) {
        this.apiKey = apiKey;
        this.host = host;
        this.webClient = webClientBuilder.baseUrl("https://" + host).build();
        log.debug("ChatGPTService inicializado con host: {}", host);
    }

    // ✅ Solo llama a la IA y retorna la respuesta
    // El guardado en MongoDB lo hace el Controller o quien llame a este metodo
    public Mono<String> ask(String message) {
        String escapedMessage = message.replace("\"", "\\\"").replace("\n", "\\n");

        String requestBody = String.format("""
            {
                "messages": [{"role": "user", "content": "%s"}],
                "web_access": false
            }
            """, escapedMessage);

        log.debug("Enviando peticion a ChatGPT con mensaje: {}", message);

        return webClient.post()
                .uri("/conversationllama")
                .header("Content-Type", "application/json")
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", host)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        log.debug("Respuesta recibida: {}", response);
                        JsonNode root = objectMapper.readTree(response);
                        String answer = root.path("result").asText();

                        if (answer == null || answer.isEmpty()) {
                            answer = "No se pudo obtener una respuesta de la IA";
                        }

                        log.debug("Respuesta parseada correctamente");
                        return Mono.just(answer);
                    } catch (Exception e) {
                        log.error("Error procesando respuesta: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Error procesando respuesta de ChatGPT: " + e.getMessage()));
                    }
                })
                .doOnError(error -> log.error("Error en llamada a API ChatGPT: {}", error.getMessage()));
    }
}