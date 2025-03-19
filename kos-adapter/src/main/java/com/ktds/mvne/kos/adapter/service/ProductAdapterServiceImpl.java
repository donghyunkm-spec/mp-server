// File: mp-server\kos-adapter\src\main\java\com\ktds\mvne\kos\adapter\service\ProductAdapterServiceImpl.java
package com.ktds.mvne.kos.adapter.service;

import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import com.ktds.mvne.common.exception.ExternalSystemException;
import com.ktds.mvne.common.util.ValidationUtil;
import com.ktds.mvne.kos.adapter.client.KOSClient;
import com.ktds.mvne.kos.adapter.dto.CustomerInfoResponse;
import com.ktds.mvne.kos.adapter.dto.ProductChangeResponse;
import com.ktds.mvne.kos.adapter.dto.ProductDetail;
import com.ktds.mvne.kos.adapter.util.XmlConverter;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * KT 영업시스템의 상품 관련 어댑터 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAdapterServiceImpl implements ProductAdapterService {

    private final KOSClient kosClient;
    private final XmlConverter xmlConverter;

    @Qualifier("productCircuitBreaker")
    private final CircuitBreaker circuitBreaker;

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @Override
    public CustomerInfoResponse getCustomerInfo(String phoneNumber) {
        validatePhoneNumber(phoneNumber);

        try {
            String requestXml = xmlConverter.convertToSoapXml(
                    new CustomerInfoRequest(phoneNumber));

            String responseXml = circuitBreaker.executeSupplier(() ->
                    kosClient.sendRequest(requestXml, "customer-info"));

            CustomerInfoResponse response = xmlConverter.convertToJson(responseXml, CustomerInfoResponse.class);
            log.debug("CustomerInfo response after XML conversion: {}", response);

            log.debug("CustomerInfo response for {}: {}", phoneNumber, response);
            return response;

        } catch (Exception e) {
            handleException(e, "고객 정보 조회 중 오류 발생", phoneNumber);
            return null; // 도달하지 않음 (handleException에서 예외 발생)
        }
    }

    /**
     * 상품 정보를 조회합니다.
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    @Override
    public ProductDetail getProductInfo(String productCode) {
        validateProductCode(productCode);

        try {
            String requestXml = xmlConverter.convertToSoapXml(
                    new ProductInfoRequest(productCode));

            String responseXml = circuitBreaker.executeSupplier(() ->
                    kosClient.sendRequest(requestXml, "product-info"));

            ProductDetail response = xmlConverter.convertToJson(
                    responseXml, ProductDetail.class);

            // 수정: KOS Mock에서 반환한 상품 코드와 요청한 상품 코드가 다른 경우 처리
            if (response != null && (response.getProductCode() == null ||
                    !response.getProductCode().equals(productCode))) {
                log.warn("응답의 상품 코드({})가 요청한 상품 코드({})와 다릅니다. 요청 값으로 설정합니다.",
                        response.getProductCode(), productCode);
                response.setProductCode(productCode);
            }

            log.debug("ProductInfo response for {}: {}", productCode, response);
            return response;

        } catch (Exception e) {
            handleException(e, "상품 정보 조회 중 오류 발생", productCode);
            return null; // 도달하지 않음 (handleException에서 예외 발생)
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
        validatePhoneNumber(phoneNumber);
        validateProductCode(productCode);

        try {
            // ProductChangeRequest 객체에 파라미터 명확히 포함
            String requestXml = xmlConverter.convertToSoapXml(
                    new ProductChangeRequest(phoneNumber, productCode, changeReason));

            // 로그 추가하여 XML 요청 내용 확인
            log.debug("Product change request XML: {}", requestXml);

            String responseXml = circuitBreaker.executeSupplier(() ->
                    kosClient.sendRequest(requestXml, "change"));

            ProductChangeResponse response = xmlConverter.convertToJson(
                    responseXml, ProductChangeResponse.class);

            log.debug("ProductChange response for {}, {}: {}",
                    phoneNumber, productCode, response);
            return response;

        } catch (Exception e) {
            handleException(e, "상품 변경 중 오류 발생", phoneNumber);
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
     * 상품 코드의 유효성을 검사합니다.
     *
     * @param productCode 검사할 상품 코드
     * @throws BizException 유효하지 않은 상품 코드인 경우
     */
    private void validateProductCode(String productCode) {
        if (!ValidationUtil.validateProductCode(productCode)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "Invalid product code format");
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
     * 고객 정보 조회 요청 DTO입니다.
     * 어댑터 내부에서만 사용됩니다.
     */
    private record CustomerInfoRequest(String phoneNumber) {
    }

    /**
     * 상품 정보 조회 요청 DTO입니다.
     * 어댑터 내부에서만 사용됩니다.
     */
    private record ProductInfoRequest(String productCode) {
    }

    /**
     * 상품 변경 요청 DTO입니다.
     * 어댑터 내부에서만 사용됩니다.
     */
    private record ProductChangeRequest(String phoneNumber, String productCode, String changeReason) {
    }
}