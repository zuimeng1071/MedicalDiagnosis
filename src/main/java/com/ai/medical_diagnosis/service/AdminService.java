package com.ai.medical_diagnosis.service;


import com.ai.medical_diagnosis.domain.dto.AdminLoginDto;
import com.ai.medical_diagnosis.domain.dto.UserPageQueryDto;
import com.ai.medical_diagnosis.domain.po.AdminUser;
import com.ai.medical_diagnosis.domain.vo.UseInfoVo;
import com.ai.medical_diagnosis.result.PageResult;
import com.ai.medical_diagnosis.result.Result;

public interface AdminService {
    Result<String> login(AdminLoginDto adminLoginDto);

    Result<Boolean> register(AdminUser adminUser);

    Result<Boolean> logout();

    Result<Boolean> deleteUserbyId(Long id);

    Result<PageResult<UseInfoVo>> queryUser(UserPageQueryDto dto);
}
