package com.ai.medical_diagnosis.controller.user;


import com.ai.medical_diagnosis.domain.dto.ChatDto;
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
import com.ai.medical_diagnosis.service.AIService;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/user/ai")
@Tag(name = "AI接口")
public class AiController {

    private final AIService aiService;

    public AiController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping(value = "/chat")
    @Operation(summary = "ai聊天接口")
    public Result<String> chat(@RequestBody ChatDto dto) throws GraphRunnerException {
        log.info("用户发起了聊天请求{}", dto);
        return aiService.chat(dto.getMemoryId(), dto.getQuestion());
    }

    @GetMapping(value = "/classify")
    @Operation(summary = "AI分类接口")
    public Result<ClassifyVo> classify(@RequestParam String imageUrl) {

        try {
            return Result.success(aiService.classify(imageUrl));
        } catch (Exception e) {
            return Result.error("图像处理失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/diagnosis")
    @Operation(summary = "AI诊断接口")
    public Result<DetectionRecordDetail> diagnosis(@RequestBody DetectionRecordDetailVo detectionRecordDetailVo) {

        try {
            return aiService.diagnosis(detectionRecordDetailVo);
        } catch (Exception e) {
            return Result.error("图像处理失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/chatRecodePageQuery")
    @Operation(summary = "ai聊天记录接口")
    public Result<PageResult<ChatRecode>> chatRecodePageQuery(@RequestBody ChatRecodePageQueryDto dto) {
        return aiService.chatRecodePageQuery(dto);
    }

    @GetMapping(value = "/chatRecodeDelete/{memoryId}")
    @Operation(summary = "ai聊天记录删除接口")
    public Result<String> chatRecodeDelete(@PathVariable String memoryId) {
        return aiService.chatRecodeDelete(memoryId);
    }

    @GetMapping(value = "/chatRecodeDetail/{memoryId}")
    @Operation(summary = "ai聊天记录详情接口")
    public Result<ChatRecodeMessages> chatRecodeDetail(@PathVariable String memoryId) {
        return aiService.chatRecodeDetail(memoryId);
    }

    @PostMapping(value = "/detectionRecodePageQuery")
    @Operation(summary = "AI诊断记录接口")
    public Result<PageResult<DetectionRecordSummary>> detectionRecodePageQuery(
            @RequestBody DetectionRecodePageQueryDto detectionRecodePageQueryDto) {
        return aiService.detectionRecodePageQuery(detectionRecodePageQueryDto);
    }

    @GetMapping(value = "/detectionRecodeDetail/{id}")
    @Operation(summary = "AI诊断记录详情接口")
    public Result<DetectionRecordDetail> detectionRecodeDetail(@PathVariable Long id) {
        return aiService.detectionRecodeDetail(id);
    }

    @PostMapping(value = "/segment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "图像分割接口")
    public Result<SegmentImageResponse> segment(
            @RequestParam("image") MultipartFile image) {

        try {
            byte[] imageData = image.getBytes();
            return aiService.segment(imageData);
        } catch (Exception e) {
            return Result.error("图像分割失败: " + e.getMessage());
        }
    }
}
