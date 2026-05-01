package com.aixa.ia_project.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.aixa.ia_project.model.ChatRequest;
import reactor.core.publisher.Flux;

@Repository
public interface ChatRepository extends ReactiveMongoRepository<ChatRequest, String> {
    Flux<ChatRequest> findByPromptContaining(String keyword);
}