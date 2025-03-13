package com.ktds.mvne.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 비즈니스 예외에서 사용되는 에러 코드를 정의합니다.
 * 각 에러 코드는 HTTP 상태 코드와 메시지를 포함합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, "허용되지 않은 메소드입니다."),
    CONFLICT(409, "리소스 충돌이 발생했습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(503, "서비스를 사용할 수 없습니다.");

    private final Integer code;
    private final String message;
}
