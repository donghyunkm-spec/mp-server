package com.ktds.mvne.billing.service;

import com.ktds.mvne.billing.adapter.KTAdapter;
import com.ktds.mvne.billing.dto.BillingInfoResponseDTO;
import com.ktds.mvne.billing.dto.BillingStatusResponse;
import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import com.ktds.mvne.common.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 요금 조회 관련 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillingServiceImpl implements BillingService {

    private final KTAdapter ktAdapter;
    private final CacheService cacheService;

    /**
     * 현재 또는 전월 요금을 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 요금 정보
     */
    @Override
    public BillingInfoResponseDTO getCurrentBilling(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        
        // 당월 청구 데이터 존재 여부 확인
        BillingStatusResponse statusResponse = ktAdapter.checkBillingStatus(phoneNumber);
        log.debug("BillingStatus for {}: {}", phoneNumber, statusResponse);
        
        if (statusResponse.isBillingGenerated()) {
            // 당월 청구 데이터가 생성된 경우
            return getBillingInfo(phoneNumber, statusResponse.getCurrentBillingMonth());
        } else {
            // 당월 청구 데이터가 생성되지 않은 경우 전월 데이터 조회
            String previousMonth = calculatePreviousMonth(statusResponse.getCurrentBillingMonth());
            return getBillingInfo(phoneNumber, previousMonth);
        }
    }

    /**
     * 특정 월의 요금을 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 요금 정보
     */
    @Override
    public BillingInfoResponseDTO getSpecificBilling(String phoneNumber, String billingMonth) {
        validatePhoneNumber(phoneNumber);
        validateBillingMonth(billingMonth);
        
        return getBillingInfo(phoneNumber, billingMonth);
    }

    /**
     * 요금 정보를 조회합니다.
     * 캐시에서 먼저 조회하고, 캐시에 없는 경우 KT 어댑터를 통해 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 요금 정보
     */
    private BillingInfoResponseDTO getBillingInfo(String phoneNumber, String billingMonth) {
        // 캐시에서 먼저 조회
        BillingInfoResponseDTO cachedInfo = cacheService.getCachedBillingInfo(phoneNumber, billingMonth);
        
        if (cachedInfo != null) {
            log.debug("Cache hit for {}, {}", phoneNumber, billingMonth);
            return cachedInfo;
        }
        
        // 캐시에 없는 경우 KT 어댑터를 통해 조회
        log.debug("Cache miss for {}, {}. Fetching from KT adapter.", phoneNumber, billingMonth);
        BillingInfoResponseDTO billingInfo = ktAdapter.getBillingInfo(phoneNumber, billingMonth);
        
        // 조회 결과를 캐시에 저장
        cacheService.cacheBillingInfo(phoneNumber, billingMonth, billingInfo);
        
        return billingInfo;
    }

    /**
     * 이전 월을 계산합니다.
     *
     * @param currentMonth 현재 월 (YYYYMM 형식)
     * @return 이전 월 (YYYYMM 형식)
     */
    private String calculatePreviousMonth(String currentMonth) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
            LocalDate date = LocalDate.parse(currentMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate previousMonth = date.minusMonths(1);
            return previousMonth.format(formatter);
        } catch (Exception e) {
            log.error("Error calculating previous month for {}: {}", currentMonth, e.getMessage());
            throw new BizException(ErrorCode.BAD_REQUEST, "Invalid billing month format");
        }
    }

    /**
     * 전화번호의 유효성을 검사합니다.
     *
     * @param phoneNumber 검사할 전화번호
     * @throws BizException 유효하지 않은 전화번호인 경우
     */
    private void validatePhoneNumber(String phoneNumber) {
        if (!ValidationUtil.validatePhoneNumber(phoneNumber)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "Invalid phone number format");
        }
    }

    /**
     * 청구 년월의 유효성을 검사합니다.
     *
     * @param billingMonth 검사할 청구 년월 (YYYYMM 형식)
     * @throws BizException 유효하지 않은 청구 년월인 경우
     */
    private void validateBillingMonth(String billingMonth) {
        if (billingMonth == null || !billingMonth.matches("^\\d{6}$")) {
            throw new BizException(ErrorCode.BAD_REQUEST, "Invalid billing month format");
        }
        
        try {
            int year = Integer.parseInt(billingMonth.substring(0, 4));
            int month = Integer.parseInt(billingMonth.substring(4, 6));
            if (year < 2000 || year > 2100 || month < 1 || month > 12) {
                throw new BizException(ErrorCode.BAD_REQUEST, "Invalid billing month value");
            }
        } catch (NumberFormatException e) {
            throw new BizException(ErrorCode.BAD_REQUEST, "Invalid billing month format");
        }
    }
}


