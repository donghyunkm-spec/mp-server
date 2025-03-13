package com.ktds.mvne.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

/**
 * 컨트롤러와 서비스 메소드의 실행 시간과 파라미터를 로깅하는 AOP 클래스입니다.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * 컨트롤러 메소드의 실행을 로깅합니다.
     *
     * @param joinPoint 조인 포인트
     * @return 원본 메소드의 결과
     * @throws Throwable 원본 메소드에서 발생할 수 있는 예외
     */
    @Around("execution(* com.ktds.mvne.*.controller.*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "Controller");
    }

    /**
     * 서비스 메소드의 실행을 로깅합니다.
     *
     * @param joinPoint 조인 포인트
     * @return 원본 메소드의 결과
     * @throws Throwable 원본 메소드에서 발생할 수 있는 예외
     */
    @Around("execution(* com.ktds.mvne.*.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "Service");
    }

    /**
     * 메소드 실행을 로깅하는 공통 로직입니다.
     *
     * @param joinPoint 조인 포인트
     * @param type 로깅 대상 타입 (Controller, Service 등)
     * @return 원본 메소드의 결과
     * @throws Throwable 원본 메소드에서 발생할 수 있는 예외
     */
    private Object logMethod(ProceedingJoinPoint joinPoint, String type) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        
        log.debug("[{}] {}.{} - parameters: {}", 
                type, className, methodName, Arrays.toString(joinPoint.getArgs()));
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        Object result = joinPoint.proceed();
        
        stopWatch.stop();
        log.debug("[{}] {}.{} - execution time: {} ms", 
                type, className, methodName, stopWatch.getTotalTimeMillis());
        
        return result;
    }
}
