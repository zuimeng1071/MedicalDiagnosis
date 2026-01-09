package com.ai.medical_diagnosis.constants;

/**
 * AI 相关常量配置
 * 专用于皮肤镜图像中黑色素瘤的辅助诊断系统
 */
public class AiConstants {

    // ================== 会话记忆相关 ==================
    public static final String MEMORY_ID_KEY = "mds:memoryId:"; // 用户会话ID前缀
    public static final Integer MEMORY_TIMEOUT_MINUTES = 1000;       // 会话超时时间（天）
    public static final Integer MAX_MEMORY_SIZE = 10;              // 最大保留对话轮数

    // ================== AI 模型调用参数 ==================
    /**
     * 诊断建议模式：低随机性，强调准确性和一致性
     */
    public static final Double LOW_TEMPERATURE = 0.0;     // 低温度
    public static final Double DIAGNOSIS_TEMPERATURE = 0.5;     // 中温度
    public static final Double NORMAL_TEMPERATURE = 1.0;    // 正常温度
    public static final Double DIAGNOSIS_TOP_P = 0.8;           // 核采样
    public static final Integer DIAGNOSIS_MAX_TOKENS = 512;            // 诊断文本不宜过长

    /**
     * 模型生成自然语言解释
     */
    public static final Double EXPLANATION_TEMPERATURE = 1.0;   // 生成自然语言解释
    public static final Integer EXPLANATION_MAX_TOKENS = 768;

    // ================== 模型名称常量（可根据实际使用的模型调整）==================
    public static final String MODEL_QWEN_3_MAX = "qwen3-max";
    public static final String MODEL_QWEN_FLASH = "qwen-flash";
    public static final String MODEL_QWEN_3_VL_PLUS = "qwen3-vl-plus";
    public static final String MODEL_QWEN_3_VL_FLASH = "qwen3-vl-flash";

    public static final String MODEL_TEXT_EMBEDDING = "text-embedding-v4";

    public static final Integer MAX_IMAGE_SIZE = 5242880;
}