package com.ktds.mvne.common.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 서킷 브레이커 메트릭을 수집하고 기록하는 클래스입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CircuitBreakerMetricsCollector {

    private final MeterRegistry meterRegistry;

    /**
     * 성공적인 호출을 기록합니다.
     *
     * @param name 서킷 브레이커 이름
     */
    public void recordSuccess(String name) {
        meterRegistry.counter("circuit.breaker.calls", "name", name, "result", "success").increment();
        log.debug("Circuit breaker {} call succeeded", name);
    }

    /**
     * 실패한 호출을 기록합니다.
     *
     * @param name 서킷 브레이커 이름
     */
    public void recordFailure(String name) {
        meterRegistry.counter("circuit.breaker.calls", "name", name, "result", "failure").increment();
        log.debug("Circuit breaker {} call failed", name);
    }

    /**
     * 서킷 브레이커 상태 전환을 기록합니다.
     *
     * @param name 서킷 브레이커 이름
     * @param from 이전 상태
     * @param to 전환된 상태
     */
    public void recordStateTransition(String name, CircuitBreaker.State from, CircuitBreaker.State to) {
        meterRegistry.counter("circuit.breaker.transitions", 
                "name", name, "from", from.name(), "to", to.name()).increment();
        log.info("Circuit breaker {} state changed from {} to {}", name, from, to);
    }
}
