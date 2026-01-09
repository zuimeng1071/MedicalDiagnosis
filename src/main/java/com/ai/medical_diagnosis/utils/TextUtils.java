package com.ai.medical_diagnosis.utils;

import com.ai.medical_diagnosis.constants.PythonConstants;
import com.ai.medical_diagnosis.domain.vo.TextSegmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TextUtils {

    private static final Logger log = LoggerFactory.getLogger(TextUtils.class);
    private final PythonHttpClient pythonHttpClient;
    private final VectorStore vectorStore;

    public TextUtils(PythonHttpClient pythonHttpClient, VectorStore vectorStore) {
        this.pythonHttpClient = pythonHttpClient;
        this.vectorStore = vectorStore;
    }

    /**
     * 将原始文本分割成多个段落，并转换为 Document 对象列表
     *
     * @param rawText 原始文本
     * @return Document 对象列表
     */
    public List<Document> segmentTextToDocuments(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 调用 Python 接口
        TextSegmentResponse response = pythonHttpClient.textSegmentClient(
                rawText,
                PythonConstants.PYTHON_TEXT_SEGMENT_URL,
                TextSegmentResponse.class
        );

        // 错误处理
        if (response == null) {
            throw new RuntimeException("调用文本分割服务失败：返回空响应");
        }
        if (response.getError() != null) {
            throw new RuntimeException("文本分割服务报错: " + response.getMessage());
        }

        // 转换为 Spring AI Document 列表
        return response.getParagraphs().stream()
                .map(Document::new)
                .collect(Collectors.toList());
    }

    public String VectorStoreSearch(String query, Integer TopK){
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                .query(query)
                .topK(TopK)
                .build());
        String retrievedText = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        log.info("检索到的文本: {}", retrievedText);
        return retrievedText;
    }
}
