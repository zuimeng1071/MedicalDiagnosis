package com.ai.medical_diagnosis.domain.dto;

import lombok.Data;

@Data
public class UserPageQueryDto {
    private Integer page;
    private Integer pageSize;

    private String username;    // 用户名
    private String email;   // 邮箱
    private String phone;   // 手机号
}
