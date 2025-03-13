package com.ktds.mvne.common.exception;

import com.ktds.mvne.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 핸들러 클래스입니다.
 * 각 예외 타입에 맞는 응답을 생성합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BizException을 처리합니다.
     *
     * @param e 발생한 BizException
     * @return 에러 응답
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Void>> handleBizException(BizException e) {
        log.error("BizException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(e.getErrorCode().getCode())
                .body(ApiResponse.of(e.getErrorCode().getCode(), e.getDetail(), null));
    }

    /**
     * ExternalSystemException을 처리합니다.
     *
     * @param e 발생한 ExternalSystemException
     * @return 에러 응답
     */
    @ExceptionHandler(ExternalSystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleExternalSystemException(ExternalSystemException e) {
        log.error("ExternalSystemException: {} - {} - {}", 
                e.getExternalSystem(), e.getStatusCode(), e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.of(HttpStatus.SERVICE_UNAVAILABLE.value(), 
                        "외부 시스템 오류: " + e.getMessage(), null));
    }

    /**
     * MethodArgumentNotValidException을 처리합니다.
     *
     * @param e 발생한 MethodArgumentNotValidException
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage(), e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("유효성 검사 실패");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
    }

    /**
     * HttpMessageNotReadableException을 처리합니다.
     *
     * @param e 발생한 HttpMessageNotReadableException
     * @return 에러 응답
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Message not readable: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), "요청 본문을 읽을 수 없습니다.", null));
    }

    /**
     * 기타 모든 Exception을 처리합니다.
     *
     * @param e 발생한 Exception
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                        "서버 내부 오류가 발생했습니다.", null));
    }
}
