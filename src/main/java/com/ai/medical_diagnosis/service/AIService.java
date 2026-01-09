package com.ai.medical_diagnosis.service;


import com.ai.medical_diagnosis.domain.dto.ChatRecodeMessages;
import com.ai.medical_diagnosis.domain.dto.ChatRecodePageQueryDto;
import com.ai.medical_diagnosis.domain.dto.DetectionRecodePageQueryDto;
import com.ai.medical_diagnosis.domain.po.ChatRecode;
import com.ai.medical_diagnosis.domain.po.DetectionRecordDetail;
import com.ai.medical_diagnosis.domain.po.DetectionRecordSummary;
import com.ai.medical_diagnosis.domain.vo.ClassifyVo;
import com.ai.medical_diagnosis.domain.vo.DetectionRecordDetailVo;
import com.ai.medical_diagnosis.domain.vo.SegmentImageResponse;
import com.ai.medical_diagnosis.result.PageResult;
import com.ai.medical_diagnosis.result.Result;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;

public interface AIService {

    Result<SegmentImageResponse> segment(byte[] image);

    Result<PageResult<DetectionRecordSummary>> detectionRecodePageQuery(DetectionRecodePageQueryDto detectionRecodePageQueryDto);

    Result<DetectionRecordDetail> diagnosis(DetectionRecordDetailVo detectionRecordDetailVo);

    Result<DetectionRecordDetail> detectionRecodeDetail(Long id);

    Result<String> chat(String memoryId, String question) throws GraphRunnerException;
    
    Result<PageResult<ChatRecode>> chatRecodePageQuery(ChatRecodePageQueryDto dto);

    Result<ChatRecodeMessages> chatRecodeDetail(String memoryId);

    ClassifyVo classify(String imageUrl) throws GraphRunnerException;

    Result<String> chatRecodeDelete(String memoryId);
}
