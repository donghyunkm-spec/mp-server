package com.ktds.mvne.kos.adapter.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * KOS 어댑터 서비스 메트릭 설정 클래스입니다.
 */
@Configuration
public class MetricsConfig {

    /**
     * KOS 클라이언트 호출 타이머를 생성합니다.
     * KOS 클라이언트를 통한 요청 처리 시간을 측정하는 타이머입니다.
     */
    @Bean
    public Timer kosClientOperationTimer(MeterRegistry registry) {
        return Timer.builder("kos_client_operation_time")
                .description("KOS 클라이언트 요청 처리 시간")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofMillis(500),  // 500ms
                        Duration.ofSeconds(1),   // 1s
                        Duration.ofSeconds(3)    // 3s
                )
                .register(registry);
    }

    /**
     * KOS 요청 카운터를 생성합니다.
     * KOS 클라이언트를 통한 요청 횟수를 측정합니다.
     */
    @Bean
    public Counter kosRequestCounter(MeterRegistry registry) {
        return Counter.builder("kos_request_total")
                .description("KOS 요청 횟수")
                .register(registry);
    }

    /**
     * KOS 응답 성공 카운터를 생성합니다.
     * KOS 클라이언트를 통한 요청 성공 횟수를 측정합니다.
     */
    @Bean
    public Counter kosSuccessCounter(MeterRegistry registry) {
        return Counter.builder("kos_success_total")
                .description("KOS 요청 성공 횟수")
                .register(registry);
    }

    /**
     * KOS 응답 실패 카운터를 생성합니다.
     * KOS 클라이언트를 통한 요청 실패 횟수를 측정합니다.
     */
    @Bean
    public Counter kosErrorCounter(MeterRegistry registry) {
        return Counter.builder("kos_error_total")
                .description("KOS 요청 실패 횟수")
                .register(registry);
    }
}