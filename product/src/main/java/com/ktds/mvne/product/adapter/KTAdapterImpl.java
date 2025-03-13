package com.ktds.mvne.product.adapter;

import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import com.ktds.mvne.common.exception.ExternalSystemException;
import com.ktds.mvne.product.dto.CustomerInfoResponseDTO;
import com.ktds.mvne.product.dto.ProductChangeResponse;
import com.ktds.mvne.product.dto.ProductInfoDTO;
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
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @Override
    public CustomerInfoResponseDTO getCustomerInfo(String phoneNumber) {
        log.debug("Getting customer info for phoneNumber: {}", phoneNumber);
        try {
            return webClient.get()
                    .uri(kosAdapterBaseUrl + "/api/kos/customers/" + phoneNumber)
                    .retrieve()
                    .bodyToMono(CustomerInfoResponseDTO.class)
                    .onErrorResume(this::handleError)
                    .block();
        } catch (Exception e) {
            log.error("Error getting customer info for {}: {}", phoneNumber, e.getMessage(), e);
            throw new ExternalSystemException("KT 영업시스템 연결 오류: " + e.getMessage(), 
                    HttpStatus.SERVICE_UNAVAILABLE.value(), "KOS");
        }
    }

    /**
     * 상품 정보를 조회합니다.
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    @Override
    public ProductInfoDTO getProductInfo(String productCode) {
        log.debug("Getting product info for productCode: {}", productCode);
        try {
            return webClient.get()
                    .uri(kosAdapterBaseUrl + "/api/kos/products/" + productCode)
                    .retrieve()
                    .bodyToMono(ProductInfoDTO.class)
                    .onErrorResume(this::handleError)
                    .block();
        } catch (Exception e) {
            log.error("Error getting product info for {}: {}", productCode, e.getMessage(), e);
            throw new ExternalSystemException("KT 영업시스템 연결 오류: " + e.getMessage(), 
                    HttpStatus.SERVICE_UNAVAILABLE.value(), "KOS");
        }
    }

    /**
     * 상품을 변경합니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 변경하려는 상품 코드
     * @param changeReason 변경 사유
     * @return 상품 변경 결과
     */
    @Override
    public ProductChangeResponse changeProduct(String phoneNumber, String productCode, String changeReason) {
        log.debug("Changing product for phoneNumber: {}, productCode: {}, reason: {}", 
                phoneNumber, productCode, changeReason);
        try {
            return webClient.post()
                    .uri(kosAdapterBaseUrl + "/api/kos/products/change")
                    .bodyValue(new ProductChangeRequest(phoneNumber, productCode, changeReason))
                    .retrieve()
                    .bodyToMono(ProductChangeResponse.class)
                    .onErrorResume(this::handleError)
                    .block();
        } catch (Exception e) {
            log.error("Error changing product for {}, {}: {}", phoneNumber, productCode, e.getMessage(), e);
            throw new ExternalSystemException("KT 영업시스템 연결 오류: " + e.getMessage(), 
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
                return Mono.error(new ExternalSystemException("KT 영업시스템 오류: " + ex.getResponseBodyAsString(), 
                        status.value(), "KOS"));
            }
        }
        return Mono.error(throwable);
    }

    /**
     * 상품 변경 요청 DTO입니다.
     * 어댑터 내부에서만 사용됩니다.
     */
    private record ProductChangeRequest(String phoneNumber, String productCode, String changeReason) {
    }
}
