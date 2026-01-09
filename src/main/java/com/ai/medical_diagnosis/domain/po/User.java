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
public class User {
    private Long userId;    // 用户id
    private String username;    // 用户名
    private String password;    // 密码
    private String email;   // 邮箱
    private String phone;   // 手机号
    private String avatarUrl;  // 头像

    private LocalDateTime createTime;
}
