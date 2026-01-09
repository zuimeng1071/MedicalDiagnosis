package com.ai.medical_diagnosis.mapper;


import com.ai.medical_diagnosis.domain.po.AdminUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    AdminUser selectByUsername(String username);

    void insert(AdminUser adminUser);

    void deleteById(Long id);
}
