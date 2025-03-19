// File: mp-server\product\src\main\java\com\ktds\mvne\product\adapter\KTAdapterImpl.java
package com.ktds.mvne.product.adapter;

import com.ktds.mvne.product.dto.CustomerInfoResponseDTO;
import com.ktds.mvne.product.dto.ProductChangeRequest;
import com.ktds.mvne.product.dto.ProductChangeResponse;
import com.ktds.mvne.product.dto.ProductInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * KT 영업시스템과의 연동을 위한 어댑터 구현체입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KTAdapterImpl implements KTAdapter {

    private final RestTemplate restTemplate;

    @Value("${kos.adapter.base-url}")
    private String kosAdapterBaseUrl;

    @Override
    public CustomerInfoResponseDTO getCustomerInfo(String phoneNumber) {
        log.info("KT 어댑터 - 고객 정보 조회 요청 - 회선번호: {}", phoneNumber);

        try {
            String url = kosAdapterBaseUrl + "/api/kos/customers/" + phoneNumber;
            log.debug("KOS 어댑터 호출 URL: {}", url);

            // API 응답 구조가 변경되었으므로 직접 객체를 가져옵니다
            CustomerInfoResponseDTO response = restTemplate.getForObject(url, CustomerInfoResponseDTO.class);
            log.info("KT 어댑터 - 고객 정보 조회 응답: {}", response);

            // 응답의 status를 로깅하여 값을 확인합니다
            log.debug("응답의 status 값: {}", response != null ? response.getStatus() : "null");

            // status가 숫자 형태인 경우 "사용중"으로 변환합니다
            if (response != null && response.getStatus() != null && response.getStatus().matches("\\d+")) {
                log.info("숫자 형태의 status({})를 '사용중'으로 변환합니다", response.getStatus());
                response.setStatus("사용중");
            }

            // null 체크 및 기본값 설정
            if (response == null) {
                log.warn("KT 어댑터 - 응답이 null입니다");
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

            // 전화번호 설정
            if (response.getPhoneNumber() == null || response.getPhoneNumber().isEmpty()) {
                response.setPhoneNumber(phoneNumber);
            }

            // 상품 정보 설정
            if (response.getCurrentProduct() == null) {
                ProductInfoDTO defaultProduct = new ProductInfoDTO();
                defaultProduct.setProductCode("UNKNOWN");
                defaultProduct.setProductName("Unknown Product");
                defaultProduct.setFee(0);
                response.setCurrentProduct(defaultProduct);
            }

            return response;

        } catch (Exception e) {
            log.error("KT 어댑터 - 고객 정보 조회 실패: {}", e.getMessage(), e);
            // ExternalSystemException 대신 RuntimeException 사용
            throw new RuntimeException("KT 시스템 연동 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public ProductInfoDTO getProductInfo(String productCode) {
        log.info("KT 어댑터 - 상품 정보 조회 요청 - 상품 코드: {}", productCode);

        try {
            String url = kosAdapterBaseUrl + "/api/kos/products/" + productCode;
            log.debug("KOS 어댑터 호출 URL: {}", url);

            ProductInfoDTO response = restTemplate.getForObject(url, ProductInfoDTO.class);
            log.info("KT 어댑터 - 상품 정보 조회 응답: {}", response);

            // null 체크 및 기본값 설정
            if (response == null) {
                log.warn("KT 어댑터 - 응답이 null입니다");
                return createDefaultProductInfo(productCode);
            }

            // 상품 코드 설정 - 여기서 중요 수정: 원래 요청한 productCode로 맞춰줌
            if (response.getProductCode() == null || response.getProductCode().isEmpty() ||
                    !response.getProductCode().equals(productCode)) {
                log.warn("KT 어댑터 - 응답의 상품 코드({})가 요청한 상품 코드({})와 다릅니다. 요청 값으로 설정합니다.",
                        response.getProductCode(), productCode);
                response.setProductCode(productCode);
            }

            return response;

        } catch (Exception e) {
            log.error("KT 어댑터 - 상품 정보 조회 실패: {}", e.getMessage(), e);
            // ExternalSystemException 대신 RuntimeException 사용
            throw new RuntimeException("KT 시스템 연동 중 오류가 발생했습니다: " + e.getMessage());
        }
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

    @Override
    public ProductChangeResponse changeProduct(String phoneNumber, String productCode, String changeReason) {
        log.info("KT 어댑터 - 상품 변경 요청 - 회선번호: {}, 상품코드: {}, 변경사유: {}",
                phoneNumber, productCode, changeReason);

        try {
            // URL에 쿼리 파라미터 추가 (중요 수정: REST 호출 시 phoneNumber 파라미터 명시적 추가)
            String url = UriComponentsBuilder.fromHttpUrl(kosAdapterBaseUrl + "/api/kos/products/change")
                    .queryParam("phoneNumber", phoneNumber)
                    .queryParam("productCode", productCode)
                    .queryParam("changeReason", changeReason)
                    .build()
                    .toUriString();

            log.debug("KOS 어댑터 호출 URL: {}", url);

            // 요청 객체 생성
            ProductChangeRequest request = new ProductChangeRequest(phoneNumber, productCode, changeReason);

            // KOS 어댑터 호출 (GET 방식으로 변경)
            ProductChangeResponse response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    ProductChangeResponse.class
            ).getBody();

            log.info("KT 어댑터 - 상품 변경 응답: {}", response);

            // null 체크
            if (response == null) {
                log.warn("KT 어댑터 - 응답이 null입니다");
                return createDefaultChangeResponse(phoneNumber, productCode);
            }

            return response;

        } catch (Exception e) {
            log.error("KT 어댑터 - 상품 변경 실패: {}", e.getMessage(), e);
            // ExternalSystemException 대신 RuntimeException 사용
            throw new RuntimeException("KT 시스템 연동 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}