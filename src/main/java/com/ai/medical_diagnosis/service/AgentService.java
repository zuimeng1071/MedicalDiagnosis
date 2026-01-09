package com.ai.medical_diagnosis.service;

import com.ai.medical_diagnosis.domain.vo.ClassifyVo;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.List;

public interface AgentService {
    AssistantMessage chatAgent(String question, String memoryId) throws GraphRunnerException;

    ClassifyVo classifyAgent(String question, String imageUrl) throws GraphRunnerException;

    AssistantMessage chatWithImageAgent(String question, String memoryId, List<String> urls) throws Exception;

    AssistantMessage diagnosisAgent(String question, String memoryId, List<String> urls) throws Exception;
}
