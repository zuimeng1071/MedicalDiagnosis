package com.ai.medical_diagnosis.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ClassifyVo {
    private Map<String, Float> classifyMap;
//    private String classify;
//    private Float confidence;
}
