package com.aixa.ia_project.controller;

import com.aixa.ia_project.service.ChatGPTService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatGPTService service;

    public ChatController(ChatGPTService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<String> chat(@RequestParam @NotBlank String message) {
        if (message.length() > 1000) {
            return Mono.just("Error: Mensaje demasiado largo (máx 1000 caracteres)");
        }
        return service.ask(message)
                .onErrorResume(e -> Mono.just("Error al procesar el mensaje: " + e.getMessage()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> chatPost(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return Mono.just("Error: El mensaje no puede estar vacío");
        }
        return service.ask(message);
    }
}