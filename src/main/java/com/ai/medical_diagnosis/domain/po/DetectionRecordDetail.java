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
// 详细记录
public class DetectionRecordDetail {
    private Long detectionRecordId;
    private Long userId;    // 用户id
    private String memoryId;    // 记录id
    private String description; // 图片描述
    private String classify;    // 分类结果
    private String ImgUrl; // 图片url
    private String mergedImageUrl;  // 二维掩码合并原图url
    private String binaryMaskUrl;   // 二维掩码图片url
    private String analysisResult; // 大模型分析结果

    private LocalDateTime createTime;
}
