package com.ktds.mvne.common.exception;

import lombok.Getter;

/**
 * 외부 시스템과의 통신 중 발생하는 예외를 표현하는 클래스입니다.
 * 외부 시스템의 이름과 상태 코드를 포함합니다.
 */
@Getter
public class ExternalSystemException extends RuntimeException {
    private final Integer statusCode;
    private final String externalSystem;

    /**
     * 메시지, 상태 코드, 외부 시스템 이름으로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param statusCode 외부 시스템의 응답 상태 코드
     * @param externalSystem 외부 시스템 이름
     */
    public ExternalSystemException(String message, Integer statusCode, String externalSystem) {
        super(message);
        this.statusCode = statusCode;
        this.externalSystem = externalSystem;
    }
}
