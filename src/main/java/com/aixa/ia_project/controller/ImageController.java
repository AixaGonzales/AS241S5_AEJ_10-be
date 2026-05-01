package com.aixa.ia_project.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.aixa.ia_project.service.ImageService;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public Mono<String> generate(@RequestParam @NotBlank String prompt) {
        if (prompt.length() > 500) {
            return Mono.just("Error: Prompt demasiado largo (máx 500 caracteres)");
        }
        return imageService.generateImage(prompt)
                .onErrorResume(e -> Mono.just("Error al generar imagen: " + e.getMessage()));
    }

    @PostMapping
    public Mono<String> generatePost(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.isBlank()) {
            return Mono.just("Error: El prompt no puede estar vacío");
        }
        return imageService.generateImage(prompt);
    }
}