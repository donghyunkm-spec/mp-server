package com.ktds.mvne.billing.service;

import com.ktds.mvne.billing.dto.BillingChangeNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 요금 정보 동기화 서비스 클래스입니다.
 * KT 영업시스템으로부터의 요금 정보 변경 이벤트를 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final CacheService cacheService;

    /**
     * 요금 정보 변경 이벤트를 처리합니다.
     * 이벤트 수신 시 해당 회선의 해당 월 요금 정보 캐시를 갱신합니다.
     *
     * @param billingChangeEvent 요금 정보 변경 이벤트
     */
    @Async
    public void processBillingChangeEvent(BillingChangeNotificationRequest billingChangeEvent) {
        log.info("Processing billing change event for {}, {}", 
                billingChangeEvent.getPhoneNumber(), billingChangeEvent.getBillingMonth());
        
        try {
            cacheService.updateCache(billingChangeEvent.getPhoneNumber(), billingChangeEvent.getBillingMonth());
            log.info("Successfully updated cache for {}, {}", 
                    billingChangeEvent.getPhoneNumber(), billingChangeEvent.getBillingMonth());
        } catch (Exception e) {
            log.error("Failed to update cache for {}, {}: {}", 
                    billingChangeEvent.getPhoneNumber(), billingChangeEvent.getBillingMonth(), e.getMessage(), e);
        }
    }
}
