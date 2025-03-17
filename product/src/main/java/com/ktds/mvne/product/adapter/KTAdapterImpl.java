package com.ktds.mvne.product.adapter;

import com.ktds.mvne.common.exception.ExternalSystemException;
import com.ktds.mvne.product.dto.CustomerInfoResponseDTO;
import com.ktds.mvne.product.dto.ProductChangeResponse;
import com.ktds.mvne.product.dto.ProductInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

            CustomerInfoResponseDTO response = restTemplate.getForObject(url, CustomerInfoResponseDTO.class);
            log.info("KT 어댑터 - 고객 정보 조회 응답: {}", response);

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
            throw new ExternalSystemException("KT 시스템 연동 중 오류가 발생했습니다: " + e.getMessage(), 500, "KOS");
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

            // 상품 코드 설정
            if (response.getProductCode() == null || response.getProductCode().isEmpty()) {
                response.setProductCode(productCode);
            }

            return response;

        } catch (Exception e) {
            log.error("KT 어댑터 - 상품 정보 조회 실패: {}", e.getMessage(), e);
            throw new ExternalSystemException("KT 시스템 연동 중 오류가 발생했습니다: " + e.getMessage(), 500, "KOS");
        }
    }

    @Override
    public ProductChangeResponse changeProduct(String phoneNumber, String productCode, String changeReason) {
        log.info("KT 어댑터 - 상품 변경 요청 - 회선번호: {}, 상품코드: {}, 변경사유: {}",
                phoneNumber, productCode, changeReason);

        try {
            String url = kosAdapterBaseUrl + "/api/kos/products/change";

            // 요청 객체 생성
            ProductChangeRequest request = new ProductChangeRequest(phoneNumber, productCode, changeReason);

            // KOS 어댑터 호출
            ProductChangeResponse response = restTemplate.postForObject(url, request, ProductChangeResponse.class);

            log.info("KT 어댑터 - 상품 변경 응답: {}", response);

            // null 체크
            if (response == null) {
                log.warn("KT 어댑터 - 응답이 null입니다");
                return createDefaultChangeResponse(phoneNumber, productCode);
            }

            return response;

        } catch (Exception e) {
            log.error("KT 어댑터 - 상품 변경 실패: {}", e.getMessage(), e);
            throw new ExternalSystemException("KT 시스템 연동 중 오류가 발생했습니다: " + e.getMessage(), 500, "KOS");
        }
    }

    /**
     * 기본 상품 정보 객체를 생성합니다.
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
     */
    private ProductChangeResponse createDefaultChangeResponse(String phoneNumber, String productCode) {
        return ProductChangeResponse.builder()
                .success(false)
                .message("상품 변경 응답을 받을 수 없습니다.")
                .transactionId("UNKNOWN")
                .build();
    }
}

// 요청 객체를 위한 DTO 클래스
class ProductChangeRequest {
    private String phoneNumber;
    private String productCode;
    private String changeReason;

    public ProductChangeRequest(String phoneNumber, String productCode, String changeReason) {
        this.phoneNumber = phoneNumber;
        this.productCode = productCode;
        this.changeReason = changeReason;
    }

    // Getter, Setter
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
}