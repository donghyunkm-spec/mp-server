package com.ktds.mvne.billing.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 요금조회 서비스 메트릭 설정 클래스입니다.
 */
@Configuration
public class MetricsConfig {

    /**
     * 캐시 조회 타이머를 생성합니다.
     * 캐시 조회 시간을 측정하는 타이머입니다.
     */
    @Bean
    public Timer cacheOperationTimer(MeterRegistry registry) {
        return Timer.builder("cache_operation_time")
                .description("요금정보 캐시 조회 시간")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofMillis(100), // 100ms
                        Duration.ofMillis(500), // 500ms
                        Duration.ofSeconds(1)   // 1s
                )
                .register(registry);
    }

    /**
     * KT 어댑터 호출 타이머를 생성합니다.
     * KT 어댑터를 통한 요금정보 조회 시간을 측정하는 타이머입니다.
     */
    @Bean
    public Timer ktAdapterOperationTimer(MeterRegistry registry) {
        return Timer.builder("kt_adapter_operation_time")
                .description("KT 어댑터 호출 시간")
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
     * 캐시 히트 카운터를 생성합니다.
     * 캐시에서 요금정보를 찾은 횟수를 측정합니다.
     */
    @Bean
    public Counter cacheHitCounter(MeterRegistry registry) {
        return Counter.builder("cache_hit_total")
                .description("캐시 히트 횟수")
                .register(registry);
    }

    /**
     * 캐시 미스 카운터를 생성합니다.
     * 캐시에서 요금정보를 찾지 못한 횟수를 측정합니다.
     */
    @Bean
    public Counter cacheMissCounter(MeterRegistry registry) {
        return Counter.builder("cache_miss_total")
                .description("캐시 미스 횟수")
                .register(registry);
    }

    /**
     * KT 영업시스템 요청 카운터를 생성합니다.
     * KT 영업시스템으로 직접 요청한 횟수를 측정합니다.
     */
    @Bean
    public Counter ktSystemRequestCounter(MeterRegistry registry) {
        return Counter.builder("kt_system_request_total")
                .description("KT 영업시스템 직접 요청 횟수")
                .register(registry);
    }

    /**
     * 요금 조회 API 요청 카운터를 생성합니다.
     * 요금 조회 API 요청 횟수를 측정합니다.
     */
    @Bean
    public Counter billingRequestCounter(MeterRegistry registry) {
        return Counter.builder("billing_request_total")
                .description("요금 조회 API 요청 횟수")
                .register(registry);
    }

    /**
     * 요금 조회 API 성공 카운터를 생성합니다.
     * 요금 조회 API 성공 횟수를 측정합니다.
     */
    @Bean
    public Counter billingSuccessCounter(MeterRegistry registry) {
        return Counter.builder("billing_success_total")
                .description("요금 조회 API 성공 횟수")
                .register(registry);
    }

    /**
     * 요금 조회 API 실패 카운터를 생성합니다.
     * 요금 조회 API 실패 횟수를 측정합니다.
     */
    @Bean
    public Counter billingErrorCounter(MeterRegistry registry) {
        return Counter.builder("billing_error_total")
                .description("요금 조회 API 실패 횟수")
                .register(registry);
    }
}