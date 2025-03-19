package com.ktds.mvne.kos.mock.config;
import org.springframework.beans.factory.annotation.Value; // lombok.Value 대신 spring의 Value로 변경
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WebClient 설정 클래스입니다.
 */
@Configuration
public class WebClientConfig implements WebMvcConfigurer {
    @Value("${server.allowed-origins:http://localhost:3000}") // 기본값 추가
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins.split(",")) // List 대신 배열로 전달
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}