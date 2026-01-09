package com.ai.medical_diagnosis.domain.vo;

import lombok.Data;

@Data
public class ClassifyStatisticsVo {
    private String classify;
    private Integer count;  // 数量
}
