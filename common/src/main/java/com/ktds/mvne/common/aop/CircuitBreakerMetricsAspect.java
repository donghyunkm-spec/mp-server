package com.ktds.mvne.common.aop;

import com.ktds.mvne.common.circuitbreaker.CircuitBreakerMetricsCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 서킷 브레이커 메트릭을 수집하는 AOP 클래스입니다.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CircuitBreakerMetricsAspect {

    private final CircuitBreakerMetricsCollector metricsCollector;

    /**
     * 서킷 브레이커가 적용된 메소드의 실행 결과를 기록합니다.
     *
     * @param joinPoint 조인 포인트
     * @return 원본 메소드의 결과
     * @throws Throwable 원본 메소드에서 발생할 수 있는 예외
     */
    @Around("@annotation(io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker)")
    public Object collectMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String circuitBreakerName = className + "." + methodName;
        
        try {
            Object result = joinPoint.proceed();
            metricsCollector.recordSuccess(circuitBreakerName);
            return result;
        } catch (Throwable e) {
            metricsCollector.recordFailure(circuitBreakerName);
            throw e;
        }
    }
}
