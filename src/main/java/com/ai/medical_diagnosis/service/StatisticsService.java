package com.ai.medical_diagnosis.service;



import com.ai.medical_diagnosis.domain.vo.ClassifyStatisticsVo;
import com.ai.medical_diagnosis.domain.vo.CountStatisticsVo;
import com.ai.medical_diagnosis.result.Result;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    Result<List<ClassifyStatisticsVo>> classifyStatistics(LocalDate startTime, LocalDate endTime);

    Result<List<CountStatisticsVo>> chatStatistics(LocalDate startTime, LocalDate endTime);

    Result<List<CountStatisticsVo>> diagnosisStatistics(LocalDate startTime, LocalDate endTime);
}
