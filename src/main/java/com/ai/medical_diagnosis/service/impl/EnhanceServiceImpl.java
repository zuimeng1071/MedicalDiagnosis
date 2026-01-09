package com.ai.medical_diagnosis.service.impl;


import com.ai.medical_diagnosis.constants.PythonConstants;
import com.ai.medical_diagnosis.domain.dto.EnhancedImageDto;
import com.ai.medical_diagnosis.service.EnhanceService;
import com.ai.medical_diagnosis.utils.PythonHttpClient;
import org.springframework.stereotype.Service;

/**
 * 增强服务实现类，通过调用Python脚本实现图片增强
 * 暂不使用
 */
@Service
public class EnhanceServiceImpl implements EnhanceService {

    private final PythonHttpClient pythonHttpClient;

    public EnhanceServiceImpl(PythonHttpClient pythonHttpClient) {
        this.pythonHttpClient = pythonHttpClient;
    }

    @Override
    public String basic(byte[] imageData) {
         return getEnhancedImage(imageData, PythonConstants.ENHANCE_BASIC_URL);
    }

    @Override
    public String pca(byte[] imageData) {
        return getEnhancedImage(imageData, PythonConstants.ENHANCE_PCA_URL);
    }

    @Override
    public String applyAll(byte[] imageData) {
        return getEnhancedImage(imageData, PythonConstants.ENHANCE_APPLY_ALL_URL);
    }

    private String getEnhancedImage(byte[] imageData, String url) {
        EnhancedImageDto enhancedImageDto = pythonHttpClient.ImageClient(imageData, url, EnhancedImageDto.class);
        if (enhancedImageDto != null){
            return enhancedImageDto.getEnhancedImage();
        }
        return null;
    }

}
