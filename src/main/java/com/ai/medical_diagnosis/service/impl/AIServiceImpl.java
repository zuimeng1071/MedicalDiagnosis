package com.ai.medical_diagnosis.service.impl;

import cn.hutool.core.lang.UUID;
import com.ai.medical_diagnosis.constants.AiConstants;
import com.ai.medical_diagnosis.constants.PythonConstants;
import com.ai.medical_diagnosis.domain.dto.ChatRecodeMessage;
import com.ai.medical_diagnosis.domain.dto.ChatRecodeMessages;
import com.ai.medical_diagnosis.domain.dto.ChatRecodePageQueryDto;
import com.ai.medical_diagnosis.domain.dto.DetectionRecodePageQueryDto;
import com.ai.medical_diagnosis.domain.po.ChatRecode;
import com.ai.medical_diagnosis.domain.po.DetectionRecordDetail;
import com.ai.medical_diagnosis.domain.po.DetectionRecordSummary;
import com.ai.medical_diagnosis.domain.vo.ClassifyVo;
import com.ai.medical_diagnosis.domain.vo.DetectionRecordDetailVo;
import com.ai.medical_diagnosis.domain.vo.SegmentImageResponse;
import com.ai.medical_diagnosis.mapper.AiChatMapper;
import com.ai.medical_diagnosis.mapper.DetectionRecordMapper;
import com.ai.medical_diagnosis.result.PageResult;
import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.AIService;
import com.ai.medical_diagnosis.service.AgentService;
import com.ai.medical_diagnosis.utils.PythonHttpClient;
import com.ai.medical_diagnosis.utils.ImageUtils;
import com.ai.medical_diagnosis.utils.UserHolder;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.Checkpoint;
import com.alibaba.cloud.ai.graph.checkpoint.savers.redis.RedisSaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * AIServiceImpl ai服务实现类，包含图像分割、图像分类、图像识别、Agent模型调用
 */
@Service
@Slf4j
public class AIServiceImpl implements AIService {

    private final AgentService agentService;
    private final ImageUtils imageUtils;
    private final PythonHttpClient pythonHttpClient;
    private final DetectionRecordMapper detectionRecordMapper;
    private final AiChatMapper aiChatMapper;
    private final RedisSaver redisSaver;
    private final ObjectMapper objectMapper;

    public AIServiceImpl(AgentService agentService,
                         ImageUtils imageUtils,
                         PythonHttpClient pythonHttpClient,
                         DetectionRecordMapper detectionRecordMapper,
                         AiChatMapper aiChatMapper,
                         RedisSaver redisSaver, ObjectMapper objectMapper) {
        this.agentService = agentService;
        this.imageUtils = imageUtils;
        this.pythonHttpClient = pythonHttpClient;
        this.detectionRecordMapper = detectionRecordMapper;
        this.aiChatMapper = aiChatMapper;
        this.redisSaver = redisSaver;
        this.objectMapper = objectMapper;
    }

