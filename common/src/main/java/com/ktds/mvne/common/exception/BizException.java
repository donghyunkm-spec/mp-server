package com.ktds.mvne.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 처리 중 발생하는 예외를 표현하는 클래스입니다.
 * 에러 코드와 상세 메시지를 포함합니다.
 */
@Getter
public class BizException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detail;

    /**
     * 에러 코드만으로 예외를 생성합니다.
     *
     * @param errorCode 에러 코드
     */
    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = errorCode.getMessage();
    }

    /**
     * 에러 코드와 상세 메시지로 예외를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param detail 상세 메시지
     */
    public BizException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
