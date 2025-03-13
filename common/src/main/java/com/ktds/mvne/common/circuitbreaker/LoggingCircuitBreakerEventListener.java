package com.ktds.mvne.common.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 서킷 브레이커 이벤트를 로깅하는 리스너 구현체입니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingCircuitBreakerEventListener implements CircuitBreakerEventListener {

    private final CircuitBreakerMetricsCollector metricsCollector;

    /**
     * 호출 성공 시 로그를 기록합니다.
     *
     * @param circuitBreaker 서킷 브레이커
     * @param duration 호출 소요 시간
     */
    @Override
    public void onSuccess(CircuitBreaker circuitBreaker, Duration duration) {
        log.debug("Circuit breaker {} call succeeded in {} ms", 
                circuitBreaker.getName(), duration.toMillis());
    }

    /**
     * 호출 실패 시 로그를 기록합니다.
     *
     * @param circuitBreaker 서킷 브레이커
     * @param throwable 발생한 예외
     * @param duration 호출 소요 시간
     */
    @Override
    public void onError(CircuitBreaker circuitBreaker, Throwable throwable, Duration duration) {
        log.warn("Circuit breaker {} call failed in {} ms: {}", 
                circuitBreaker.getName(), duration.toMillis(), throwable.getMessage());
    }

    /**
     * 상태 전환 시 로그를 기록하고 메트릭을 수집합니다.
     *
     * @param circuitBreaker 서킷 브레이커
     * @param oldState 이전 상태
     * @param newState 전환된 상태
     */
    @Override
    public void onStateTransition(CircuitBreaker circuitBreaker, CircuitBreaker.State oldState, CircuitBreaker.State newState) {
        log.info("Circuit breaker {} state changed from {} to {}", 
                circuitBreaker.getName(), oldState, newState);
        metricsCollector.recordStateTransition(circuitBreaker.getName(), oldState, newState);
    }
}
