package com.ai.medical_diagnosis.mapper;


import com.ai.medical_diagnosis.domain.dto.DetectionRecodePageQueryDto;
import com.ai.medical_diagnosis.domain.po.DetectionRecordDetail;
import com.ai.medical_diagnosis.domain.po.DetectionRecordSummary;
import com.ai.medical_diagnosis.domain.vo.ClassifyStatisticsVo;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DetectionRecordMapper {
    void insertDetail(DetectionRecordDetail record);

    void insertSummary(DetectionRecordSummary summary);

    @Select("select * from detection_record_detail where detection_record_id = #{id}")
    DetectionRecordDetail selectById(Long id);

    Page<DetectionRecordSummary> query(DetectionRecodePageQueryDto dto, Long userId);

    List<ClassifyStatisticsVo> classifyStatistics(LocalDateTime startTime, LocalDateTime endTime);

    Integer diagnosisStatistics(LocalDateTime startTime, LocalDateTime endTime);
}
