package com.ktds.mvne.kos.adapter.service;

import com.ktds.mvne.kos.adapter.dto.CustomerInfoResponse;
import com.ktds.mvne.kos.adapter.dto.ProductChangeResponse;
import com.ktds.mvne.kos.adapter.dto.ProductDetail;

/**
 * KT 영업시스템의 상품 관련 어댑터 서비스 인터페이스입니다.
 */
public interface ProductAdapterService {

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    CustomerInfoResponse getCustomerInfo(String phoneNumber);

    /**
     * 상품 정보를 조회합니다.
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    ProductDetail getProductInfo(String productCode);

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
