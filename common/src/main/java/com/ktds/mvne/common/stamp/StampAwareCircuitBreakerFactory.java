package com.ktds.mvne.common.stamp;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

/**
 * 스탬프 별로 분리된 CircuitBreaker를 생성하는 팩토리 클래스입니다.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "stamp.enabled", havingValue = "true", matchIfMissing = true)
public class StampAwareCircuitBreakerFactory {

    private final StampContextHolder stampContextHolder;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    /**
     * 현재 스탬프에 해당하는 CircuitBreaker를 생성합니다.
     *
     * @param id CircuitBreaker ID
     * @return 스탬프 인식 CircuitBreaker 인스턴스
     */
    public CircuitBreaker create(String id) {
        String currentStamp = stampContextHolder.getCurrentStamp();
        String circuitBreakerId = currentStamp + "-" + id;
        return circuitBreakerRegistry.circuitBreaker(circuitBreakerId);
    }
}
