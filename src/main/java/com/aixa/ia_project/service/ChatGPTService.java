package com.aixa.ia_project.service;

import com.aixa.ia_project.model.ChatRequest;
import com.aixa.ia_project.repository.ChatRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ChatGPTService {

    private final WebClient webClient;
    private final ChatRepository chatRepository;

    @Value("${api.rapidapi.key}")
    private String apiKey;

    @Value("${api.rapidapi.chatgpt.host}")
    private String host;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatGPTService(WebClient.Builder builder, ChatRepository chatRepository) {
        this.webClient = builder
                .baseUrl("https://chatgpt-42.p.rapidapi.com")
                .build();
        this.chatRepository = chatRepository;
    }

public Mono<String> ask(String message) {
    return webClient.post()
            .uri("/conversationgpt4-2")
            .header("Content-Type", "application/json")
            .header("x-rapidapi-key", apiKey)
            .header("x-rapidapi-host", host)
            .bodyValue("""
            {
              "messages":[{"role":"user","content":"%s"}],
              "system_prompt":"",
              "temperature":0.9,
              "top_k":5,
              "top_p":0.9,
              "max_tokens":256,
              "web_access":false
            }
            """.formatted(message))
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(response -> {
                try {
                    JsonNode root = objectMapper.readTree(response);
                    String answer = root.path("result").asText();

                    ChatRequest entity = new ChatRequest(message, answer);

                    return chatRepository.save(entity)
                            .thenReturn(answer);

                } catch (Exception e) {
                    return Mono.error(new RuntimeException("Error procesando respuesta", e));
                }
            });
}
}