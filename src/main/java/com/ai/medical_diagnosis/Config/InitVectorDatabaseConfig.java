package com.ai.medical_diagnosis.Config;

import cn.hutool.crypto.SecureUtil;
import com.ai.medical_diagnosis.constants.RedisConstants;
import com.ai.medical_diagnosis.utils.TextUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
@Slf4j
public class InitVectorDatabaseConfig {

    private final VectorStore vectorStore;
    private final RedisTemplate<String, String> redisTemplate;
    private final TextUtils textUtils;

    @Value("classpath:aiResourcesText/ragText/RAGOrgText.txt")
    private Resource opsFile;

    public InitVectorDatabaseConfig(VectorStore vectorStore,
                                    RedisTemplate<String, String> redisTemplate,
                                    TextUtils textUtils) {
        this.vectorStore = vectorStore;
        this.redisTemplate = redisTemplate;
        this.textUtils = textUtils;
    }

    /**
     * 应用启动时初始化向量数据库（仅当未初始化过时）
     */
    @PostConstruct
    public void init() {
        TextReader textReader = new TextReader(opsFile);
        textReader.setCharset(Charset.defaultCharset());

        String sourceMetadata = (String) textReader.getCustomMetadata().get("source");
        String textHash = SecureUtil.md5(sourceMetadata);
        String redisKey = RedisConstants.REDIS_VECTOR_KEY + textHash;

        Boolean retFlag = redisTemplate.opsForValue().setIfAbsent(redisKey, "1");
        log.info("redisKey: {}", redisKey);

        if (Boolean.TRUE.equals(retFlag)) {
            log.info("向量初始化数据不存在，开始初始化...");
            initVectorStore(textReader); // 委托给专用方法
            log.info("向量数据库初始化完成！");
        } else {
            log.info("向量初始化数据已存在，跳过重复加载");
        }
    }

    /**
     * 实际执行向量库初始化的逻辑
     */
    private void initVectorStore(TextReader textReader) {
        String fullText = textReader.read().toString();
        List<Document> list = textUtils.segmentTextToDocuments(fullText);

        if (list == null || list.isEmpty()) {
            log.warn("分割后的文档列表为空，跳过向量初始化");
            return;
        }

        log.info("检测到新数据源，开始分批加载 {} 个文档到向量数据库...", list.size());

        int batchSize = 5; // 批次大小，过大会报错
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            List<Document> batch = list.subList(i, end);
            log.info("正在添加第 {} - {} 个文档", i + 1, end);
            vectorStore.add(batch);
        }
    }
}