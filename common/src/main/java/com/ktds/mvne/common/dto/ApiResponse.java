package com.ktds.mvne.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 응답의 표준 형식을 정의하는 클래스입니다.
 * 모든 API 응답은 이 클래스를 통해 표준화됩니다.
 *
 * @param <T> 응답 데이터의 타입
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Integer status;
    private String message;
    private T data;

    /**
     * 성공 응답을 생성합니다.
     *
     * @param <T> 응답 데이터의 타입
     * @param data 응답 데이터
     * @return 성공 상태의 API 응답 객체
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    /**
     * 에러 응답을 생성합니다.
     *
     * @param message 에러 메시지
     * @return 에러 상태의 API 응답 객체
     */
    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .status(500)
                .message(message)
                .build();
    }

    /**
     * 사용자 정의 상태 코드와 메시지로 API 응답을 생성합니다.
     *
     * @param <T> 응답 데이터의 타입
     * @param status 상태 코드
     * @param message 응답 메시지
     * @param data 응답 데이터
     * @return 사용자 정의 API 응답 객체
     */
    public static <T> ApiResponse<T> of(Integer status, String message, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
