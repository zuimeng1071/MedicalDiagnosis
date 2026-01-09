package com.ai.medical_diagnosis.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Knife4j配置
 *
 * @author jie
 */
@Configuration
public class SwaggerConfig {
    /**
     * API标题
     */
    private static final String API_TITLE = "智能医生辅助诊断平台";
    /**
     * API版本
     */
    private static final String API_VERSION = "V1.0";
    /**
     * API描述
     */
    private static final String API_DESCRIPTION = "智能医生辅助诊断平台";
    /**
     * 服务条款URL
     */
    private static final String TERMS_OF_SERVICE_URL = "http://doc.xiaominfo.com";
    /**
     * 许可证名称
     */
    private static final String LICENSE_NAME = "Apache 2.0";
    /**
     * 许可证URL
     */
    private static final String LICENSE_URL = "http://doc.xiaominfo.com";

    /**
     * 根据@Tag 上的排序，写入x-order
     *
     * @return the global open api customizer
     */
    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getTags() != null) {
                openApi.getTags().forEach(tag -> {
                    // 使用Map.of简化Map的创建
                    tag.setExtensions(Map.of("x-order", 1));
                });
            }
            if (openApi.getPaths() != null) {
                openApi.addExtension("x-test123", "333");
                openApi.getPaths().addExtension("x-abb", 1);
            }
        };
    }

    @Bean
    public OpenAPI createCustomOpenApi() { // 修改方法名为createCustomOpenApi
        return new OpenAPI()
                .info(new Info()
                        .title(API_TITLE)
                        .version(API_VERSION)
                        .description(API_DESCRIPTION)
                        .termsOfService(TERMS_OF_SERVICE_URL)
                        .license(new License().name(LICENSE_NAME)
                                .url(LICENSE_URL)));
    }
}