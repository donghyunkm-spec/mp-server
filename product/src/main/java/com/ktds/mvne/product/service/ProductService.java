package com.ktds.mvne.product.service;

import com.ktds.mvne.product.dto.ProductCheckResponse;
import com.ktds.mvne.product.dto.ProductChangeResponse;

/**
 * 상품 정보 및 변경 관련 서비스 인터페이스입니다.
 */
public interface ProductService {

    /**
     * 상품 변경 가능 여부를 확인합니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 변경하려는 상품 코드
     * @return 상품 변경 가능 여부 및 정보
     */
    ProductCheckResponse checkProductChangeAvailability(String phoneNumber, String productCode);

    /**
     * 상품을 변경합니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 변경하려는 상품 코드
     * @param changeReason 변경 사유
     * @return 상품 변경 결과
     */
    ProductChangeResponse changeProduct(String phoneNumber, String productCode, String changeReason);
}
