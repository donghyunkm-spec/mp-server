package com.ktds.mvne.kos.adapter.service;

import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import com.ktds.mvne.common.exception.ExternalSystemException;
import com.ktds.mvne.common.util.ValidationUtil;
import com.ktds.mvne.kos.adapter.client.KOSClient;
import com.ktds.mvne.kos.adapter.dto.BillingInfoResponse;
import com.ktds.mvne.kos.adapter.dto.BillingStatusResponse;
import com.ktds.mvne.kos.adapter.util.XmlConverter;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * KT 영업시스템의 요금 조회 관련 어댑터 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillingAdapterServiceImpl implements BillingAdapterService {

    private final KOSClient kosClient;
    private final XmlConverter xmlConverter;

    @Qualifier("billingCircuitBreaker")
    private final CircuitBreaker circuitBreaker;

    /**
     * 당월 청구 데이터 존재 여부를 확인합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 청구 상태 정보
     */
    @Override
    public BillingStatusResponse checkBillingStatus(String phoneNumber) {
        validatePhoneNumber(phoneNumber);

        try {
            String requestXml = xmlConverter.convertToSoapXml(
                    new BillingStatusRequest(phoneNumber));

            String responseXml = circuitBreaker.executeSupplier(() ->
                    kosClient.sendRequest(requestXml, "billing-status"));

            BillingStatusResponse response = xmlConverter.convertToJson(
                    responseXml, BillingStatusResponse.class);

            log.debug("BillingStatus response for {}: {}", phoneNumber, response);
            return response;

        } catch (Exception e) {
            handleException(e, "청구 상태 확인 중 오류 발생", phoneNumber);
            return null; // 도달하지 않음 (handleException에서 예외 발생)
        }
    }

    /**
     * 요금 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 요금 정보
     */
    @Override
    public BillingInfoResponse getBillingInfo(String phoneNumber, String billingMonth) {
        validatePhoneNumber(phoneNumber);
        validateBillingMonth(billingMonth);

        try {
            String requestXml = xmlConverter.convertToSoapXml(
                    new BillingInfoRequest(phoneNumber, billingMonth));

            String responseXml = circuitBreaker.executeSupplier(() ->
                    kosClient.sendRequest(requestXml, "info"));

            BillingInfoResponse response = xmlConverter.convertToJson(
                    responseXml, BillingInfoResponse.class);

            log.debug("BillingInfo response for {}, {}: {}", phoneNumber, billingMonth, response);
            return response;

        } catch (Exception e) {
            handleException(e, "요금 정보 조회 중 오류 발생", phoneNumber);
            return null; // 도달하지 않음 (handleException에서 예외 발생)
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

    /**
     * 예외를 처리합니다.
     *
     * @param e 발생한 예외
     * @param message 오류 메시지
     * @param identifier 식별자 (전화번호 또는 상품 코드)
     * @throws BizException 비즈니스 예외
     * @throws ExternalSystemException 외부 시스템 예외
     */
    private void handleException(Exception e, String message, String identifier) {
        log.error("{} (identifier: {}): {}", message, identifier, e.getMessage(), e);

        if (e instanceof BizException) {
            throw (BizException) e;
        } else if (e instanceof ExternalSystemException) {
            throw (ExternalSystemException) e;
        } else {
            throw new ExternalSystemException(message + ": " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "KOS");
        }
    }

    /**
     * 청구 상태 확인 요청 DTO입니다.
     * 어댑터 내부에서만 사용됩니다.
     */
    private record BillingStatusRequest(String phoneNumber) {
    }

    /**
     * 요금 정보 조회 요청 DTO입니다.
     * 어댑터 내부에서만 사용됩니다.
     */
    private record BillingInfoRequest(String phoneNumber, String billingMonth) {
    }
}