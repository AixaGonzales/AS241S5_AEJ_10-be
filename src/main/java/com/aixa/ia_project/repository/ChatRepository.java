package com.aixa.ia_project.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.aixa.ia_project.model.ChatRequest;

public interface ChatRepository extends ReactiveMongoRepository<ChatRequest, String> {
}