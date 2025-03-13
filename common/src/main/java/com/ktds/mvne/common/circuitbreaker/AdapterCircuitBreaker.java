package com.ktds.mvne.common.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 어댑터 레이어에서 사용하는 서킷 브레이커 클래스입니다.
 * 비동기 폴백 처리가 가능합니다.
 *
 * @param <T> 실행 결과의 타입
 */
@Slf4j
@RequiredArgsConstructor
public class AdapterCircuitBreaker<T> implements CircuitBreakerWithFallback<T> {

    private final CircuitBreaker circuitBreaker;

    /**
     * 서킷 브레이커로 보호된 코드를 실행하고, 실패 시 폴백 메소드를 실행합니다.
     *
     * @param supplier 보호할 코드 블록
     * @param fallback 실패 시 실행할 폴백 코드 블록
     * @return 실행 결과
     */
    @Override
    public T execute(Supplier<T> supplier, Function<Throwable, T> fallback) {
        try {
            return circuitBreaker.executeSupplier(supplier);
        } catch (Throwable t) {
            return fallback.apply(t);
        }
    }

    /**
     * 서킷 브레이커로 보호된 코드를 실행하고, 실패 시 비동기 폴백 처리를 수행합니다.
     *
     * @param supplier 보호할 코드 블록
     * @param asyncFallback 실패 시 비동기 처리할 폴백 코드 블록
     * @return 실행 결과
     */
    public T executeWithAsync(Supplier<T> supplier, BiConsumer<Supplier<T>, Throwable> asyncFallback) {
        try {
            return circuitBreaker.executeSupplier(supplier);
        } catch (Throwable e) {
            log.warn("Circuit breaker {} is open. Executing async fallback.", circuitBreaker.getName());
            asyncFallback.accept(supplier, e);
            throw e;
        }
    }
}