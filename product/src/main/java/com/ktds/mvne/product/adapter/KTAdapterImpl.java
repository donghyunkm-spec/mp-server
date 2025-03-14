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
        // 기존 구현 코드
        return null; // 실제 구현으로 대체
    }

    @Override
    public ProductInfoDTO getProductInfo(String productCode) {
        // 기존 구현 코드
        return null; // 실제 구현으로 대체
    }

    @Override
    public ProductChangeResponse changeProduct(String phoneNumber, String productCode, String changeReason) {
        log.info("KT 어댑터 - 상품 변경 요청 - 회선번호: {}, 상품코드: {}, 변경사유: {}",
                phoneNumber, productCode, changeReason);

        try {
            String url = kosAdapterBaseUrl + "/api/kos/products/change";

            // 요청 객체 생성
            // 실제 구현에서는 요청 DTO 클래스를 별도로 만들어 사용할 수 있습니다.
            ProductChangeRequest request = new ProductChangeRequest(phoneNumber, productCode, changeReason);

            // KOS 어댑터 호출
            ProductChangeResponse response = restTemplate.postForObject(url, request, ProductChangeResponse.class);

            log.info("KT 어댑터 - 상품 변경 응답: {}", response);
            return response;

        } catch (Exception e) {
            log.error("KT 어댑터 - 상품 변경 실패: {}", e.getMessage(), e);
            throw new ExternalSystemException("KT 시스템 연동 중 오류가 발생했습니다: " + e.getMessage(), 500, "KOS");
        }
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