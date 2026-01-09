package com.ai.medical_diagnosis.utils.agentTools;

import com.ai.medical_diagnosis.domain.vo.SearchToolRequest;
import com.ai.medical_diagnosis.domain.vo.SearchToolResponse;
import com.ai.medical_diagnosis.utils.TextUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Component;

@Component
// 创建文档检索工具
public class DocumentSearchTool {

    private final TextUtils textUtils;

    public DocumentSearchTool( TextUtils textUtils) {
        this.textUtils = textUtils;
    }

    private SearchToolResponse search(SearchToolRequest request) {

        return SearchToolResponse.builder()
                .result(textUtils.VectorStoreSearch(request.getQuery(), request.getTopK()))
                .build();
    }
    public ToolCallback getCallback() {
        return FunctionToolCallback.builder("search_documents", this::search)
                .description("输入一个查询字符串，搜索文档以查找相关信息，返回TopK的数据，每个数据以换行相连，即返回一个拼接在一起的字符串")
                .inputType(SearchToolRequest.class)
                .build();
    }
}
