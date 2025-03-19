package com.ktds.mvne.kos.mock.service;

import com.ktds.mvne.kos.mock.util.XmlUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 상품 관련 목업 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MockProductService {

    private final XmlUtility xmlUtility;
    private final com.ktds.mvne.kos.mock.service.MockDataService mockDataService;

    /**
     * 고객 정보 XML 형식으로 조회
     * @param phoneNumber 휴대폰 번호
     * @return 고객 정보 XML
     */
    public String getCustomerInfoXml(String phoneNumber) {
        com.ktds.mvne.kos.mock.service.MockDataService.CustomerInfo customerInfo = mockDataService.getCustomerDb().get(phoneNumber);

        if (customerInfo == null) {
            return xmlUtility.wrapInSoapEnvelope(
                    "<customerInfoResponse>" +
                            "<error>해당 휴대폰 번호로 등록된 고객이 없습니다.</error>" +
                            "</customerInfoResponse>"
            );
        }

        return xmlUtility.wrapInSoapEnvelope(
                "<customerInfoResponse>" +
                        "<phoneNumber>" + customerInfo.getPhoneNumber() + "</phoneNumber>" +
                        "<status>" + customerInfo.getStatus() + "</status>" +
                        "<currentProduct>" +
                        "<productCode>" + customerInfo.getProductCode() + "</productCode>" +
                        "<productName>" + customerInfo.getProductName() + "</productName>" +
                        "<fee>" + customerInfo.getFee() + "</fee>" +
                        "</currentProduct>" +
                        "</customerInfoResponse>"
        );
    }

    /**
     * 상품 정보 XML 형식으로 조회
     * @param productCode 상품 코드
     * @return 상품 정보 XML
     */
    public String getProductInfoXml(String productCode) {
        com.ktds.mvne.kos.mock.service.MockDataService.ProductInfo productInfo = mockDataService.getProductDb().get(productCode);

        if (productInfo == null) {
            return xmlUtility.wrapInSoapEnvelope(
                    "<productDetail>" +
                            "<error>해당 상품 코드에 대한 정보가 없습니다.</error>" +
                            "</productDetail>"
            );
        }

        return xmlUtility.wrapInSoapEnvelope(
                "<productDetail>" +
                        "<productCode>" + productInfo.getProductCode() + "</productCode>" +
                        "<productName>" + productInfo.getProductName() + "</productName>" +
                        "<fee>" + productInfo.getFee() + "</fee>" +
                        "</productDetail>"
        );
    }

    /**
     * 상품 변경 가능 여부 XML 형식으로 조회
     * @param phoneNumber 휴대폰 번호
     * @param productCode 상품 코드
     * @return 변경 가능 여부 XML
     */
    public String checkProductChangeXml(String phoneNumber, String productCode) {
        Map<String, com.ktds.mvne.kos.mock.service.MockDataService.CustomerInfo> customerDb = mockDataService.getCustomerDb();
        Map<String, com.ktds.mvne.kos.mock.service.MockDataService.ProductInfo> productDb = mockDataService.getProductDb();

        com.ktds.mvne.kos.mock.service.MockDataService.CustomerInfo customer = customerDb.get(phoneNumber);
        com.ktds.mvne.kos.mock.service.MockDataService.ProductInfo product = productDb.get(productCode);

        boolean isAvailable = true;
        String message = "상품 변경이 가능합니다.";

        // 고객 존재 여부 체크
        if (customer == null) {
            isAvailable = false;
            message = "해당 휴대폰 번호로 등록된 고객이 없습니다.";
        }
        // 상품 존재 여부 체크
        else if (product == null) {
            isAvailable = false;
            message = "해당 상품 코드에 대한 정보가 없습니다.";
        }
        // 이미 같은 상품을 사용 중인지 체크
        else if (customer.getProductCode().equals(productCode)) {
            isAvailable = false;
            message = "이미 해당 상품을 사용 중입니다.";
        }
        // 고객 상태 체크 (정지 상태인 경우)
        else if ("SUSPENDED".equals(customer.getStatus())) {
            isAvailable = false;
            message = "정지 상태인 회선은 상품 변경이 불가합니다.";
        }
        // 상품 상태 체크 (비활성 상품인 경우)
        else if (!"ACTIVE".equals(product.getStatus())) {
            isAvailable = false;
            message = "현재 판매 중인 상품이 아닙니다.";
        }

        String currentProductXml = customer != null ?
                "<currentProduct>" +
                        "<productCode>" + customer.getProductCode() + "</productCode>" +
                        "<productName>" + customer.getProductName() + "</productName>" +
                        "<fee>" + customer.getFee() + "</fee>" +
                        "</currentProduct>" : "<currentProduct/>";

        String targetProductXml = product != null ?
                "<targetProduct>" +
                        "<productCode>" + product.getProductCode() + "</productCode>" +
                        "<productName>" + product.getProductName() + "</productName>" +
                        "<fee>" + product.getFee() + "</fee>" +
                        "</targetProduct>" : "<targetProduct/>";

        return xmlUtility.wrapInSoapEnvelope(
                "<productCheckResponse>" +
                        "<available>" + isAvailable + "</available>" +
                        "<message>" + message + "</message>" +
                        currentProductXml +
                        targetProductXml +
                        "</productCheckResponse>"
        );
    }

    /**
     * 상품 변경 요청 처리 XML 형식
     * @param phoneNumber 휴대폰 번호
     * @param productCode 상품 코드
     * @param changeReason 변경 사유
     * @return 상품 변경 결과 XML
     */
    public String changeProductXml(String phoneNumber, String productCode, String changeReason) {
        Map<String, com.ktds.mvne.kos.mock.service.MockDataService.CustomerInfo> customerDb = mockDataService.getCustomerDb();
        Map<String, com.ktds.mvne.kos.mock.service.MockDataService.ProductInfo> productDb = mockDataService.getProductDb();

        com.ktds.mvne.kos.mock.service.MockDataService.CustomerInfo customer = customerDb.get(phoneNumber);
        com.ktds.mvne.kos.mock.service.MockDataService.ProductInfo newProduct = productDb.get(productCode);
        System.out.println("====Mock===="+phoneNumber+productCode+changeReason);
        // 체크 로직
        if (customer == null || newProduct == null) {
            String errorMessage = customer == null ?
                    "해당 휴대폰 번호로 등록된 고객이 없습니다." : "해당 상품 코드에 대한 정보가 없습니다.";

            return xmlUtility.wrapInSoapEnvelope(
                    "<productChangeResponse>" +
                            "<success>false</success>" +
                            "<message>" + errorMessage + "</message>" +
                            "</productChangeResponse>"
            );
        }

        // 이미 같은 상품을 사용 중인지 체크
        if (customer.getProductCode().equals(productCode)) {
            return xmlUtility.wrapInSoapEnvelope(
                    "<productChangeResponse>" +
                            "<success>false</success>" +
                            "<message>이미 해당 상품을 사용 중입니다.</message>" +
                            "</productChangeResponse>"
            );
        }

        // 고객 상태 체크 (정지 상태인 경우)
        if ("SUSPENDED".equals(customer.getStatus())) {
            return xmlUtility.wrapInSoapEnvelope(
                    "<productChangeResponse>" +
                            "<success>false</success>" +
                            "<message>정지 상태인 회선은 상품 변경이 불가합니다.</message>" +
                            "</productChangeResponse>"
            );
        }

        // 상품 상태 체크 (비활성 상품인 경우)
        if (!"ACTIVE".equals(newProduct.getStatus())) {
            return xmlUtility.wrapInSoapEnvelope(
                    "<productChangeResponse>" +
                            "<success>false</success>" +
                            "<message>현재 판매 중인 상품이 아닙니다.</message>" +
                            "</productChangeResponse>"
            );
        }

        // 현재 상품 정보 저장
        String oldProductCode = customer.getProductCode();
        String oldProductName = customer.getProductName();
        Integer oldProductFee = customer.getFee();
        com.ktds.mvne.kos.mock.service.MockDataService.ProductInfo oldProduct = productDb.get(oldProductCode);

        // 추가 요금 계산 (업그레이드 시에만 발생)
        int additionalFee = 0;
        if (newProduct.getFee() > oldProductFee) {
            additionalFee = (newProduct.getFee() - oldProductFee) / 2; // 예시: 차액의 절반을 추가 요금으로 설정
        }

        // 트랜잭션 ID 생성
        String transactionId = "TRX" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime changeDate = LocalDateTime.now();

        // 고객 정보 업데이트
        customer.setProductCode(newProduct.getProductCode());
        customer.setProductName(newProduct.getProductName());
        customer.setFee(newProduct.getFee());
        customerDb.put(phoneNumber, customer);

        // 로그 추가
        log.info("상품 변경 완료 - 번호: {}, 기존 상품: {}, 신규 상품: {}",
                phoneNumber, oldProductCode, newProduct.getProductCode());

        // 응답 생성
        return xmlUtility.wrapInSoapEnvelope(
                "<productChangeResponse>" +
                        "<success>true</success>" +
                        "<message>상품 변경이 완료되었습니다.</message>" +
                        "<transactionId>" + transactionId + "</transactionId>" +
                        "<changeDate>" + changeDate.toString() + "</changeDate>" +
                        "<previousProduct>" +
                        "<productCode>" + oldProductCode + "</productCode>" +
                        "<productName>" + oldProductName + "</productName>" +
                        "<fee>" + oldProductFee + "</fee>" +
                        "</previousProduct>" +
                        "<newProduct>" +
                        "<productCode>" + newProduct.getProductCode() + "</productCode>" +
                        "<productName>" + newProduct.getProductName() + "</productName>" +
                        "<fee>" + newProduct.getFee() + "</fee>" +
                        "</newProduct>" +
                        "<additionalFee>" + additionalFee + "</additionalFee>" +
                        "</productChangeResponse>"
        );
    }

    /**
     * XML 내용을 SOAP 엔벨로프로 감싸기
     * @param content SOAP 본문 내용
     * @return SOAP 엔벨로프로 감싼 XML
     */
    public String wrapInSoapEnvelope(String content) {
        return xmlUtility.wrapInSoapEnvelope(content);
    }
}