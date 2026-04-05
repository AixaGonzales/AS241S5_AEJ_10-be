package com.aixa.ia_project.controller;

import com.aixa.ia_project.service.ChatGPTService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatGPTService service;

    public ChatController(ChatGPTService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<String> chat(@RequestParam String message) {
        return service.ask(message);
    }
}