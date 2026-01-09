package com.ai.medical_diagnosis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai.redis")
@Data
public class RedisProperties {
    private String host;
    private int port;
}
