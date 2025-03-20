package com.ktds.mvne.product.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 상품변경 서비스 메트릭 설정 클래스입니다.
 */
@Configuration
public class MetricsConfig {

    /**
     * 상품 변경 처리 타이머를 생성합니다.
     * 상품 변경 처리 시간을 측정하는 타이머입니다.
     */
    @Bean
    public Timer productChangeOperationTimer(MeterRegistry registry) {
        return Timer.builder("product_change_operation_time")
                .description("상품 변경 처리 시간")
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
     * 서킷 브레이커 처리 타이머를 생성합니다.
     * 서킷 브레이커로 보호된 작업의 처리 시간을 측정하는 타이머입니다.
     */
    @Bean
    public Timer circuitBreakerOperationTimer(MeterRegistry registry) {
        return Timer.builder("circuit_breaker_operation_time")
                .description("서킷 브레이커 처리 시간")
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
     * 상품 변경 API 요청 카운터를 생성합니다.
     * 상품 변경 API 요청 횟수를 측정합니다.
     */
    @Bean
    public Counter productChangeRequestCounter(MeterRegistry registry) {
        return Counter.builder("product_change_request_total")
                .description("상품 변경 API 요청 횟수")
                .register(registry);
    }

    /**
     * 상품 변경 API 성공 카운터를 생성합니다.
     * 상품 변경 API 성공 횟수를 측정합니다.
     */
    @Bean
    public Counter productChangeSuccessCounter(MeterRegistry registry) {
        return Counter.builder("product_change_success_total")
                .description("상품 변경 API 성공 횟수")
                .register(registry);
    }

    /**
     * 상품 변경 API 실패 카운터를 생성합니다.
     * 상품 변경 API 실패 횟수를 측정합니다.
     */
    @Bean
    public Counter productChangeErrorCounter(MeterRegistry registry) {
        return Counter.builder("product_change_error_total")
                .description("상품 변경 API 실패 횟수")
                .register(registry);
    }

    /**
     * 즉시 처리된 상품 변경 카운터를 생성합니다.
     * 동기적으로 즉시 처리된 상품 변경 요청 횟수를 측정합니다.
     */
    @Bean
    public Counter productChangeSyncCounter(MeterRegistry registry) {
        return Counter.builder("product_change_sync_total")
                .description("즉시 처리된 상품 변경 횟수")
                .register(registry);
    }

    /**
     * 비동기 처리된 상품 변경 카운터를 생성합니다.
     * 비동기적으로 처리된 상품 변경 요청 횟수를 측정합니다.
     */
    @Bean
    public Counter productChangeAsyncCounter(MeterRegistry registry) {
        return Counter.builder("product_change_async_total")
                .description("비동기 처리된 상품 변경 횟수")
                .register(registry);
    }

    /**
     * 서킷 브레이커 상태 변경 카운터를 생성합니다.
     * 서킷 브레이커의 상태 변경 횟수를 측정합니다.
     */
    @Bean
    public Counter circuitBreakerStateChangeCounter(MeterRegistry registry) {
        return Counter.builder("circuit_breaker_state_change_total")
                .description("서킷 브레이커 상태 변경 횟수")
                .tag("state", "unknown")
                .register(registry);
    }
}