package com.aixa.ia_project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_requests")
public class ChatRequest {

    @Id
    private String id;

    private String prompt;
    private String response;

    public ChatRequest() {
    }

    public ChatRequest(String prompt, String response) {
        this.prompt = prompt;
        this.response = response;
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getResponse() {
        return response;
    }

    // Setters

    public void setId(String id) {
        this.id = id;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ChatRequest{" +
                "id='" + id + '\'' +
                ", prompt='" + prompt + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}