package com.ai.medical_diagnosis.Config;

import com.alibaba.cloud.ai.graph.checkpoint.savers.redis.RedisSaver;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RedisSaver 配置
 */
@Configuration
public class RedisSaverConfig {
    private final RedissonClient redissonClient;

    public RedisSaverConfig(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Bean
    public RedisSaver redisSaver() {
        return RedisSaver.builder()
                .redisson(redissonClient)
                .build();
    }
}
