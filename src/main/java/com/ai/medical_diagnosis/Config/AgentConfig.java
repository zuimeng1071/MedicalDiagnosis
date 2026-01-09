package com.ai.medical_diagnosis.Config;// package com.ai.medical_diagnosis.Config;

import com.ai.medical_diagnosis.domain.vo.ClassifyVo;
import com.ai.medical_diagnosis.utils.ResourceUtils;
import com.ai.medical_diagnosis.utils.agentTools.DocumentSearchTool;
import com.ai.medical_diagnosis.utils.hooks.AgentDebugHook;
import com.ai.medical_diagnosis.utils.hooks.DiagnosisMessageTrimmingHook;
import com.ai.medical_diagnosis.utils.hooks.MessageSummarizationHook;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SequentialAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.redis.RedisSaver;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class AgentConfig {

    private final RedisSaver redisSaver;

    private final DocumentSearchTool documentSearchTool;


    @Value("classpath:aiResourcesText/instruction/ChatAgent.txt")
    private Resource chatAgentInstructionFile;

    @Value("classpath:aiResourcesText/instruction/SearchAgent.txt")
    private Resource searchAgentInstructionFile;

    @Value("classpath:aiResourcesText/instruction/ClassificationAgent.txt")
    private Resource classificationAgentInstructionFile;

    @Value("classpath:aiResourcesText/instruction/DiagnosisSummaryAgent.txt")
    private Resource diagnosisSummaryAgentInstructionFile;

    @Value("classpath:aiResourcesText/instruction/ImageAnalysisAgent.txt")
    private Resource imageAnalysisAgentInstructionFile;

    public AgentConfig(RedisSaver redisSaver, DocumentSearchTool documentSearchTool) {
        this.redisSaver = redisSaver;
        this.documentSearchTool = documentSearchTool;
    }

    @jakarta.annotation.Resource
    @Qualifier("summaryModel")
    private ChatModel summaryModel;

    DiagnosisMessageTrimmingHook trimmingHook = new DiagnosisMessageTrimmingHook();

    // 纯文本模型
    @Bean("ChatAgent")
    public ReactAgent ChatAgent(@Qualifier("chatModel") ChatModel chatModel) {
        String systemInstruction = ResourceUtils.getSystemInstruction(chatAgentInstructionFile);
        return ReactAgent.builder()
                .name("chatModel")
                .model(chatModel)
//                .hooks(debugHook)
                .saver(redisSaver)
                .instruction(systemInstruction)
                .build();
    }

    // 搜索模型，联网搜索和rag数据库检索
    @Bean("SearchAgent")
    public ReactAgent SearchAgent(@Qualifier("searchModel") ChatModel chatModel) {
        String systemInstruction = ResourceUtils.getSystemInstruction(searchAgentInstructionFile);

        return ReactAgent.builder()
                .name("searchAgent")
                .model(chatModel)
//                .hooks(debugHook)
                .tools(documentSearchTool.getCallback())
                .instruction(systemInstruction)
                .outputKey("search_result")
                .returnReasoningContents(false)
                .build();
    }

    // 分类模型
    @Bean("ClassificationAgent")
    public ReactAgent ClassificationAgent(@Qualifier("classificationChatModel") ChatModel chatModel) {
        String systemInstruction = ResourceUtils.getSystemInstruction(classificationAgentInstructionFile);
        return ReactAgent.builder()
                .name("classificationAgent")
                .model(chatModel)
//                .hooks(debugHook)
                .outputType(ClassifyVo.class)
                .instruction(systemInstruction)
                .build();
    }

    // 带记忆的图像分析模型
    @Bean("DiagnosisSummaryAgent")
    public ReactAgent DiagnosisSummaryAgent(@Qualifier("imageDiagnosisChatModel") ChatModel chatModel) {
        String systemInstruction = ResourceUtils.getSystemInstruction(diagnosisSummaryAgentInstructionFile);
        return ReactAgent.builder()
                .name("DiagnosisSummaryAgent")
                .model(chatModel)
                .hooks(trimmingHook)
                .saver(redisSaver)
                .outputKey("diagnosis_summary")
                .returnReasoningContents(false) // 不暴露中间推理
                .instruction(systemInstruction)
                .build();
    }

    // 不带记忆的图像分析模型
    @Bean("ImageAnalysisAgent")
    public ReactAgent ImageAnalysisAgent(@Qualifier("imageDiagnosisChatModel") ChatModel chatModel) {
        String systemInstruction = ResourceUtils.getSystemInstruction(imageAnalysisAgentInstructionFile);
        return ReactAgent.builder()
                .name("ImageAnalysisAgent")
                .model(chatModel)
                .instruction(systemInstruction)
                .tools(documentSearchTool.getCallback())
                .outputKey("image_analysis") // 关键：命名输出，供后续引用
                .returnReasoningContents(false) // 不暴露中间推理
                .build();
    }

    // SequentialAgent 编排流程
    @Bean("MedicalDiagnosisWorkflow")
    public SequentialAgent medicalDiagnosisWorkflow(
            @Qualifier("ImageAnalysisAgent") ReactAgent imageAnalysisAgent,
            @Qualifier("DiagnosisSummaryAgent") ReactAgent diagnosisSummaryAgent) {

        return SequentialAgent.builder()
                .name("medical_diagnosis_workflow")
                .description("医学图像+文本联合诊断工作流:" +
                        "ImageAgent输出结构化数据并自主触发RAG → ChatAgent基于完整上下文生成结论")
                .subAgents(List.of(imageAnalysisAgent, diagnosisSummaryAgent))
                .build();
    }
}