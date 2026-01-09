package com.ai.medical_diagnosis.domain.vo;

import lombok.Data;
import java.util.List;

@Data
public class TextSegmentResponse {
    private String segmentedText;     // 原始分割文本（含 \n 和可能的 \t）
    private List<String> paragraphs;  // 清洗后的段落列表
    private int paragraphCount;       // 段落数量
    private long processingTimeMs;    // 处理耗时（毫秒）

    // 错误情况下的字段（可选，用于统一错误处理）
    private String error;
    private String message;
}