// kos-mock/src/main/java/com/ktds/mvne/kos/mock/service/CustomerService.java
package com.ktds.mvne.kos.mock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 고객 정보 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    // 인메모리 고객 데이터 (데모용)
    private final Map<String, CustomerInfo> customerDb = new HashMap<>();

    /**
     * 서비스 초기화 - 샘플 고객 데이터 생성
     */
    {
        // 샘플 고객 1
        customerDb.put("01012345678", new CustomerInfo(
                "01012345678",
                "CUST001",
                "ACTIVE",
                "5GX_STANDARD",
                "5G 스탠다드",
                55000
        ));

        // 샘플 고객 2
        customerDb.put("01098765432", new CustomerInfo(
                "01098765432",
                "CUST002",
                "ACTIVE",
                "5GX_PREMIUM",
                "5G 프리미엄",
                75000
        ));

        // 샘플 고객 3 (정지 상태)
        customerDb.put("01011112222", new CustomerInfo(
                "01011112222",
                "CUST003",
                "SUSPENDED",
                "LTE_STANDARD",
                "LTE 스탠다드",
                45000
        ));
    }

    /**
     * 고객 정보 조회
     * @param phoneNumber 휴대폰 번호
     * @return 고객 정보
     */
    public CustomerInfo getCustomerInfo(String phoneNumber) {
        return customerDb.get(phoneNumber);
    }

    /**
     * 고객 정보 XML 형식으로 조회
     * @param phoneNumber 휴대폰 번호
     * @return 고객 정보 XML
     */
    public String getCustomerInfoXml(String phoneNumber) {
        CustomerInfo customerInfo = getCustomerInfo(phoneNumber);

        if (customerInfo == null) {
            return wrapInSoapEnvelope(
                    "<customerInfoResponse>" +
                            "<e>해당 휴대폰 번호로 등록된 고객이 없습니다.</e>" +
                            "</customerInfoResponse>"
            );
        }

        return wrapInSoapEnvelope(
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
     * 고객 정보 업데이트
     * @param phoneNumber 휴대폰 번호
     * @param customerInfo 새 고객 정보
     * @return 업데이트 성공 여부
     */
    public boolean updateCustomerInfo(String phoneNumber, CustomerInfo customerInfo) {
        if (!customerDb.containsKey(phoneNumber)) {
            return false;
        }

        customerDb.put(phoneNumber, customerInfo);
        return true;
    }

    /**
     * 고객의 상품 정보 업데이트
     * @param phoneNumber 휴대폰 번호
     * @param productCode 상품 코드
     * @param productName 상품 이름
     * @param fee 요금
     * @return 업데이트 성공 여부
     */
    public boolean updateCustomerProduct(String phoneNumber, String productCode, String productName, Integer fee) {
        CustomerInfo customerInfo = customerDb.get(phoneNumber);
        if (customerInfo == null) {
            return false;
        }

        customerInfo.setProductCode(productCode);
        customerInfo.setProductName(productName);
        customerInfo.setFee(fee);

        return updateCustomerInfo(phoneNumber, customerInfo);
    }

    /**
     * XML 내용을 SOAP 엔벨로프로 감싸기
     * @param content SOAP 본문 내용
     * @return SOAP 엔벨로프로 감싼 XML
     */
    private String wrapInSoapEnvelope(String content) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soapenv:Header/>\n" +
                "  <soapenv:Body>\n" +
                "    " + content + "\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    /**
     * 고객 정보 클래스
     */
    public static class CustomerInfo {
        private String phoneNumber;
        private String custId;
        private String status; // ACTIVE, SUSPENDED 등
        private String productCode;
        private String productName;
        private Integer fee;

        public CustomerInfo(String phoneNumber, String custId, String status,
                            String productCode, String productName, Integer fee) {
            this.phoneNumber = phoneNumber;
            this.custId = custId;
            this.status = status;
            this.productCode = productCode;
            this.productName = productName;
            this.fee = fee;
        }

        // Getters and Setters
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getCustId() { return custId; }
        public void setCustId(String custId) { this.custId = custId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Integer getFee() { return fee; }
        public void setFee(Integer fee) { this.fee = fee; }
    }
}