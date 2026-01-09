package com.ai.medical_diagnosis.service;

public interface EnhanceService {
    String basic(byte[] imageData);

    String pca(byte[] imageData);

    String applyAll(byte[] imageData);
}
