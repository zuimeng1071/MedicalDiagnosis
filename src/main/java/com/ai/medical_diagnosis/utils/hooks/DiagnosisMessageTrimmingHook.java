package com.ai.medical_diagnosis.utils.hooks;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.HookPosition;
import com.alibaba.cloud.ai.graph.agent.hook.HookPositions;
import com.alibaba.cloud.ai.graph.agent.hook.messages.AgentCommand;
import com.alibaba.cloud.ai.graph.agent.hook.messages.MessagesModelHook;
import com.alibaba.cloud.ai.graph.agent.hook.messages.UpdatePolicy;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;

@HookPositions({HookPosition.AFTER_MODEL}) // ✅ 修正这里
public class DiagnosisMessageTrimmingHook extends MessagesModelHook {

    private static final int MAX_MESSAGES_TO_KEEP = 2; // 保留首 + 尾，共2条

    @Override
    public String getName() {
        return "diagnosis_message_trimming";
    }

    @Override
    public AgentCommand afterModel(List<Message> previousMessages, RunnableConfig config) {
        if (previousMessages.size() <= MAX_MESSAGES_TO_KEEP) {
            return new AgentCommand(previousMessages);
        }

        Message first = previousMessages.get(0);
        Message last = previousMessages.get(previousMessages.size() - 1);

        List<Message> trimmed = new ArrayList<>();
        trimmed.add(first);
        trimmed.add(last);

        return new AgentCommand(trimmed, UpdatePolicy.REPLACE);
    }
}