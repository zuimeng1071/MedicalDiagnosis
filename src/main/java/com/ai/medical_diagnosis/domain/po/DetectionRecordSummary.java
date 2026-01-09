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
public class DetectionRecordSummary {
    private Long detectionRecordId;
    private Long userId;    // 用户id
    private String classify;    // 分类结果
    private String diagnosis;   // 诊断结果（前50字）

    private LocalDateTime createTime;
}
