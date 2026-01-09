package com.ai.medical_diagnosis.domain.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatarUrl;


    private LocalDateTime createTime;
}
