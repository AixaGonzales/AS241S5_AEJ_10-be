package com.aixa.ia_project.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.aixa.ia_project.model.ImageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ImageRepository extends ReactiveMongoRepository<ImageRequest, String> {
    Flux<ImageRequest> findByPromptContaining(String keyword);
    Flux<ImageRequest> findByActiveTrue();
    Flux<ImageRequest> findByActiveTrueOrderByCreatedAtDesc();
    Mono<Long> countByActiveTrue();
    Flux<ImageRequest> findAllByOrderByCreatedAtDesc();
}