package com.ai.medical_diagnosis.constants;

public class PythonConstants {
    public static String PYTHON_PATH = "http://127.0.0.1:5000";
    // python服务-ai模型
    public static String PYTHON_SEGMENT_URL = PYTHON_PATH + "/segment"; // 图像分割
    public static String PYTHON_SEGMENT_MEDSAM_URL = PYTHON_PATH +"/segment-medsam";
    public static String PYTHON_CLASSIFY_URL = PYTHON_PATH + "/classify";   //  分类
    public static String PYTHON_TEXT_SEGMENT_URL = PYTHON_PATH + "/text-segment";   // 文本分词

    // python服务-图像增强
    public static String ENHANCE_PCA_URL = PYTHON_PATH + "/enhance/pca";
    public static String ENHANCE_BASIC_URL = PYTHON_PATH + "/enhance/basic";
    public static String ENHANCE_APPLY_ALL_URL = PYTHON_PATH + "/enhance/all";

}
