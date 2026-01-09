package com.ai.medical_diagnosis.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ai.medical_diagnosis.constants.RedisConstants;
import com.ai.medical_diagnosis.domain.dto.AdminInfoDTO;
import com.ai.medical_diagnosis.domain.dto.AdminLoginDto;
import com.ai.medical_diagnosis.domain.dto.UserPageQueryDto;
import com.ai.medical_diagnosis.domain.po.AdminUser;
import com.ai.medical_diagnosis.domain.vo.AdminInfoVo;
import com.ai.medical_diagnosis.domain.vo.UseInfoVo;
import com.ai.medical_diagnosis.mapper.AdminMapper;
import com.ai.medical_diagnosis.mapper.UserMapper;
import com.ai.medical_diagnosis.result.PageResult;
import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.AdminService;
import com.ai.medical_diagnosis.utils.AdminHolder;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 *  管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public AdminServiceImpl(AdminMapper adminMapper,
                            UserMapper userMapper,
                            StringRedisTemplate stringRedisTemplate) {
        this.adminMapper = adminMapper;
        this.userMapper = userMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     *  管理员登录
     * @param adminLoginDto  管理员登录信息
     * @return 返回token
     */
    @Override
    public Result<String> login(AdminLoginDto adminLoginDto) {
        AdminUser adminUser = adminMapper.selectByUsername(adminLoginDto.getUsername());
        if (adminUser == null){
            return Result.error("用户不存在");
        }
        if (!adminUser.getPassword().equals(adminLoginDto.getPassword())){
            return Result.error("密码错误");
        }

        AdminInfoDTO adminInfoDTO = AdminHolder.getAdmin();
        if (adminInfoDTO != null){
            String oldToken = adminInfoDTO.getToken();
            if (oldToken != null) {
                stringRedisTemplate.delete(RedisConstants.LOGIN_ADMIN_KEY + oldToken);
            }
        }

        String token = UUID.randomUUID().toString();
        AdminInfoVo adminInfoVo = new AdminInfoVo();
        BeanUtil.copyProperties(adminUser, adminInfoVo);
        Map<String, Object> mapUser = BeanUtil.beanToMap(adminInfoVo)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() == null ? "" : e.getValue().toString()
                ));;
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_ADMIN_KEY + token, mapUser);
        return Result.success(token);
    }


    /**
     * 管理员注册
     * @param adminUser 管理员信息
     * @return 注册结果
     */
    @Override
    public Result<Boolean> register(AdminUser adminUser) {
        if (adminMapper.selectByUsername(adminUser.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        if (adminUser.getUsername() == null || adminUser.getUsername().length() < 3 || adminUser.getUsername().length() > 20) {
            return Result.error("用户名长度必须在3-20位之间");
        }
        if (adminUser.getPassword() == null || adminUser.getPassword().length() < 6 || adminUser.getPassword().length() > 20) {
            return Result.error("密码长度必须在6-20位之间");
        }
        adminUser.setCreateTime(LocalDateTime.now());
        adminMapper.insert(adminUser);
        return Result.success(true);
    }

    /**
     * 登出
     * @return  登出结果
     */
    @Override
    public Result<Boolean> logout() {
        AdminInfoDTO adminInfoDTO = AdminHolder.getAdmin();
        if (adminInfoDTO == null){
            return Result.error("用户未登录");
        }
        String oldToken = adminInfoDTO.getToken();
        if (oldToken != null) {
            stringRedisTemplate.delete(RedisConstants.LOGIN_ADMIN_KEY + oldToken);
        }else return Result.error("用户未登录");
        return Result.success(true);
    }

     /**
     * 删除用户
     * @param id 用户id
     * @return 删除结果
     */
    @Override
    public Result<Boolean> deleteUserbyId(Long id) {
        adminMapper.deleteById(id);
        return Result.success(true);
    }

     /**
     * 查询用户
     * @param dto 查询条件
     * @return 查询结果， 包含用户列表和总页数
     */
    @Override
    public Result<PageResult<UseInfoVo>> queryUser(UserPageQueryDto dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<UseInfoVo> page = userMapper.queryUser(dto);
        long total = page.getTotal();
        List<UseInfoVo> records = page.getResult();
        PageResult<UseInfoVo> result = new PageResult<>(total, records);
        return Result.success(result);
    }
}
