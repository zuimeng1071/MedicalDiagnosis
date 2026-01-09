package com.ai.medical_diagnosis.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;

import com.ai.medical_diagnosis.constants.RedisConstants;
import com.ai.medical_diagnosis.domain.dto.UserInfoDTO;
import com.ai.medical_diagnosis.domain.dto.UserLoginDto;
import com.ai.medical_diagnosis.domain.po.User;
import com.ai.medical_diagnosis.domain.vo.UseInfoVo;
import com.ai.medical_diagnosis.mapper.UserMapper;
import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.UserService;
import com.ai.medical_diagnosis.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public UserServiceImpl(UserMapper userMapper,
                           StringRedisTemplate stringRedisTemplate) {
        this.userMapper = userMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 登录
     *
     * @param userLoginDto 用户登录信息
     * @return 登录结果
     */
    @Override
    public Result<String> login(UserLoginDto userLoginDto) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(userLoginDto.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 2. 校验密码
        if (!user.getPassword().equals(userLoginDto.getPassword())) {
            return Result.error("密码错误");
        }
        UseInfoVo userInfoVo = new UseInfoVo();
        BeanUtil.copyProperties(user, userInfoVo);

        // 3. 保存用户信息到redis中，生成的token作为key，用户信息作为value
        // 3.1 删除旧token，生成token
        UserInfoDTO userInfoDTO = UserHolder.getUser();
        if (userInfoDTO != null){
            String oldToken = userInfoDTO.getToken();
            if (oldToken != null) {
                stringRedisTemplate.delete(RedisConstants.LOGIN_USER_KEY + oldToken);
            }
        }

        String token = UUID.randomUUID().toString();
        // 3.2 用户信息转为Map<Object, Object>, 保存到redis中
        Map<String, Object> mapUser = BeanUtil.beanToMap(userInfoVo)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() == null ? "" : e.getValue().toString()
                ));
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY + token, mapUser);
        // 3.3 设置token的TTL
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token,
                RedisConstants.LOGIN_USER_TTL,
                TimeUnit.MINUTES);

        // 4. 返回结果
        return Result.success(token);
    }

    /**
     * 注册
     *
     * @param user 用户信息
     * @return 注册结果
     */
    @Override
    public Result<Boolean> register(User user) {
        // 1. 校验用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        // 2. 校验信息合法性
        if (user.getUsername() == null || user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            return Result.error("用户名长度必须在3-20位之间");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6 || user.getPassword().length() > 20) {
            return Result.error("密码长度必须在6-20位之间");
        }
        log.info("用户注册：{}", user);
        user.setCreateTime(LocalDateTime.now());
        // 3. 保存用户
        try {
            userMapper.insert(user);
        }
        catch (Exception e){
            log.error("用户注册失败：{}", e.getMessage());
        }

        return Result.success(true);
    }

    /**
     * 登出
     *
     * @return 登出结果
     */
    @Override
    public Result<Boolean> logout() {
        // 删除token
        UserInfoDTO userInfoDTO = UserHolder.getUser();
        if (userInfoDTO == null){
            return Result.error("用户未登录");
        }
        String token = userInfoDTO.getToken();
        if (token != null) {
            stringRedisTemplate.delete(RedisConstants.LOGIN_USER_KEY + token);
        }else {
            return Result.error("用户未登录");
        }
        return Result.success(true);
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新结果
     */
    @Override
    public Result<UseInfoVo> updateUser(User user) {
        // 1. 校验用户是否存在
        UserInfoDTO userInfoDTO = UserHolder.getUser();
        if (userMapper.selectByUsername(userInfoDTO.getUsername()) == null) {
            return Result.error("用户不存在");
        }
        // 2. 校验信息合法性
        // 2.1 用户名是否重复
        if (!Objects.equals(userInfoDTO.getUsername(), user.getUsername()) && userMapper.selectByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        if (user.getUsername() == null || user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            return Result.error("用户名长度必须在3-20位之间");
        }
//        if (user.getEmail() != null && !user.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
//            return Result.error("邮箱格式错误");
//        }
//        if (user.getPhone() != null && !user.getPhone().matches("^1[3-9]\\d{8}$")) {
//            return Result.error("手机号格式错误");
//        }

        // 3. 更新用户信息
        userMapper.update(user);
        UseInfoVo userInfoVo = new UseInfoVo();
        BeanUtil.copyProperties(user, userInfoVo);
        return Result.success(userInfoVo);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @Override
    public Result<UseInfoVo> getUserInfo() {
        // 1. 获取当前用户
        UserInfoDTO userInfoDTO = UserHolder.getUser();
        User user = userMapper.selectByUsername(userInfoDTO.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }
        UseInfoVo userInfoVo = new UseInfoVo();
        BeanUtil.copyProperties(user, userInfoVo);
        return Result.success(userInfoVo);
    }
}
