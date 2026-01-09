package com.ai.medical_diagnosis.utils;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

/**
 * 静态资源工具类
 */
@Component
public class ResourceUtils {

    public static String getSystemInstruction(Resource File) {
        try {
            // 读取整个文件为字符串（UTF-8 编码）
            return StreamUtils.copyToString(File.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("无法读取系统提示词文件", e);
        }
    }

}
