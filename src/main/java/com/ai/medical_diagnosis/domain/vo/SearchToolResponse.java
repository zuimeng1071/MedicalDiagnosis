package com.ai.medical_diagnosis.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchToolResponse {
    private String result;
}
