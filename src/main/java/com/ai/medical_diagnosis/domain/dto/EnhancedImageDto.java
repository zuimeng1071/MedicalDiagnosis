package com.ai.medical_diagnosis.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EnhancedImageDto {
    @JsonProperty("enhanced_image")
    private String enhancedImage;
}
