package com.ai.medical_diagnosis.service.impl;


import com.ai.medical_diagnosis.domain.vo.ClassifyStatisticsVo;
import com.ai.medical_diagnosis.domain.vo.CountStatisticsVo;
import com.ai.medical_diagnosis.mapper.AiChatMapper;
import com.ai.medical_diagnosis.mapper.DetectionRecordMapper;
import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务实现类
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final DetectionRecordMapper detectionRecordMapper;
    private final AiChatMapper aiChatMapper;

    public StatisticsServiceImpl(DetectionRecordMapper detectionRecordMapper,
                                 AiChatMapper aiChatMapper) {
        this.detectionRecordMapper = detectionRecordMapper;
        this.aiChatMapper = aiChatMapper;
    }


    /**
     * 分类统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分类统计结果
     */
    @Override
    public Result<List<ClassifyStatisticsVo>> classifyStatistics(LocalDate startTime, LocalDate endTime) {
        if (startTime != null && endTime != null){
            if (startTime.isAfter(endTime)){
                return Result.error("开始时间不能大于结束时间");
            }
        }
        LocalDateTime startLocalDateTime = null;
        LocalDateTime endLocalDateTime = null;
        if (startTime != null) {
            startLocalDateTime = LocalDateTime.of(startTime, LocalTime.MIN);
        }
        if (endTime != null) {
            endLocalDateTime = LocalDateTime.of(endTime, LocalTime.MAX);
        }
        List<ClassifyStatisticsVo> classifyStatistics = detectionRecordMapper.classifyStatistics(startLocalDateTime, endLocalDateTime);
        return Result.success(classifyStatistics);
    }

    /**
     *  聊天统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 聊天统计结果
     */
    @Override
    public Result<List<CountStatisticsVo>> chatStatistics(LocalDate startTime, LocalDate endTime) {
        List<CountStatisticsVo> chatStatistics = new ArrayList<>();
        if (startTime != null && endTime != null){
            if (startTime.isAfter(endTime)){
                return Result.error("开始时间不能大于结束时间");
            }
            List<LocalDate> localDates = getDateList(startTime, endTime);
            for (LocalDate localDate : localDates){
                CountStatisticsVo countStatisticsVo = new CountStatisticsVo();
                LocalDateTime startLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
                LocalDateTime endLocalDateTime = LocalDateTime.of(localDate, LocalTime.MAX);

                Integer chatCount = aiChatMapper.chatStatistics(startLocalDateTime, endLocalDateTime);
                countStatisticsVo.setDate(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                countStatisticsVo.setCount(chatCount);

                chatStatistics.add(countStatisticsVo);

            }

            return Result.success(chatStatistics);
        }
        return Result.error("请选择时间范围");
    }

    /**
     *  诊断统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 诊断统计结果
     */
    @Override
    public Result<List<CountStatisticsVo>> diagnosisStatistics(LocalDate startTime, LocalDate endTime) {
        List<CountStatisticsVo> diagnosisStatistics = new ArrayList<>();
        if (startTime != null && endTime != null){
            if (startTime.isAfter(endTime)){
                return Result.error("开始时间不能大于结束时间");
            }
            List<LocalDate> localDates = getDateList(startTime, endTime);
            for (LocalDate localDate : localDates){
                CountStatisticsVo countStatisticsVo = new CountStatisticsVo();
                LocalDateTime startLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
                LocalDateTime endLocalDateTime = LocalDateTime.of(localDate, LocalTime.MAX);

                Integer chatCount = detectionRecordMapper.diagnosisStatistics(startLocalDateTime, endLocalDateTime);
                countStatisticsVo.setDate(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                countStatisticsVo.setCount(chatCount);

                diagnosisStatistics.add(countStatisticsVo);

            }

            return Result.success(diagnosisStatistics);
        }
        return Result.error("请选择时间范围");
    }

    /**
     * 获取时间列表
     * @param begin 开始时间
     * @param end 结束时间
     * @return 时间列表
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {

        // end > begin
        if (begin.isAfter(end)){
            throw new RuntimeException("开始时间不能大于结束时间");
        }
        List<LocalDate> dateList = new ArrayList<>();

        LocalDate date = begin;
        while (!date.isAfter(end)) {
            dateList.add(date);
            date = date.plusDays(1);
        }
        return dateList;
    }
}
