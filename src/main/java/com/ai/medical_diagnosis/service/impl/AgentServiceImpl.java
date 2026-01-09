package com.ai.medical_diagnosis.service.impl;

import com.ai.medical_diagnosis.domain.vo.ClassifyVo;
import com.ai.medical_diagnosis.service.AgentService;
import com.ai.medical_diagnosis.utils.UserHolder;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SequentialAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * agent 服务实现类
 */
@Service
@Slf4j
public class AgentServiceImpl implements AgentService {
    @Resource
    @Qualifier("ChatAgent")
    private ReactAgent chatAgent;

    @Resource
    @Qualifier("ClassificationAgent")
    private ReactAgent classificationAgent;

    @Resource
    @Qualifier("MedicalDiagnosisWorkflow")
    private SequentialAgent medicalDiagnosisWorkflow;

    @Resource
    private ObjectMapper objectMapper;

    @Value("classpath:aiResourcesText/system/chatAgentSystem.txt")
    private org.springframework.core.io.Resource systemInstructionFile;

    @Value("classpath:aiResourcesText/system/ClassificationAgentSystem.txt")
    private org.springframework.core.io.Resource ClassificationAgentSystemInstructionFile;

    @Value("classpath:aiResourcesText/system/diagnosisSystem.txt")
    private org.springframework.core.io.Resource diagnosisSystemInstructionFile;

    /**
     * 纯文本聊天
     *
     */
    @Override
    public AssistantMessage chatAgent(String question, String memoryId) throws GraphRunnerException {


        SystemMessage systemMessage = SystemMessage.builder()
                .text(systemInstructionFile)
                .build();

        UserMessage userMessage = UserMessage.builder()
                .text(question)
                .build();

        List<Message> messages = List.of(systemMessage, userMessage);

        // 使用 thread_id 维护对话上下文
        RunnableConfig config = RunnableConfig.builder()
                .threadId(memoryId)
                .build();

        AssistantMessage chatResponse = chatAgent
                .call(messages, config);
        return chatResponse;
    }

    /**
     * 图像分类
     * @param question
     * @return
     * @throws GraphRunnerException
     */
    @Override
    public ClassifyVo classifyAgent(String question, String imageUrl) throws GraphRunnerException {
        // 构建 SystemMessage
        SystemMessage systemMessage = SystemMessage.builder()
                .text(ClassificationAgentSystemInstructionFile)
                .build();

        // 构建 UserMessage
        UserMessage userMessage = UserMessage.builder()
                .media(Media.builder()
                        .mimeType(MimeTypeUtils.IMAGE_JPEG)
                        .data(URI.create(imageUrl))
                        .build()
                )
                .text(question)
                .build();

        List<Message> messages = List.of(systemMessage, userMessage);

        // 调用 Agent
        AssistantMessage chatResponse = classificationAgent.call(messages);
        String jsonResponse = chatResponse.getText();

        try {
            // 尝试直接解析为 ClassifyVo
            return objectMapper.readValue(jsonResponse, ClassifyVo.class);
        } catch (Exception e) {
            log.warn("LLM错误JSON返回: {}", jsonResponse, e);
            // 兜底数据 fallback
            return ClassifyVo.builder()
                    .classifyMap(Map.of("未知", 1.0f))
                    .build();
        }
    }


    /**
     * 带图片参数的聊天
     */
    @Override
    public AssistantMessage chatWithImageAgent(String question, String memoryId, List<String> urls) throws Exception {


        // 构建 RunnableConfig（用于会话记忆）
        RunnableConfig config = RunnableConfig.builder()
                .threadId(memoryId)
                .build();

        // 将每个 URL 转为 Media 对象（注意：必须是可访问的 URI）
        List<Media> mediaList = new ArrayList<>();
        for (String url : urls) {
            // 推荐：校验非空 & 支持多种格式（JPG/PNG）
            MimeType mimeType = getMimeTypeFromUrl(url); // 判断 MIME 类型
            mediaList.add(Media.builder()
                    .mimeType(mimeType)
                    .data(new URI(url))
                    .build());
        }

        // 构建 SystemMessage
        SystemMessage systemMessage = SystemMessage.builder()
                .text(diagnosisSystemInstructionFile)
                .build();

        // 构建 UserMessage，包含文本 + 多张图片
        UserMessage userMessage = UserMessage.builder()
                .text(question)
                .media(mediaList)
                .build();

        List<Message> messages = List.of(systemMessage, userMessage);

        // 调用 Agent
        return chatAgent.call(messages, config);
    }