    /**
     * 对图像进行分割。
     * @param image 输入的图像字节数组。
     * @return 包含分割结果的Result对象。
     */
    @Override
    public Result<SegmentImageResponse> segment(byte[] image) {
        // 调用 Python 分割接口
        SegmentImageResponse resp = pythonHttpClient.ImageClient(
                image, PythonConstants.PYTHON_SEGMENT_URL, SegmentImageResponse.class);
        if (resp == null) return Result.error("分割失败");

        return Result.success(resp);
    }
    /**
     * 执行AI诊断，包括图像分类、分割，并基于这些信息生成诊断报告。
     * @param detectionRecordDetailVo 输入的图像信息。
     *                                包含图像URL、年龄、性别、症状描述等信息。
     * @return 诊断结果。
     */
    @Override
    public Result<DetectionRecordDetail> diagnosis(DetectionRecordDetailVo detectionRecordDetailVo) {
        try {
            // 通过URL读取图像
            byte[] image = imageUtils.downloadWithHttpClient(detectionRecordDetailVo.getImageUrl());

            // 创建 Future，异步调用分类接口
            CompletableFuture<ClassifyVo> classifyFuture = CompletableFuture.supplyAsync(() ->
                    {
                        try {
                            return classify(detectionRecordDetailVo.getImageUrl());
                        } catch (GraphRunnerException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    );

            // 创建 Future，异步调用分割接口
            CompletableFuture<SegmentImageResponse> segmentFuture = CompletableFuture.supplyAsync(() ->
                    pythonHttpClient.ImageClient(image, PythonConstants.PYTHON_SEGMENT_URL, SegmentImageResponse.class)
            );


            // 等待完成，带超时
            ClassifyVo classifyResp = classifyFuture.get(30, TimeUnit.SECONDS);
            SegmentImageResponse segmentResp = segmentFuture.get(10, TimeUnit.SECONDS);

            if (segmentResp == null) {
                return Result.error("AI 分析失败：分割返回空");
            }

            // 提取 Base64 图像
            String base64MergedImage = segmentResp.getMergedImage();
            String base64BinaryMask = segmentResp.getBinaryMask();

            // 图像转换为 OSS URL
            String imgUrl = detectionRecordDetailVo.getImageUrl();
            String mergedImageUrl = imageUtils.ImageBase64ToOssUrl(base64MergedImage);
            String binaryMaskUrl = imageUtils.ImageBase64ToOssUrl(base64BinaryMask);

            // 构造memoryId
            Long userId = UserHolder.getUser().getUserId();
            String memoryId = System.currentTimeMillis() + "_" +
                    UUID.randomUUID().toString().substring(0, 8);

            String userMemoryId = userId + "_" + memoryId;

            // 传入模型的图像，包括原始图像、分割图像、二值掩码
            List<String> urls = List.of(imgUrl, mergedImageUrl, binaryMaskUrl);

            // 创建模型输入
            // 构造 classifyInfo 为格式化的 JSON 字符串
            String classifyInfo;
            if (classifyResp != null && classifyResp.getClassifyMap() != null) {
                try {
                    // 将 classifyMap 转为JSON（可读性好）
                    classifyInfo = objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(classifyResp.getClassifyMap());
                } catch (JsonProcessingException e) {
                    classifyInfo = "解析分类结果失败";
                }
            } else {
                classifyInfo = "{\n}";
            }

            String question = """
            病人信息：
             - age：%d 岁
            - gender：%s
            - description：%s
            
            classify_info：
            %s
            请结合上传的医学图像（含AI分割与标注结果）及后续检索到的医学证据，生成专业诊断报告。
            """.formatted(
                    detectionRecordDetailVo.getAge() != null ? detectionRecordDetailVo.getAge() : 0,
                    detectionRecordDetailVo.getGender() != null ? detectionRecordDetailVo.getGender() : "未知",
                    detectionRecordDetailVo.getDescription() != null ? detectionRecordDetailVo.getDescription() : "无",
                    classifyInfo
            );

            // 模型调用
            AssistantMessage assistantMessage = agentService.diagnosisAgent(
                    question,
                    userMemoryId,
                    urls
            );

            // TODO 异步保存
            // 保存诊断记录
            DetectionRecordDetail record = DetectionRecordDetail.builder()
                    .detectionRecordId(null) // 数据库生成
                    .userId(userId)
                    .memoryId(memoryId)
                    .description(detectionRecordDetailVo.getDescription()) // 初始化为空字符串
                    .classify(classifyInfo)
                    .ImgUrl(imgUrl)
                    .mergedImageUrl(mergedImageUrl)
                    .binaryMaskUrl(binaryMaskUrl)
                    .analysisResult(assistantMessage.getText())
                    .createTime(LocalDateTime.now())
                    .build();
            detectionRecordMapper.insertDetail(record);

            // 保存简要记录

            // 从 classifyResp 中提取分类得分最高的类别名称（key）
            String maxClassify = (classifyResp != null && classifyResp.getClassifyMap() != null) ?
                    // 将 classifyMap 的所有键值对（Entry）转换为 Stream 流，便于函数式处理
                    classifyResp.getClassifyMap().entrySet().stream()
                            // 使用 Map.Entry.comparingByValue() 比较器，按 value（即分类得分）进行自然升序比较，
                            // .max() 则找出 value 最大的那个 Entry（即得分最高的分类）
                            .max(Map.Entry.comparingByValue())
                            // 如果找到了最大值的 Entry，则提取其 key（即分类名称）
                            .map(Map.Entry::getKey)
                            // 如果 map 为空或没有找到有效项，则返回空字符串作为默认值
                            .orElse("")
                    // 如果 classifyResp 或 classifyMap 为 null，直接返回空字符串，避免空指针异常
                    : "";

            DetectionRecordSummary summary = DetectionRecordSummary.builder()
                    .detectionRecordId(record.getDetectionRecordId())
                    .userId(UserHolder.getUser().getUserId())
                    .classify(maxClassify)
                    .diagnosis(safeSubstring(assistantMessage.getText(), 50))
                    .createTime(LocalDateTime.now())
                    .build();
            detectionRecordMapper.insertSummary(summary);

            // 保存聊天记录
            ChatRecode chatRecode = ChatRecode.builder()
                    .chatRecodeId(null)
                    .UserId(userId)
                    .memoryId(memoryId)
                    .firstQuestion(safeSubstring(question, 10))
                    .createTime(LocalDateTime.now())
                    .expirationTime(LocalDateTime.now().plusDays(AiConstants.MEMORY_TIMEOUT_MINUTES))
                    .build();

            aiChatMapper.insert(chatRecode);

            return Result.success(record);

        } catch (TimeoutException e) {
            return Result.error("服务响应超时");
        } catch (Exception e) {
            return Result.error("诊断失败：" + e.getMessage());
        }
    }

    /**
     * 获取图像检测记录的详细信息。
     * @param id 检测记录ID。
     * @return 检测记录的详细信息。
     */
    @Override
    public Result<DetectionRecordDetail> detectionRecodeDetail(Long id) {
        if (id == null){
            return Result.error("请提供有效的检测记录ID");
        }
        // 获取详细信息
        DetectionRecordDetail record = detectionRecordMapper.selectById(id);
        if (record == null) {
            return Result.error("未找到该检测记录");
        }
        return Result.success(record);
    }

    /**
     * 聊天服务，纯文本
     * 最好船memoryId给前端
     * @param memoryId 记忆ID
     * @param question 问题
     * @return 聊天结果
     */
    @Override
    public Result<String> chat(String memoryId, String question) throws GraphRunnerException {
        Long userId = UserHolder.getUser().getUserId();

        if (memoryId.isEmpty()){
            memoryId = java.util.UUID.randomUUID().toString();
        }

        String userMemoryId = userId + "_" + memoryId;
        if (memoryId == null){
            return Result.error("请提供有效的记忆ID");
        }else if (question == null){
            return Result.error("请提供有效的问题");
        }
        if (aiChatMapper.selectByUserIdAndMemoryId(userId, memoryId) == null){
            // 创建聊天记录
            ChatRecode chatRecode = ChatRecode.builder()
                    .chatRecodeId(null)
                    .UserId(userId)
                    .memoryId(memoryId)
                    .firstQuestion(question)
                    .createTime(LocalDateTime.now())
                    .expirationTime(LocalDateTime.now().plusDays(AiConstants.MEMORY_TIMEOUT_MINUTES))
                    .build();

            aiChatMapper.insert(chatRecode);
        }
        return Result.success(agentService.chatAgent(question, userMemoryId).getText());
    }

    /**
     * 聊天记录分页查询
     * @param dto 查询参数
     * @return 聊天记录分页结果
     */
    @Override
    public Result<PageResult<ChatRecode>> chatRecodePageQuery(ChatRecodePageQueryDto dto) {

        log.info("用户发起了聊天记录查询请求{}", dto);

        Long userId = UserHolder.getUser().getUserId();

        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        LocalDateTime now = LocalDateTime.now();
        Page<ChatRecode> page = aiChatMapper.query(userId, now);
        log.info("用户查询的聊天记录结果{}", page);

        List<ChatRecode> records = page.getResult();
        long total = records.size();
        PageResult<ChatRecode> pageResult = new PageResult<>(total, records);
        return Result.success(pageResult);
    }

    /**
     * 获取聊天记录。
     * @param memoryId 记录的记忆ID。
     * @return 检测记录的json。
     */
    @Override
    public Result<ChatRecodeMessages> chatRecodeDetail(String memoryId) {

        // 获取用户ID
        Long userId = UserHolder.getUser().getUserId();
        String userMemoryId = userId + "_" + memoryId;

        // 获取会话状态
        RunnableConfig config = RunnableConfig.builder()
                .threadId(userMemoryId)
                .build();

        Optional<Checkpoint> checkpointOpt = redisSaver.get(config);

        if (checkpointOpt.isEmpty()) {
            return Result.success(null);
        }

        Checkpoint checkpoint = checkpointOpt.get();

        // 从 state 中取出 messages
        Map<String, Object> state = checkpoint.getState();
        Object messagesObj = state.get("messages");

        if (!(messagesObj instanceof List)) {
            return Result.success(null);
        }

        List<Message> messages = (List<Message>) messagesObj;

        List<Message> messagesWithoutFirst;
        if (messages.isEmpty()) {
            messagesWithoutFirst = List.of(); // 空列表
        } else {
            messagesWithoutFirst = messages.subList(1, messages.size());
        }

        // 转换为前端友好的格式
        List<ChatRecodeMessage> result = messagesWithoutFirst.stream()
                .map(msg -> {
                    ChatRecodeMessage vo = new ChatRecodeMessage();
                    // 角色：USER -> "user", ASSISTANT -> "assistant"
                    vo.setRole(msg.getMessageType().name().toLowerCase());
                    vo.setContent(msg.getText()); // getText() 是通用方法
                    return vo;
                })
                .collect(Collectors.toList());

        return Result.success(ChatRecodeMessages.builder()
                .memoryId(memoryId)
                .messages(result)
                .build());
    }

    /**
     * 分类图像。
     * 后续可以换为分类模型分类
     * @param imageUrl 图像的URL。
     * @return 分类结果。
     */
    @Override
    public ClassifyVo classify(String imageUrl) throws GraphRunnerException {

        String question = "请对图像进行分类";
        return agentService.classifyAgent(question, imageUrl);
    }

    /**
     * 删除聊天记录。
     * @param memoryId 记录的记忆ID。
     * @return 删除结果。
     */
    @Override
    public Result<String> chatRecodeDelete(String memoryId) {
        Long userId = UserHolder.getUser().getUserId();
        aiChatMapper.deleteByUserIdAndMemoryId(userId, memoryId);

        return Result.success("删除成功");
    }

    /**
     * 查询图像检测记录。
     * @param dto 查询参数。
     * @return 包含检测记录的Result对象。
     */
    @Override
    public Result<PageResult<DetectionRecordSummary>> detectionRecodePageQuery(DetectionRecodePageQueryDto dto) {
        Long userId = UserHolder.getUser().getUserId();
        PageHelper.startPage(dto.getPage(), dto.getPageSize());;
        Page<DetectionRecordSummary> page = detectionRecordMapper.query(dto, userId);

        List<DetectionRecordSummary> records = page.getResult();
        long total = records.size();
        PageResult<DetectionRecordSummary> result = new PageResult<>(total, records);
        return Result.success(result);
    }


    /**
     * 工具方法：安全截取字符串（可放在工具类中，如 StringUtils）
     * @param str 待截取的字符串
     * @param maxLength 最大长度
     * @return 截取后的字符串
     */
    private static String safeSubstring(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }

}
