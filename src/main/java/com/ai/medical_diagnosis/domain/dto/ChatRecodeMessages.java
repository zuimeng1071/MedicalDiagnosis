package com.ai.medical_diagnosis.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatRecodeMessages {
    private String memoryId;
    private List<ChatRecodeMessage> messages;
}
