package com.ktds.mvne.billing.service;

import com.ktds.mvne.billing.adapter.KTAdapter;
import com.ktds.mvne.billing.dto.BillingInfoResponseDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
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
    private final Timer cacheOperationTimer;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Timer ktAdapterOperationTimer;
    private final Counter ktSystemRequestCounter;

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
        try {
            String cacheKey = generateCacheKey(phoneNumber, billingMonth);
            log.debug("Looking up cache with key: {}", cacheKey);

            Timer.Sample cacheSample = Timer.start();
            BillingInfoResponseDTO cachedInfo = redisTemplate.opsForValue().get(cacheKey);
            cacheSample.stop(cacheOperationTimer);

            if (cachedInfo != null) {
                log.debug("Cache hit for key: {}", cacheKey);
                cacheHitCounter.increment();
                return cachedInfo;
            } else {
                log.debug("Cache miss for key: {}", cacheKey);
                cacheMissCounter.increment();
                return null;
            }
        } catch (Exception e) {
            log.error("Error retrieving data from cache: {} - {}", phoneNumber, billingMonth, e);
            return null; // 캐시 오류 시에도 서비스 지속
        }
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
        try {
            if (billingInfo == null) {
                log.warn("Attempted to cache null billing info for {}, {}", phoneNumber, billingMonth);
                return;
            }

            // 캐싱할 데이터의 유효성 검증
            if (billingInfo.getDetails() == null || billingInfo.getTotalFee() == null) {
                log.warn("Invalid billing info data for caching: {}, {}", phoneNumber, billingMonth);
                return;
            }

            String cacheKey = generateCacheKey(phoneNumber, billingMonth);

            // Redis에 저장 시도할 때 추가 예외 처리
            try {
                Timer.Sample cacheSample = Timer.start();
                redisTemplate.opsForValue().set(cacheKey, billingInfo, ttlHours, TimeUnit.HOURS);
                cacheSample.stop(cacheOperationTimer);
                log.debug("Cached billing info for {}, {} with TTL {} hours", phoneNumber, billingMonth, ttlHours);
            } catch (Exception e) {
                // Redis 서버 연결 실패 등 심각한 오류인 경우 메시지만 로깅하고 진행
                log.error("Failed to store data in Redis cache: {} - {}: {}", phoneNumber, billingMonth, e.getMessage());
                // 오류를 상위로 전파하지 않음 - 캐시 저장 실패는 서비스 전체 실패로 이어지지 않아야 함
            }
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("Error caching billing info: {} - {}: {}", phoneNumber, billingMonth, e.getMessage());
            // 캐시 저장 실패는 크리티컬한 오류가 아니므로 예외를 전파하지 않음
        }
    }

    /**
     * 특정 회선의 특정 월 요금 정보 캐시를 갱신합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     */
    @Override
    public void updateCache(String phoneNumber, String billingMonth) {
        try {
            log.debug("Updating cache for {}, {}", phoneNumber, billingMonth);

            // 기존 캐시 데이터 삭제
            String cacheKey = generateCacheKey(phoneNumber, billingMonth);
            Timer.Sample cacheSample = Timer.start();
            Boolean deleted = redisTemplate.delete(cacheKey);
            cacheSample.stop(cacheOperationTimer);
            log.debug("Deleted existing cache entry: {} - result: {}", cacheKey, deleted);

            // KT 어댑터를 통해 최신 데이터 조회
            ktSystemRequestCounter.increment();
            Timer.Sample ktSample = Timer.start();
            BillingInfoResponseDTO updatedInfo = ktAdapter.getBillingInfo(phoneNumber, billingMonth);
            ktSample.stop(ktAdapterOperationTimer);

            // 새로운 데이터 캐싱
            if (updatedInfo != null) {
                cacheBillingInfo(phoneNumber, billingMonth, updatedInfo);
                log.debug("Cache updated for {}, {}", phoneNumber, billingMonth);
            } else {
                log.warn("Could not update cache - null response from KT adapter: {}, {}", phoneNumber, billingMonth);
            }
        } catch (Exception e) {
            log.error("Error updating cache for {}, {}: {}", phoneNumber, billingMonth, e.getMessage(), e);
            // 캐시 갱신 실패는 크리티컬한 오류가 아니므로 예외를 전파하지 않음
        }
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