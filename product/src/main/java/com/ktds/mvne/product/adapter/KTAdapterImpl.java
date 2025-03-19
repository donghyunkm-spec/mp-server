package com.ktds.mvne.product.adapter;

import com.ktds.mvne.product.dto.CustomerInfoResponseDTO;
import com.ktds.mvne.product.dto.ProductChangeRequest;
import com.ktds.mvne.product.dto.ProductChangeResponse;
import com.ktds.mvne.product.dto.ProductInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * KT 영업시스템과의 연동을 위한 어댑터 구현체입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KTAdapterImpl implements KTAdapter {

    private final WebClient webClient;

    @Value("${kos.adapter.base-url}")
    private String kosAdapterBaseUrl;

    @Override
    public CustomerInfoResponseDTO getCustomerInfo(String phoneNumber) {
        log.info("KT 어댑터 - 고객 정보 조회 요청 - 회선번호: {}", phoneNumber);

        return webClient.get()
                .uri(kosAdapterBaseUrl + "/api/kos/customers/" + phoneNumber)
                .retrieve()
                .bodyToMono(CustomerInfoResponseDTO.class)
                .onErrorResume(e -> {
                    log.error("KT 어댑터 - 고객 정보 조회 실패: {}", e.getMessage(), e);
                    return Mono.just(createDefaultCustomerInfo(phoneNumber));
                })
                .block();
    }

    @Override
    public ProductInfoDTO getProductInfo(String productCode) {
        log.info("KT 어댑터 - 상품 정보 조회 요청 - 상품 코드: {}", productCode);

        return webClient.get()
                .uri(kosAdapterBaseUrl + "/api/kos/products/" + productCode)
                .retrieve()
                .bodyToMono(ProductInfoDTO.class)
                .onErrorResume(e -> {
                    log.error("KT 어댑터 - 상품 정보 조회 실패: {}", e.getMessage(), e);
                    return Mono.just(createDefaultProductInfo(productCode));
                })
                .block();
    }

    /**
     * 기본 고객 정보 객체를 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 기본 고객 정보 DTO
     */
    private CustomerInfoResponseDTO createDefaultCustomerInfo(String phoneNumber) {
        CustomerInfoResponseDTO defaultResponse = new CustomerInfoResponseDTO();
        defaultResponse.setPhoneNumber(phoneNumber);
        defaultResponse.setStatus("UNKNOWN");

        ProductInfoDTO defaultProduct = new ProductInfoDTO();
        defaultProduct.setProductCode("UNKNOWN");
        defaultProduct.setProductName("Unknown Product");
        defaultProduct.setFee(0);
        defaultResponse.setCurrentProduct(defaultProduct);

        return defaultResponse;
    }

    /**
     * 기본 상품 정보 객체를 생성합니다.
     *
     * @param productCode 상품 코드
     * @return 기본 상품 정보 DTO
     */
    private ProductInfoDTO createDefaultProductInfo(String productCode) {
        ProductInfoDTO product = new ProductInfoDTO();
        product.setProductCode(productCode);
        product.setProductName("Unknown Product");
        product.setFee(0);
        return product;
    }

    @Override
    public ProductChangeResponse changeProduct(String phoneNumber, String productCode, String changeReason) {
        log.info("KT 어댑터 - 상품 변경 요청 - 회선번호: {}, 상품코드: {}, 변경사유: {}",
                phoneNumber, productCode, changeReason);

        ProductChangeRequest request = new ProductChangeRequest(phoneNumber, productCode, changeReason);

        return webClient.post()
                .uri(kosAdapterBaseUrl + "/api/kos/products/change")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ProductChangeResponse.class)
                .onErrorResume(e -> {
                    log.error("KT 어댑터 - 상품 변경 실패: {}", e.getMessage(), e);
                    return Mono.just(createDefaultChangeResponse(phoneNumber, productCode));
                })
                .block();
    }

    /**
     * 기본 상품 변경 응답 객체를 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 상품 코드
     * @return 기본 상품 변경 응답
     */
    private ProductChangeResponse createDefaultChangeResponse(String phoneNumber, String productCode) {
        return ProductChangeResponse.builder()
                .success(false)
                .message("상품 변경 응답을 받을 수 없습니다.")
                .transactionId("UNKNOWN")
                .build();
    }
}