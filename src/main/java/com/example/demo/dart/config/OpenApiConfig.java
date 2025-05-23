package com.example.demo.dart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KB Project API")
                        .version("1.0")
                        .description("SpringDoc OpenAPI를 사용한 KB 프로젝트 API 문서"));
    }
}
