package com.ktds.mvne.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 및 스케줄링을 위한 설정 클래스입니다.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    @Value("${worker.product-change.core-pool-size:2}")
    private int corePoolSize;

    @Value("${worker.product-change.max-pool-size:5}")
    private int maxPoolSize;

    @Value("${worker.product-change.queue-capacity:10}")
    private int queueCapacity;

    @Value("${worker.product-change.thread-name-prefix:product-change-worker-}")
    private String threadNamePrefix;

    /**
     * 비동기 처리를 위한 태스크 실행기 빈을 생성합니다.
     *
     * @return Executor 인스턴스
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
