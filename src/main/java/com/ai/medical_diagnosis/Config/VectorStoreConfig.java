package com.ai.medical_diagnosis.Config;


import com.ai.medical_diagnosis.constants.AiConstants;
import com.ai.medical_diagnosis.properties.RedisProperties;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

/**
 * 创建向量数据库
 */
@Configuration
public class VectorStoreConfig {

    private final RedisProperties redisProperties;


    public VectorStoreConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    /**
     * 创建Redis连接池
     * @return Redis连接池
     */
    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled(
                redisProperties.getHost(),
                redisProperties.getPort());
    }

    /**
     * 创建向量数据库
     * @param jedisPooled Redis连接池
     * @param embeddingModel 向量模型
     * @return 向量数据库
     */
    @Bean
    public VectorStore vectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("custom-index")                // 可选：默认为 "spring-ai-index"
                .prefix("custom-prefix")                  // 可选：默认为 "embedding:"
                .metadataFields(                         // 可选：定义用于过滤的元数据字段
                        RedisVectorStore.MetadataField.tag("country"),
                        RedisVectorStore.MetadataField.numeric("year"))
                .initializeSchema(true)                   // 可选：默认为 false
                .batchingStrategy(new TokenCountBatchingStrategy()) // 可选：默认为 TokenCountBatchingStrategy
                .build();
    }

    // 这可以是任何 EmbeddingModel 实现
    @Bean
    public EmbeddingModel embeddingModel() {

        return new DashScopeEmbeddingModel(
                DashScopeApi.builder()
                        .apiKey(System.getenv("AI_DASHSCOPE_API_KEY"))
//                        .baseUrl()
                        .build(),
                MetadataMode.EMBED,
                DashScopeEmbeddingOptions.builder()
                        .model(AiConstants.MODEL_TEXT_EMBEDDING)
                        .textType(DashScopeModel.EmbeddingTextType.DOCUMENT.getValue())
                        .build());
    }
}