    /**
     * 疾病诊断智能体
     * 用户输入文本（question） + 图像 → ImageAgent 分析（带RAG） → 再次查询向量数据库 → ChatAgent 总结
     *
     * @param question 输入问题
     * @param memoryId 记忆ID
     * @param urls     图像URL列表
     * @return 疾病诊断结果（封装为 AssistantMessage）
     * @throws Exception 抛出异常（如工作流失败、无输出等）
     */
    @Override
    public AssistantMessage diagnosisAgent(String question, String memoryId, List<String> urls) throws Exception {
        // 构建带图像的初始输入（Spring AI 支持 Media）
        List<Media> mediaList = urls.stream()
                .filter(Objects::nonNull)
                .map(url -> Media.builder()
                        .mimeType(getMimeTypeFromUrl(url))
                        .data(URI.create(url))
                        .build())
                .collect(Collectors.toList());

        SystemMessage systemMessage = SystemMessage.builder()
                .text(diagnosisSystemInstructionFile)
                .build();

        UserMessage userMessage = UserMessage.builder()
                .text(question)
                .media(mediaList)
                .build();

        List<Message> messages = List.of(systemMessage, userMessage);


        RunnableConfig config = RunnableConfig.builder()
                .threadId(memoryId)
                .build();

        // 执行工作流
        Optional<OverAllState> agentOutput = medicalDiagnosisWorkflow.invoke(messages, config);

        Object diagnosisResult = getObject(agentOutput);
        AssistantMessage resultMessage;

        if (diagnosisResult instanceof AssistantMessage) {
            resultMessage = (AssistantMessage) diagnosisResult;
        } else if (diagnosisResult instanceof String) {
            resultMessage = AssistantMessage.builder()
                    .content((String) diagnosisResult)
                    .build();
        } else {
            // 兜底：转为字符串
            resultMessage = AssistantMessage.builder()
                    .content(diagnosisResult.toString())
                    .build();
        }

        return resultMessage;
    }

    /**
     * 获取工作流执行结果
     * @param agentOutput 工作流执行结果
     * @return  工作流执行结果
     */
    @NotNull
    private static Object getObject(Optional<OverAllState> agentOutput) {
        if (agentOutput.isEmpty()) {
            throw new IllegalStateException("疾病诊断工作流执行失败，未返回有效状态");
        }

        OverAllState overAllState = agentOutput.get();

        // 获取 diagnosis_summary 输出
        Optional<Object> diagnosisResultOpt = overAllState.value("diagnosis_summary");

        if (diagnosisResultOpt.isEmpty()) {
            throw new IllegalStateException("工作流成功执行，但未找到 'diagnosis_summary' 输出字段，请检查 Agent 是否设置了 outputKey");
        }

        return diagnosisResultOpt.get();
    }

    /**
     * 获取图片的 MIME 类型
     * @param url 图片 URL
     * @return  MIME 类型
     */

    private MimeType getMimeTypeFromUrl(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.endsWith(".png")) {
            return MimeTypeUtils.IMAGE_PNG;
        } else if (lowerUrl.endsWith(".jpeg") || lowerUrl.endsWith(".jpg")) {
            return MimeTypeUtils.IMAGE_JPEG;
        } else if (lowerUrl.endsWith(".gif")) {
            return MimeTypeUtils.IMAGE_GIF;
        }
        // 默认 fallback
        return MimeTypeUtils.IMAGE_JPEG;
    }
}
