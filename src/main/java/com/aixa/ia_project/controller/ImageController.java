package com.aixa.ia_project.controller;

import com.aixa.ia_project.model.ApiResponse;
import com.aixa.ia_project.model.ImageRequest;
import com.aixa.ia_project.repository.ImageRepository;
import com.aixa.ia_project.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/image")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ImageController {

    private final ImageRepository imageRepository;
    private final ImageService imageService;

    public ImageController(ImageRepository imageRepository, ImageService imageService) {
        this.imageRepository = imageRepository;
        this.imageService = imageService;
    }

    // ============================================
    // C - CREATE: Llama a la IA y guarda en MongoDB
    // ============================================
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<ImageRequest>> createImage(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");

        if (prompt == null || prompt.trim().isEmpty()) {
            return Mono.just(ApiResponse.error("El prompt no puede estar vacio"));
        }
        if (prompt.length() > 500) {
            return Mono.just(ApiResponse.error("El prompt es demasiado largo (max 500 caracteres)"));
        }

        return imageService.generateImage(prompt)
            .flatMap(imageUrl -> {
                ImageRequest newImage = new ImageRequest();
                newImage.setPrompt(prompt);
                newImage.setImageUrl(imageUrl);
                newImage.setCreatedAt(LocalDateTime.now());
                newImage.setActive(true);
                // ✅ Una sola vez guardamos en MongoDB
                return imageRepository.save(newImage);
            })
            .map(image -> ApiResponse.success("Imagen generada exitosamente", image))
            .onErrorResume(e -> Mono.just(ApiResponse.error("Error al generar imagen: " + e.getMessage())));
    }

    // ============================================
    // R - READ ALL: Lista todas las imagenes activas
    // ============================================
    @GetMapping("/all")
    public Mono<ApiResponse<List<ImageRequest>>> getAllImages() {
        return imageRepository.findByActiveTrue()
            .collectList()
            .map(images -> ApiResponse.success("Lista de imagenes", images))
            .defaultIfEmpty(ApiResponse.success("No hay imagenes", List.of()))
            .onErrorResume(e -> Mono.just(ApiResponse.error("Error al obtener imagenes: " + e.getMessage())));
    }

    // ============================================
    // R - READ ONE: Obtiene imagen por ID
    // ============================================
    @GetMapping("/{id}")
    public Mono<ApiResponse<ImageRequest>> getImageById(@PathVariable String id) {
        return imageRepository.findById(id)
            .filter(ImageRequest::getActive)
            .map(image -> ApiResponse.success("Imagen encontrada", image))
            .switchIfEmpty(Mono.just(ApiResponse.error("Imagen no encontrada con ID: " + id)));
    }

    // ============================================
    // U - UPDATE: Llama a la IA con nuevo prompt y actualiza en MongoDB
    // ============================================
    @PutMapping("/{id}")
    public Mono<ApiResponse<ImageRequest>> updateImage(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        String prompt = body.get("prompt");

        if (prompt == null || prompt.trim().isEmpty()) {
            return Mono.just(ApiResponse.error("El prompt es requerido para actualizar"));
        }

        return imageRepository.findById(id)
            .filter(ImageRequest::getActive)
            .switchIfEmpty(Mono.error(new RuntimeException("Imagen no encontrada o inactiva con ID: " + id)))
            .flatMap(existingImage ->
                imageService.generateImage(prompt)
                    .flatMap(newUrl -> {
                        existingImage.setPrompt(prompt);
                        existingImage.setImageUrl(newUrl);
                        existingImage.setCreatedAt(LocalDateTime.now());
                        return imageRepository.save(existingImage);
                    })
            )
            .map(image -> ApiResponse.success("Imagen regenerada exitosamente", image))
            .onErrorResume(e -> Mono.just(ApiResponse.error("Error al regenerar imagen: " + e.getMessage())));
    }

    // ============================================
    // D - DELETE: Borrado logico (active = false)
    // ============================================
    @DeleteMapping("/{id}")
    public Mono<ApiResponse<String>> deleteImage(@PathVariable String id) {
        return imageRepository.findById(id)
            .filter(ImageRequest::getActive)
            .flatMap(image -> {
                image.setActive(false);
                return imageRepository.save(image);
            })
            .map(image -> ApiResponse.success("Imagen eliminada exitosamente", "OK"))
            .switchIfEmpty(Mono.just(ApiResponse.error("Imagen no encontrada con ID: " + id)));
    }
}