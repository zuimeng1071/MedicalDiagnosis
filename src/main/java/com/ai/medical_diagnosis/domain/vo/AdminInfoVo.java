package com.ai.medical_diagnosis.domain.vo;

import lombok.Data;

@Data
public class AdminInfoVo {
    private Long userId;    // 用户id
    private String username;    // 用户名
    private String email;   // 邮箱
    private String phone;   // 手机号
    private String avatarUrl;  // 头像
}
