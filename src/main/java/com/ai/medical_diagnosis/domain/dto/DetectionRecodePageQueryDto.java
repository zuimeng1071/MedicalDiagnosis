package com.ai.medical_diagnosis.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionRecodePageQueryDto {
    private Integer page;
    private Integer pageSize;

    // 根据描述查找
    private String classify;            // 分类
    private String diagnosis;           // 诊断
    // 根据时间范围查找
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;    // 开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;      // 结束时间
}
