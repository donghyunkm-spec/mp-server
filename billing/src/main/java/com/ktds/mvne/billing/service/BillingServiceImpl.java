package com.ktds.mvne.billing.service;

import com.ktds.mvne.billing.adapter.KTAdapter;
import com.ktds.mvne.billing.dto.BillingInfoResponseDTO;
import com.ktds.mvne.billing.dto.BillingStatusResponse;
import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import com.ktds.mvne.common.util.ValidationUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
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
    private final Counter billingRequestCounter;
    private final Counter billingSuccessCounter;
    private final Counter billingErrorCounter;
    private final Counter ktSystemRequestCounter;
    private final Timer ktAdapterOperationTimer;

    /**
     * 현재 또는 전월 요금을 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 요금 정보
     */
    @Override
    public BillingInfoResponseDTO getCurrentBilling(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        billingRequestCounter.increment();

        try {
            // 당월 청구 데이터 존재 여부 확인
            ktSystemRequestCounter.increment();
            Timer.Sample ktSample = Timer.start();
            BillingStatusResponse statusResponse = ktAdapter.checkBillingStatus(phoneNumber);
            ktSample.stop(ktAdapterOperationTimer);

            log.debug("BillingStatus for {}: {}", phoneNumber, statusResponse);

            // statusResponse가 null이면 기본 응답 생성
            if (statusResponse == null) {
                log.warn("BillingStatusResponse is null for phoneNumber: {}", phoneNumber);
                billingErrorCounter.increment();
                return createDefaultBillingInfo(phoneNumber, getCurrentMonth());
            }

            if (statusResponse.isBillingGenerated()) {
                // 당월 청구 데이터가 생성된 경우
                BillingInfoResponseDTO response = getBillingInfo(phoneNumber, statusResponse.getCurrentBillingMonth());
                billingSuccessCounter.increment();
                return response;
            } else {
                // 당월 청구 데이터가 생성되지 않은 경우 전월 데이터 조회
                String previousMonth = calculatePreviousMonth(statusResponse.getCurrentBillingMonth());
                BillingInfoResponseDTO response = getBillingInfo(phoneNumber, previousMonth);
                billingSuccessCounter.increment();
                return response;
            }
        } catch (Exception e) {
            log.error("Error retrieving current billing for {}: {}", phoneNumber, e.getMessage(), e);
            billingErrorCounter.increment();
            // 예외가 발생하더라도 기본 응답 제공
            return createDefaultBillingInfo(phoneNumber, getCurrentMonth());
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
        billingRequestCounter.increment();

        try {
            BillingInfoResponseDTO response = getBillingInfo(phoneNumber, billingMonth);
            billingSuccessCounter.increment();
            return response;
        } catch (Exception e) {
            log.error("Error retrieving specific billing for {}, {}: {}", phoneNumber, billingMonth, e.getMessage(), e);
            billingErrorCounter.increment();
            // 예외가 발생하더라도 기본 응답 제공
            return createDefaultBillingInfo(phoneNumber, billingMonth);
        }
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

            // phoneNumber가 null인 경우 처리
            if (cachedInfo.getPhoneNumber() == null || cachedInfo.getPhoneNumber().isEmpty()) {
                cachedInfo.setPhoneNumber(phoneNumber);
            }

            // billingMonth가 null인 경우 처리
            if (cachedInfo.getBillingMonth() == null || cachedInfo.getBillingMonth().isEmpty()) {
                cachedInfo.setBillingMonth(billingMonth);
            }

            return cachedInfo;
        }

        // 캐시에 없는 경우 KT 어댑터를 통해 조회
        log.debug("Cache miss for {}, {}. Fetching from KT adapter.", phoneNumber, billingMonth);
        BillingInfoResponseDTO billingInfo = ktAdapter.getBillingInfo(phoneNumber, billingMonth);

        // 응답이 null인 경우 기본 응답 생성
        if (billingInfo == null) {
            log.warn("BillingInfoResponseDTO is null for phoneNumber: {}, billingMonth: {}", phoneNumber, billingMonth);
            billingInfo = createDefaultBillingInfo(phoneNumber, billingMonth);
        }

        // phoneNumber가 null인 경우 처리
        if (billingInfo.getPhoneNumber() == null || billingInfo.getPhoneNumber().isEmpty()) {
            billingInfo.setPhoneNumber(phoneNumber);
        }

        // billingMonth가 null인 경우 처리
        if (billingInfo.getBillingMonth() == null || billingInfo.getBillingMonth().isEmpty()) {
            billingInfo.setBillingMonth(billingMonth);
        }

        // 응답이 유효한 경우에만 캐시에 저장
        if (billingInfo != null && billingInfo.getDetails() != null) {
            // 조회 결과를 캐시에 저장
            cacheService.cacheBillingInfo(phoneNumber, billingMonth, billingInfo);
        }

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
     * 현재 월을 YYYYMM 형식으로 반환합니다.
     *
     * @return 현재 월 (YYYYMM 형식)
     */
    private String getCurrentMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    /**
     * 기본 요금 정보 응답을 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 기본 요금 정보 응답
     */
    private BillingInfoResponseDTO createDefaultBillingInfo(String phoneNumber, String billingMonth) {
        return BillingInfoResponseDTO.builder()
                .phoneNumber(phoneNumber)
                .billingMonth(billingMonth)
                .totalFee(0)
                .details(java.util.Collections.emptyList())
                .discounts(java.util.Collections.emptyList())
                .build();
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