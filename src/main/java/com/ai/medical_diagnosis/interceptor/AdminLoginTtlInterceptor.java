package com.ai.medical_diagnosis.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.ai.medical_diagnosis.constants.RedisConstants;
import com.ai.medical_diagnosis.domain.dto.AdminInfoDTO;
import com.ai.medical_diagnosis.utils.AdminHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AdminLoginTtlInterceptor implements HandlerInterceptor {
    private static StringRedisTemplate stringRedisTemplate;

    public AdminLoginTtlInterceptor(StringRedisTemplate stringRedisTemplate) {
        AdminLoginTtlInterceptor.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
        String token = request.getHeader("Authorization-Admin");

        // 2.获取用户
        Map<Object, Object> mapUser = stringRedisTemplate.opsForHash().entries(RedisConstants.LOGIN_ADMIN_KEY + token);
        if (!mapUser.isEmpty()){
            AdminInfoDTO admin = BeanUtil.fillBeanWithMap(mapUser, new AdminInfoDTO(), false);
            admin.setToken(token);
            log.info("用户登录状态：{}", admin);
            // 2.1 用户已登录，保存用户
            AdminHolder.saveAdmin(admin);
            // 2.2 设置token的TTL
            stringRedisTemplate.expire(RedisConstants.LOGIN_ADMIN_KEY + token,
                    RedisConstants.LOGIN_ADMIN_TTL,
                    TimeUnit.MINUTES);
        }
        // 4.返回结果
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        AdminHolder.removeAdmin();
    }
}
