package com.ai.medical_diagnosis.Config;// package com.ai.medical_diagnosis.Config;

import com.ai.medical_diagnosis.constants.AiConstants;
import com.ai.medical_diagnosis.properties.ChatModelProperties;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelConfig {

    private final ChatModelProperties chatModelProperties;

    public ChatModelConfig(ChatModelProperties chatModelProperties) {
        this.chatModelProperties = chatModelProperties;
    }


    @Bean("chatModel")
    public ChatModel chatModel() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder()
                        .apiKey(chatModelProperties.getApiKey())
                        .completionsPath(chatModelProperties.getCompletionsPath())
                        .build())
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(AiConstants.MODEL_QWEN_3_VL_FLASH)
                        .temperature(AiConstants.EXPLANATION_TEMPERATURE)
                        .build())
                .build();
    }

    @Bean("summaryModel")
    public ChatModel summaryModel () {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder()
                        .apiKey(chatModelProperties.getApiKey())
                        .build())
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(AiConstants.MODEL_QWEN_FLASH)
                        .temperature(AiConstants.EXPLANATION_TEMPERATURE)
                        .build())
                .build();
    }

    @Bean("searchModel")
    public ChatModel searchModel() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder()
                        .apiKey(chatModelProperties.getApiKey())
                        .build())
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(AiConstants.MODEL_QWEN_FLASH)
                        .temperature(AiConstants.EXPLANATION_TEMPERATURE)
                        .enableSearch(true)
                        .build())
                .build();
    }

    @Bean("classificationChatModel")
    public ChatModel classificationChatModel() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder()
                        .apiKey(chatModelProperties.getApiKey())
                        .completionsPath(chatModelProperties.getCompletionsPath())
                        .build())
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(AiConstants.MODEL_QWEN_3_VL_FLASH)
                        .temperature(AiConstants.LOW_TEMPERATURE)
                        .build())
                .build();
    }

    @Bean("imageDiagnosisChatModel")
    public ChatModel imageDiagnosisChatModel() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder()
                        .apiKey(chatModelProperties.getApiKey())
                        .completionsPath(chatModelProperties.getCompletionsPath())
                        .build())
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(AiConstants.MODEL_QWEN_3_VL_FLASH)
                        .temperature(AiConstants.DIAGNOSIS_TEMPERATURE)
                        .build())
                .build();
    }
}