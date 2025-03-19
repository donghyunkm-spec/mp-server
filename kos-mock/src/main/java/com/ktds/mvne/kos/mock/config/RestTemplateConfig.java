// kosmock/src/main/java/com/ktds/mvne/kosmock/config/RestTemplateConfig.java
package com.ktds.mvne.kos.mock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정 클래스
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate 빈 설정
     * @return RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}