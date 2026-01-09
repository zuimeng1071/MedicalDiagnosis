package com.ai.medical_diagnosis.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DetectionRecordDetailVo {
    private Integer age;    // 年龄
    private String gender;  //  性别
    private String description; // 症状描述
    private String imageUrl;    // 图片url
}
