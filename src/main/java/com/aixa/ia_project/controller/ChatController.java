package com.aixa.ia_project.controller;

import com.aixa.ia_project.model.ApiResponse;
import com.aixa.ia_project.model.ChatRequest;
import com.aixa.ia_project.repository.ChatRepository;
import com.aixa.ia_project.service.ChatGPTService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ChatController {

    private final ChatRepository chatRepository;
    private final ChatGPTService chatGPTService;

    public ChatController(ChatRepository chatRepository, ChatGPTService chatGPTService) {
        this.chatRepository = chatRepository;
        this.chatGPTService = chatGPTService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<ChatRequest>> createChat(@RequestBody ChatRequest request) {
        String prompt = request.getPrompt();

        if (prompt == null || prompt.trim().isEmpty()) {
            return Mono.just(ApiResponse.error("El prompt no puede estar vacio"));
        }

        return chatGPTService.ask(prompt)
            .flatMap(response -> {
                ChatRequest newChat = new ChatRequest();
                newChat.setPrompt(prompt);
                newChat.setResponse(response);
                newChat.setCreatedAt(LocalDateTime.now());
                newChat.setActive(true);
                System.out.println("Guardando en MongoDB: " + newChat);
                return chatRepository.save(newChat);
            })
            .map(chat -> {
                System.out.println("Guardado exitoso con ID: " + chat.getId());
                return ApiResponse.success("Chat creado exitosamente", chat);
            })
            .onErrorResume(e -> {
                System.err.println("Error al guardar: " + e.getMessage());
                return Mono.just(ApiResponse.error("Error al crear chat: " + e.getMessage()));
            });
    }

    @GetMapping("/all")
    public Mono<ApiResponse<java.util.List<ChatRequest>>> getAllChats() {
        return chatRepository.findByActiveTrue()
            .collectList()
            .map(chats -> ApiResponse.success("Lista de chats", chats))
            .defaultIfEmpty(ApiResponse.success("No hay chats", java.util.List.of()))
            .onErrorResume(e -> Mono.just(ApiResponse.error("Error al obtener chats: " + e.getMessage())));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<ChatRequest>> updateChat(
            @PathVariable String id,
            @RequestBody ChatRequest request) {

        String prompt = request.getPrompt();

        if (prompt == null || prompt.trim().isEmpty()) {
            return Mono.just(ApiResponse.error("El prompt es requerido para actualizar"));
        }

        return chatRepository.findById(id)
            .filter(ChatRequest::getActive)
            .switchIfEmpty(Mono.error(new RuntimeException("Chat no encontrado")))
            .flatMap(existingChat ->
                chatGPTService.ask(prompt)
                    .flatMap(newResponse -> {
                        existingChat.setPrompt(prompt);
                        existingChat.setResponse(newResponse);
                        existingChat.setCreatedAt(LocalDateTime.now());
                        System.out.println("Actualizando chat ID: " + id);
                        return chatRepository.save(existingChat);
                    })
            )
            .map(chat -> ApiResponse.success("Chat actualizado exitosamente", chat))
            .onErrorResume(e -> Mono.just(ApiResponse.error("Error al actualizar: " + e.getMessage())));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<String>> deleteChat(@PathVariable String id) {
        return chatRepository.findById(id)
            .filter(ChatRequest::getActive)
            .flatMap(chat -> {
                chat.setActive(false);
                return chatRepository.save(chat);
            })
            .map(chat -> ApiResponse.success("Chat eliminado exitosamente", "OK"))
            .switchIfEmpty(Mono.just(ApiResponse.error("Chat no encontrado")));
    }
}