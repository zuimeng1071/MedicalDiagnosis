package com.ai.medical_diagnosis.domain.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AIDiagnosisResultVO {

    private AiMessage aiMessage;
    private Metadata metadata;

    @Data
    public static class AiMessage {
        private String text;
        private String thinking;
        private JSONObject attributes;
        private List<ToolExecutionRequestVO> toolExecutionRequests;
    }

    @Data
    public static class Metadata {
        private String id;
        private String modelName;
        private TokenUsage tokenUsage;
        private String finishReason;
    }

    @Data
    public static class TokenUsage {
        private int inputTokenCount;
        private int outputTokenCount;
        private int totalTokenCount;
    }

    @Data
    public static class ToolExecutionRequestVO {
         private String name;
         private String arguments;
         private Map<String, Object> argumentsMap;
    }
}