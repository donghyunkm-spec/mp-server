package com.ktds.mvne.billing.service;

import com.ktds.mvne.billing.dto.BillingInfoResponseDTO;

/**
 * 요금 조회 관련 서비스 인터페이스입니다.
 */
public interface BillingService {

    /**
     * 현재 또는 전월 요금을 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 요금 정보
     */
    BillingInfoResponseDTO getCurrentBilling(String phoneNumber);

    /**
     * 특정 월의 요금을 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 요금 정보
     */
    BillingInfoResponseDTO getSpecificBilling(String phoneNumber, String billingMonth);
}
