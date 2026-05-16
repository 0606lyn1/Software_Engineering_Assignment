package com.example.tiyu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("体育场馆预约系统 API")
                .description("包含认证、用户、场馆、预约、评论模块")
                .version("v1.0.0"));
    }
}
