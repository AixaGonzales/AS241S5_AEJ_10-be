package com.aixa.ia_project.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.aixa.ia_project.service.ImageService;

@RestController
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/image")
    public Mono<String> generate(@RequestParam String prompt) {
        return imageService.generateImage(prompt);
    }
}