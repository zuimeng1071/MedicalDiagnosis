package com.ai.medical_diagnosis.service;


import com.ai.medical_diagnosis.domain.dto.UserLoginDto;
import com.ai.medical_diagnosis.domain.po.User;
import com.ai.medical_diagnosis.domain.vo.UseInfoVo;
import com.ai.medical_diagnosis.result.Result;

public interface UserService {
    Result<String> login(UserLoginDto userLoginDto);

    Result<Boolean> register(User user);

    Result<Boolean> logout();

    Result<UseInfoVo> updateUser(User user);

    Result<UseInfoVo> getUserInfo();
}
