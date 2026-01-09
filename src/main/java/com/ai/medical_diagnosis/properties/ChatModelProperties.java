package com.ai.medical_diagnosis.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mds.chat-model")
@Data
public class ChatModelProperties {
    private String baseUrl;
    private String apiKey;
    private String completionsPath;
    private Boolean logRequests;
    private Boolean logResponses;
}