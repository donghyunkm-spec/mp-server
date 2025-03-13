package com.ktds.mvne.billing.service;

import com.ktds.mvne.billing.dto.BillingInfoResponseDTO;

/**
 * 캐싱 관련 서비스 인터페이스입니다.
 */
public interface CacheService {

    /**
     * 캐시에서 요금 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 캐시된 요금 정보, 캐시에 없는 경우 null
     */
    BillingInfoResponseDTO getCachedBillingInfo(String phoneNumber, String billingMonth);

    /**
     * 요금 정보를 캐시에 저장합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @param billingInfo 요금 정보
     */
    void cacheBillingInfo(String phoneNumber, String billingMonth, BillingInfoResponseDTO billingInfo);

    /**
     * 특정 회선의 특정 월 요금 정보 캐시를 갱신합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     */
    void updateCache(String phoneNumber, String billingMonth);
}
