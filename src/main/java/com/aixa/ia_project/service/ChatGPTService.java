package com.aixa.ia_project.service;

import com.aixa.ia_project.model.ChatRequest;
import com.aixa.ia_project.repository.ChatRepository;
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
    private final ChatRepository chatRepository;

    @Value("${api.rapidapi.key}")
    private String apiKey;

    @Value("${api.rapidapi.chatgpt.host}")
    private String host;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatGPTService(WebClient webClient, ChatRepository chatRepository) {
        this.webClient = webClient;
        this.chatRepository = chatRepository;
    }

    public Mono<String> ask(String message) {
        String escapedMessage = message.replace("\"", "\\\"").replace("\n", "\\n");
        String requestBody = String.format("""
            {
              "messages":[{"role":"user","content":"%s"}],
              "system_prompt":"",
              "temperature":0.9,
              "top_k":5,
              "top_p":0.9,
              "max_tokens":256,
              "web_access":false
            }
            """, escapedMessage);

        log.debug("Enviando petición a ChatGPT: {}", requestBody);

        return webClient.post()
                .uri("https://" + host + "/conversationgpt4-2")
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
                            answer = "No se pudo obtener una respuesta";
                        }

                        ChatRequest entity = new ChatRequest(message, answer);
                        return chatRepository.save(entity)
                                .thenReturn(answer);
                    } catch (Exception e) {
                        log.error("Error procesando respuesta: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Error procesando respuesta de ChatGPT", e));
                    }
                })
                .doOnError(error -> log.error("Error en la llamada a la API: {}", error.getMessage()));
    }
}