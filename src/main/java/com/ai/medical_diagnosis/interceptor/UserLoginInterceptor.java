package com.ai.medical_diagnosis.interceptor;

import com.ai.medical_diagnosis.domain.dto.UserInfoDTO;
import com.ai.medical_diagnosis.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class UserLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 1. 获取用户
        UserInfoDTO user = UserHolder.getUser();
        // 2. 判断用户是否登录
        if (user == null){
            log.info("用户未登录");
            response.setStatus(401);
            return false;
        }
        // 3. 返回结果
        return true;
    }
}
