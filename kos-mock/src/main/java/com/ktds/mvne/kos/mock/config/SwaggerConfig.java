package com.ktds.mvne.kos.mock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger(OpenAPI) 설정 클래스입니다.
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 구성을 제공합니다.
     *
     * @return OpenAPI 인스턴스
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MVNE 플랫폼 - KOS 목업 서비스 API")
                        .description("KOS 목업 서비스의 REST API 문서입니다.")
                        .version("v1.0.0"));
    }
}

