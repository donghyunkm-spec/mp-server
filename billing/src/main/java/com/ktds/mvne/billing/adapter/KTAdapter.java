package com.ktds.mvne.billing.adapter;

import com.ktds.mvne.billing.dto.BillingInfoResponseDTO;
import com.ktds.mvne.billing.dto.BillingStatusResponse;
import com.ktds.mvne.billing.dto.CustomerInfoResponseDTO;

/**
 * KT 영업시스템과의 통신을 담당하는 어댑터 인터페이스입니다.
 */
public interface KTAdapter {

    /**
     * 당월 청구 데이터 존재 여부를 확인합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 청구 상태 정보
     */
    BillingStatusResponse checkBillingStatus(String phoneNumber);

    /**
     * 요금 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 요금 정보
     */
    BillingInfoResponseDTO getBillingInfo(String phoneNumber, String billingMonth);

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    CustomerInfoResponseDTO getCustomerInfo(String phoneNumber);
}
