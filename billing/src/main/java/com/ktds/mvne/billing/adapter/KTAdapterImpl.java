package com.ktds.mvne.billing.adapter;

import com.ktds.mvne.billing.dto.*;
import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import com.ktds.mvne.common.exception.ExternalSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * KT 영업시스템과의 통신을 담당하는 어댑터 구현체입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KTAdapterImpl implements KTAdapter {

    private final WebClient webClient;

    @Value("${kos-adapter.base-url}")
    private String kosAdapterBaseUrl;

    /**
     * 당월 청구 데이터 존재 여부를 확인합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 청구 상태 정보
     */
    @Override
    public BillingStatusResponse checkBillingStatus(String phoneNumber) {
        log.debug("Checking billing status for phoneNumber: {}", phoneNumber);
        try {
            BillingStatusResponse response = webClient.get()
                    .uri(kosAdapterBaseUrl + "/api/kos/billings/billing-status?phoneNumber=" + phoneNumber)
                    .retrieve()
                    .bodyToMono(BillingStatusResponse.class)
                    .onErrorResume(this::handleError)
                    .block();

            // 응답이 null인 경우 기본 응답 생성
            if (response == null) {
                log.warn("BillingStatusResponse is null for phoneNumber: {}", phoneNumber);
                response = new BillingStatusResponse();
                response.setPhoneNumber(phoneNumber);
                response.setCurrentBillingMonth(getCurrentMonth());
                response.setBillingGenerated(true);
            }

            // phoneNumber가 null인 경우 처리
            if (response.getPhoneNumber() == null || response.getPhoneNumber().isEmpty()) {
                response.setPhoneNumber(phoneNumber);
            }

            return response;
        } catch (Exception e) {
            log.error("Error checking billing status for {}: {}", phoneNumber, e.getMessage(), e);
            throw new ExternalSystemException("KT 영업시스템 연결 오류 checkBillingStatus: " + e.getMessage(),
                    HttpStatus.SERVICE_UNAVAILABLE.value(), "KOS");
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
    public BillingInfoResponseDTO getBillingInfo(String phoneNumber, String billingMonth) {
        log.debug("Getting billing info for phoneNumber: {}, billingMonth: {}", phoneNumber, billingMonth);
        try {
            BillingInfoResponseDTO response = webClient.get()
                    .uri(kosAdapterBaseUrl + "/api/kos/billings/info?phoneNumber=" + phoneNumber
                            + "&billingMonth=" + billingMonth)
                    .retrieve()
                    .bodyToMono(BillingInfoResponseDTO.class)
                    .onErrorResume(this::handleError)
                    .block();

            // 응답이 null인 경우 기본 응답 생성
            if (response == null) {
                log.warn("BillingInfoResponseDTO is null for phoneNumber: {}, billingMonth: {}", phoneNumber, billingMonth);
                response = BillingInfoResponseDTO.builder()
                        .phoneNumber(phoneNumber)
                        .billingMonth(billingMonth)
                        .totalFee(0)
                        .details(java.util.Collections.emptyList())
                        .discounts(java.util.Collections.emptyList())
                        .build();
            }

            // phoneNumber가 null인 경우 처리
            if (response.getPhoneNumber() == null || response.getPhoneNumber().isEmpty()) {
                response.setPhoneNumber(phoneNumber);
            }

            // billingMonth가 null인 경우 처리
            if (response.getBillingMonth() == null || response.getBillingMonth().isEmpty()) {
                response.setBillingMonth(billingMonth);
            }

            return response;
        } catch (Exception e) {
            log.error("Error getting billing info for {}, {}: {}", phoneNumber, billingMonth, e.getMessage(), e);
            throw new ExternalSystemException("KT 영업시스템 연결 오류 getBillingInfo: " + e.getMessage(),
                    HttpStatus.SERVICE_UNAVAILABLE.value(), "KOS");
        }
    }

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @Override
    public CustomerInfoResponseDTO getCustomerInfo(String phoneNumber) {
        log.debug("Getting customer info for phoneNumber: {}", phoneNumber);
        try {
            CustomerInfoResponseDTO response = webClient.get()
                    .uri(kosAdapterBaseUrl + "/api/kos/customers/" + phoneNumber)
                    .retrieve()
                    .bodyToMono(CustomerInfoResponseDTO.class)
                    .onErrorResume(this::handleError)
                    .block();

            // 응답이 null인 경우 기본 응답 생성
            if (response == null) {
                log.warn("CustomerInfoResponseDTO is null for phoneNumber: {}", phoneNumber);
                response = new CustomerInfoResponseDTO();
                response.setPhoneNumber(phoneNumber);
                response.setStatus("UNKNOWN");

                ProductInfoDTO emptyProduct = new ProductInfoDTO();
                emptyProduct.setProductCode("UNKNOWN");
                emptyProduct.setProductName("Unknown Product");
                emptyProduct.setFee(0);
                response.setCurrentProduct(emptyProduct);
            }

            // phoneNumber가 null인 경우 처리
            if (response.getPhoneNumber() == null || response.getPhoneNumber().isEmpty()) {
                response.setPhoneNumber(phoneNumber);
            }

            // currentProduct가 null인 경우 처리
            if (response.getCurrentProduct() == null) {
                ProductInfoDTO emptyProduct = new ProductInfoDTO();
                emptyProduct.setProductCode("UNKNOWN");
                emptyProduct.setProductName("Unknown Product");
                emptyProduct.setFee(0);
                response.setCurrentProduct(emptyProduct);
            }

            return response;
        } catch (Exception e) {
            log.error("Error getting customer info for {}: {}", phoneNumber, e.getMessage(), e);
            throw new ExternalSystemException("KT 영업시스템 연결 오류 getCustomerInfo: " + e.getMessage(),
                    HttpStatus.SERVICE_UNAVAILABLE.value(), "KOS");
        }
    }

    /**
     * WebClient 오류를 처리합니다.
     *
     * @param <T> 응답 데이터 타입
     * @param throwable 발생한 예외
     * @return 오류를 적절히 처리한 Mono
     */
    private <T> Mono<T> handleError(Throwable throwable) {
        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) throwable;
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

            log.error("KT Adapter error: {} - {}", status, ex.getResponseBodyAsString());

            if (status.is4xxClientError()) {
                return Mono.error(new BizException(ErrorCode.BAD_REQUEST,
                        "요청 오류: " + ex.getResponseBodyAsString()));
            } else {
                return Mono.error(new ExternalSystemException("KT 영업시스템 오류 handleError: " + ex.getResponseBodyAsString(),
                        status.value(), "KOS"));
            }
        }
        return Mono.error(throwable);
    }

    /**
     * 현재 월을 YYYYMM 형식으로 반환합니다.
     *
     * @return 현재 월 (YYYYMM 형식)
     */
    private String getCurrentMonth() {
        return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
    }
}