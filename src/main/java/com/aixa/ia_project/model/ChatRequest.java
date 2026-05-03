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
@Document(collection = "chat_requests")
public class ChatRequest {
    @Id
    private String id;
    private String prompt;
    private String response;
    private LocalDateTime createdAt;
    private Boolean active = true;
}