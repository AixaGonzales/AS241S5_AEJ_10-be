package com.aixa.ia_project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "images")
public class ImageRequest {
    @Id
    private String id;
    private String prompt;
    private String imageUrl;
    private LocalDateTime createdAt;
    
    public ImageRequest(String prompt, String imageUrl) {
        this.prompt = prompt;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }
}