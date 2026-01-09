package com.ai.medical_diagnosis.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentImageResponse {
    private String mergedImage;
    private String binaryMask;
}
