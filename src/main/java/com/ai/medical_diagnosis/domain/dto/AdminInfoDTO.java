package com.ai.medical_diagnosis.domain.dto;


import lombok.Data;

@Data
public class AdminInfoDTO {
    private Long userId;
    private String username;
    private String token;
}
