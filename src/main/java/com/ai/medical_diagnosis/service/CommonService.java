package com.ai.medical_diagnosis.service;


import com.ai.medical_diagnosis.result.Result;

public interface CommonService {
    Result<String> uploadImage(byte[] image);
}
