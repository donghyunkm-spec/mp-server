package com.ktds.mvne.common.circuitbreaker;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 폴백 처리가 가능한 서킷 브레이커 인터페이스입니다.
 *
 * @param <T> 실행 결과의 타입
 */
public interface CircuitBreakerWithFallback<T> {

    /**
     * 서킷 브레이커로 보호된 코드를 실행하고, 실패 시 폴백 메소드를 실행합니다.
     *
     * @param supplier 보호할 코드 블록
     * @param fallback 실패 시 실행할 폴백 코드 블록
     * @return 실행 결과
     */
    T execute(Supplier<T> supplier, Function<Throwable, T> fallback);
}
