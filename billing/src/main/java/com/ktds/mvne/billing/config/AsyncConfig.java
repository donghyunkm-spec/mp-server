package com.ktds.mvne.billing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리를 위한 설정 클래스입니다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 비동기 처리를 위한 태스크 실행기 빈을 생성합니다.
     *
     * @return Executor 인스턴스
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("billing-sync-");
        executor.initialize();
        return executor;
    }
}
