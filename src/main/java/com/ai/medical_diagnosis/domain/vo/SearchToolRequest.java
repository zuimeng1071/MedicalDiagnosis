package com.ai.medical_diagnosis.domain.vo;

import lombok.Data;

@Data
public class SearchToolRequest {
    private String query;
    private Integer topK;
}
