package com.ktds.mvne.kos.adapter.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 서킷 브레이커 설정 클래스입니다.
 */
@Configuration
@RequiredArgsConstructor
public class KosAdapterCircuitBreakerConfig {

    @Value("${resilience4j.circuitbreaker.instances.kosRequest.slidingWindowSize:10}")
    private int slidingWindowSize;

    @Value("${resilience4j.circuitbreaker.instances.kosRequest.minimumNumberOfCalls:5}")
    private int minimumNumberOfCalls;

    @Value("${resilience4j.circuitbreaker.instances.kosRequest.failureRateThreshold:50}")
    private float failureRateThreshold;

    @Value("${resilience4j.circuitbreaker.instances.kosRequest.waitDurationInOpenState:30000}")
    private long waitDurationInOpenState;

    @Value("${resilience4j.circuitbreaker.instances.kosRequest.permittedNumberOfCallsInHalfOpenState:3}")
    private int permittedNumberOfCallsInHalfOpenState;

    /**
     * CircuitBreakerRegistry 빈을 생성합니다.
     *
     * @return CircuitBreakerRegistry 인스턴스
     */
    @Bean("kosAdapterCircuitBreakerRegistry")
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig kosConfig = 
                io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .slidingWindowType(SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(slidingWindowSize)
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenState))
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .build();

        return CircuitBreakerRegistry.of(java.util.Map.of("kosRequest", kosConfig));
    }

    /**
     * 요금 조회용 서킷 브레이커 빈을 생성합니다.
     *
     * @param registry CircuitBreakerRegistry 인스턴스
     * @return CircuitBreaker 인스턴스
     */
    @Bean
    public CircuitBreaker billingCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("billing-kosRequest");
    }

    /**
     * 상품 관리용 서킷 브레이커 빈을 생성합니다.
     *
     * @param registry CircuitBreakerRegistry 인스턴스
     * @return CircuitBreaker 인스턴스
     */
    @Bean
    public CircuitBreaker productCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("product-kosRequest");
    }
}
