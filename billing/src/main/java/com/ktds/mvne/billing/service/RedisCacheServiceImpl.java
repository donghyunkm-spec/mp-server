package com.ktds.mvne.billing.service;

import com.ktds.mvne.billing.adapter.KTAdapter;
import com.ktds.mvne.billing.dto.BillingInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis를 사용한 캐싱 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, BillingInfoResponseDTO> redisTemplate;
    private final KTAdapter ktAdapter;

    @Value("${cache.billing-info.ttl-hours:24}")
    private long ttlHours;

    /**
     * 캐시에서 요금 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 캐시된 요금 정보, 캐시에 없는 경우 null
     */
    @Override
    public BillingInfoResponseDTO getCachedBillingInfo(String phoneNumber, String billingMonth) {
        String cacheKey = generateCacheKey(phoneNumber, billingMonth);
        return redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * 요금 정보를 캐시에 저장합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @param billingInfo 요금 정보
     */
    @Override
    public void cacheBillingInfo(String phoneNumber, String billingMonth, BillingInfoResponseDTO billingInfo) {
        if (billingInfo == null) {
            log.warn("Attempted to cache null billing info for {}, {}", phoneNumber, billingMonth);
            return;
        }
        
        String cacheKey = generateCacheKey(phoneNumber, billingMonth);
        redisTemplate.opsForValue().set(cacheKey, billingInfo, ttlHours, TimeUnit.HOURS);
        log.debug("Cached billing info for {}, {} with TTL {} hours", phoneNumber, billingMonth, ttlHours);
    }

    /**
     * 특정 회선의 특정 월 요금 정보 캐시를 갱신합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     */
    @Override
    public void updateCache(String phoneNumber, String billingMonth) {
        log.debug("Updating cache for {}, {}", phoneNumber, billingMonth);
        BillingInfoResponseDTO updatedInfo = ktAdapter.getBillingInfo(phoneNumber, billingMonth);
        cacheBillingInfo(phoneNumber, billingMonth, updatedInfo);
    }

    /**
     * 캐시 키를 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 캐시 키
     */
    private String generateCacheKey(String phoneNumber, String billingMonth) {
        return "billing:" + phoneNumber + ":" + billingMonth;
    }
}
