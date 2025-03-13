package com.ktds.mvne.common.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 서킷 브레이커 설정 클래스입니다.
 */
@Configuration
@RequiredArgsConstructor
public class CommonCircuitBreakerConfig {

    private final Map<String, CircuitBreakerProperties> circuitBreakerProperties;

    /**
     * CircuitBreakerRegistry 빈을 생성합니다.
     *
     * @return CircuitBreakerRegistry 인스턴스
     */
    @Bean("commonCircuitBreakerRegistry")
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();

        circuitBreakerProperties.forEach((name, properties) -> {
            io.github.resilience4j.circuitbreaker.CircuitBreakerConfig config =
                    io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                            .slidingWindowType(SlidingWindowType.COUNT_BASED)
                            .slidingWindowSize(properties.getSlidingWindowSize())
                            .minimumNumberOfCalls(properties.getMinimumNumberOfCalls())
                            .failureRateThreshold(properties.getFailureRateThreshold())
                            .waitDurationInOpenState(properties.getWaitDurationInOpenState())
                            .permittedNumberOfCallsInHalfOpenState(properties.getPermittedNumberOfCallsInHalfOpenState())
                            .build();

            registry.addConfiguration(name, config);
        });

        return registry;
    }
}