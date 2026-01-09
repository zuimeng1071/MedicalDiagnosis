package com.ai.medical_diagnosis.mapper;


import com.ai.medical_diagnosis.domain.dto.UserPageQueryDto;
import com.ai.medical_diagnosis.domain.po.User;
import com.ai.medical_diagnosis.domain.vo.UseInfoVo;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    void insert(User user);

    void update(User user);

    Page<UseInfoVo> queryUser(UserPageQueryDto dto);
}
