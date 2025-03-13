package com.ktds.mvne.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 변경 가능 여부 점검 결과를 담는 응답 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCheckResponse {
    // 필드명이 'available'이어야 isAvailable() 메서드가 생성됩니다
    private Boolean available;
    private String message;
    private ProductInfoDTO currentProduct;
    private ProductInfoDTO targetProduct;

    // Lombok @Data가 isAvailable()를 자동 생성하지만, 명시적으로 추가할 수도 있습니다
    public boolean isAvailable() {
        return available != null && available;
    }
}