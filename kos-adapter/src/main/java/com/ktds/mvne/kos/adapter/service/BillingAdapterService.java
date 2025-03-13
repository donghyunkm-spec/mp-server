package com.ktds.mvne.kos.adapter.service;

import com.ktds.mvne.kos.adapter.dto.BillingInfoResponse;
import com.ktds.mvne.kos.adapter.dto.BillingStatusResponse;

/**
 * KT 영업시스템의 요금 조회 관련 어댑터 서비스 인터페이스입니다.
 */
public interface BillingAdapterService {

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
    BillingInfoResponse getBillingInfo(String phoneNumber, String billingMonth);
}
