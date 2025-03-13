package com.ktds.mvne.common.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.time.Duration;

/**
 * 서킷 브레이커 이벤트를 수신하는 리스너 인터페이스입니다.
 */
public interface CircuitBreakerEventListener {

    /**
     * 호출 성공 시 실행됩니다.
     *
     * @param circuitBreaker 서킷 브레이커
     * @param duration 호출 소요 시간
     */
    void onSuccess(CircuitBreaker circuitBreaker, Duration duration);

    /**
     * 호출 실패 시 실행됩니다.
     *
     * @param circuitBreaker 서킷 브레이커
     * @param throwable 발생한 예외
     * @param duration 호출 소요 시간
     */
    void onError(CircuitBreaker circuitBreaker, Throwable throwable, Duration duration);

    /**
     * 상태 전환 시 실행됩니다.
     *
     * @param circuitBreaker 서킷 브레이커
     * @param oldState 이전 상태
     * @param newState 전환된 상태
     */
    void onStateTransition(CircuitBreaker circuitBreaker, CircuitBreaker.State oldState, CircuitBreaker.State newState);
}
