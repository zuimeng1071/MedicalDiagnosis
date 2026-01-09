package com.ai.medical_diagnosis.interceptor;


import com.ai.medical_diagnosis.domain.dto.AdminInfoDTO;
import com.ai.medical_diagnosis.utils.AdminHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AdminLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 1. 获取用户
        AdminInfoDTO admin = AdminHolder.getAdmin();
        // 2. 判断用户是否登录
        if (admin == null){
            log.info("管理员未登录");
            response.setStatus(401);
            return false;
        }
        // 3. 返回结果
        return true;
    }
}
