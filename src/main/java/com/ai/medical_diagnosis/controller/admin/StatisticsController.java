package com.ai.medical_diagnosis.controller.admin;


import com.ai.medical_diagnosis.domain.vo.ClassifyStatisticsVo;
import com.ai.medical_diagnosis.domain.vo.CountStatisticsVo;
import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/statistics")
@Tag(name = "统计接口")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // 诊断记录分类数据统计(饼状图)
    @GetMapping("classifyStatistics")
    @Operation(summary = "分类统计")
    public Result<List<ClassifyStatisticsVo>> classifyStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime){
        return statisticsService.classifyStatistics(startTime, endTime);
    }

    // 聊天次数统计（折线图）
    @GetMapping("chatStatistics")
    @Operation(summary = "聊天次数统计")
    public Result<List<CountStatisticsVo>> chatStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime){
        return statisticsService.chatStatistics(startTime, endTime);
    }

    // 诊断次数统计（折线图）
    @GetMapping("diagnosisStatistics")
    @Operation(summary = "诊断次数统计")
    public Result<List<CountStatisticsVo>> diagnosisStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime){
        return statisticsService.diagnosisStatistics(startTime, endTime);
    }
}
