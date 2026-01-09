package com.ai.medical_diagnosis.domain.dto;

import lombok.Data;

@Data
public class ChatDto {
    private String memoryId;
    private String question;
}
