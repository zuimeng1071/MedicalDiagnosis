package com.ai.medical_diagnosis.mapper;


import com.ai.medical_diagnosis.domain.po.ChatRecode;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface AiChatMapper {
    void insert(ChatRecode chatRecode);

    @Select("select * from chat_recode where user_id = #{userId} and expiration_time > #{now}")
    Page<ChatRecode> query(Long userId, LocalDateTime now);

    Integer chatStatistics(LocalDateTime startTime, LocalDateTime endTime);

    @Select("select * from chat_recode where user_id = #{userId} and memory_id = #{memoryId}")
    ChatRecode selectByUserIdAndMemoryId(Long userId, String memoryId);

    @Delete("delete from chat_recode where user_id = #{userId} and memory_id = #{memoryId}")
    void deleteByUserIdAndMemoryId(Long userId, String memoryId);
}
