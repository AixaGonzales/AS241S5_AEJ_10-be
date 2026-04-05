package com.aixa.ia_project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "images")
public class ImageRequest {

    @Id
    private String id;

    private String prompt;
    private String imageUrl;

    public ImageRequest() {
    }

    public ImageRequest(String prompt, String imageUrl) {
        this.prompt = prompt;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}