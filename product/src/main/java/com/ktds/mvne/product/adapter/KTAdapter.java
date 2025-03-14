package com.ktds.mvne.product.adapter;

import com.ktds.mvne.product.dto.CustomerInfoResponseDTO;
import com.ktds.mvne.product.dto.ProductChangeResponse;
import com.ktds.mvne.product.dto.ProductInfoDTO;

/**
 * KT 영업시스템과의 연동을 위한 어댑터 인터페이스입니다.
 */
public interface KTAdapter {

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 전화번호
     * @return 고객 정보 응답
     */
    CustomerInfoResponseDTO getCustomerInfo(String phoneNumber);

    /**
     * 상품 정보를 조회합니다.
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    ProductInfoDTO getProductInfo(String productCode);

    /**
     * 상품을 변경합니다.
     *
     * @param phoneNumber 전화번호
     * @param productCode 변경할 상품 코드
     * @param changeReason 변경 사유
     * @return 상품 변경 응답
     */
    ProductChangeResponse changeProduct(String phoneNumber, String productCode, String changeReason);
}