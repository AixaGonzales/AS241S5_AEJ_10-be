package com.aixa.ia_project.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.aixa.ia_project.model.ImageRequest;

@Repository
public interface ImageRepository extends ReactiveMongoRepository<ImageRequest, String> {
}