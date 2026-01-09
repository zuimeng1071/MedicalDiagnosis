package com.ai.medical_diagnosis.domain.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecode {
    private Long chatRecodeId;  // 聊天记录id
    private Long UserId;    // 用户id
    private String memoryId;    // 记忆id
    private String firstQuestion;   // 用户第一个问题

    private LocalDateTime createTime;
    private LocalDateTime expirationTime; // 过期时间
}
