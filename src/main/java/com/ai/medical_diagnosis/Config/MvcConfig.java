package com.ai.medical_diagnosis.Config;


import com.ai.medical_diagnosis.interceptor.AdminLoginInterceptor;
import com.ai.medical_diagnosis.interceptor.AdminLoginTtlInterceptor;
import com.ai.medical_diagnosis.interceptor.UserLoginInterceptor;
import com.ai.medical_diagnosis.interceptor.UserLoginTtlInterceptor;
import com.ai.medical_diagnosis.json.JacksonObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginTtlInterceptor(stringRedisTemplate))
                .addPathPatterns("/user/**")
                .order(0);

        registry.addInterceptor(new AdminLoginTtlInterceptor(stringRedisTemplate))
                .addPathPatterns("/admin/**")
                .order(1);

        registry.addInterceptor(new AdminLoginInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/admin/login",
                        "/admin/admin/register")
                .order(2);

        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/user/login",
                        "/user/user/register")
                .order(3);
    }
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(messageConverter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // ✅ Spring Boot 2.4+ 用这个替代 allowedOrigins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 如果前端带认证信息
    }
}
