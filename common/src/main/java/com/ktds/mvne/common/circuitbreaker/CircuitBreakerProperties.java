package com.ktds.mvne.common.circuitbreaker;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * 서킷 브레이커 설정 속성 클래스입니다.
 */
@Getter
@Setter
public class CircuitBreakerProperties {
    private boolean enabled = true;
    private int slidingWindowSize = 10;
    private int minimumNumberOfCalls = 5;
    private float failureRateThreshold = 50;
    private Duration waitDurationInOpenState = Duration.ofSeconds(30);
    private int permittedNumberOfCallsInHalfOpenState = 3;
}
