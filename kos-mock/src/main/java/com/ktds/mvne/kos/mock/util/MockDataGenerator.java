package com.ktds.mvne.kos.mock.util;

import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import com.ktds.mvne.kos.mock.service.MockDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 목업 데이터를 생성하는 유틸리티 클래스입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataGenerator {

    private final MockDataService mockDataService;

    /**
     * 청구 상태 응답 SOAP XML을 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @return SOAP XML 응답
     */
    public String generateBillingStatusResponse(String phoneNumber) {
        // 입력 파라미터 검증
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            phoneNumber = "01012345678"; // 기본값 설정
            log.warn("Empty phone number provided, using default: {}", phoneNumber);
        }

        // 현재 월은 청구 데이터가 생성된 것으로 가정
        YearMonth currentMonth = YearMonth.now();
        String yearMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

        String responseBody = "<BillingStatusResponse>" +
                "<phoneNumber>" + phoneNumber + "</phoneNumber>" +
                "<currentBillingMonth>" + yearMonth + "</currentBillingMonth>" +
                "<billingGenerated>true</billingGenerated>" +
                "</BillingStatusResponse>";

        log.debug("Generated BillingStatusResponse for phone: {}, month: {}", phoneNumber, yearMonth);
        return wrapInSoapEnvelope(responseBody);
    }

    /**
     * 요금 정보 응답 SOAP XML을 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return SOAP XML 응답
     */
    public String generateBillingInfoResponse(String phoneNumber, String billingMonth) {
        // 입력 파라미터 검증
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            phoneNumber = "01012345678"; // 기본값 설정
            log.warn("Empty phone number provided, using default: {}", phoneNumber);
        }

        if (billingMonth == null || billingMonth.trim().isEmpty()) {
            billingMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM")); // 현재 월 사용
            log.warn("Empty billing month provided, using current month: {}", billingMonth);
        }

        // 마지막 두 자리가 해당 월에 영향을 주도록 랜덤 데이터 생성
        int monthValue = 1;
        try {
            monthValue = Integer.parseInt(billingMonth.substring(4, 6));
        } catch (Exception e) {
            log.warn("Failed to parse month from {}, using default value", billingMonth);
        }

        // 공유 데이터 소스에서 고객 정보 조회
        MockDataService.CustomerInfo customer = mockDataService.getCustomerDb().get(phoneNumber);

        // 기본 요금과 부가 서비스 요금 생성 - 월에 따라 약간의 변동 적용
        int baseFee = customer != null ? customer.getFee() : 55000 + (monthValue * 100);
        int dataFee = 10000 + (monthValue * 50);
        int serviceFee1 = 5000;
        int serviceFee2 = 3000;
        int deviceFee = 25000;
        int discount1 = 5000;
        int discount2 = 3000;
        int totalFee = baseFee + dataFee + serviceFee1 + serviceFee2 + deviceFee - discount1 - discount2;

        String responseBody = "<BillingInfoResponse>" +
                "<phoneNumber>" + phoneNumber + "</phoneNumber>" +
                "<billingMonth>" + billingMonth + "</billingMonth>" +
                "<totalFee>" + totalFee + "</totalFee>" +
                "<details>" +
                "<itemCode>BASE_FEE</itemCode>" +
                "<itemName>기본 요금</itemName>" +
                "<amount>" + baseFee + "</amount>" +
                "</details>" +
                "<details>" +
                "<itemCode>DATA_FEE</itemCode>" +
                "<itemName>데이터 요금</itemName>" +
                "<amount>" + dataFee + "</amount>" +
                "</details>" +
                "<details>" +
                "<itemCode>SVC001</itemCode>" +
                "<itemName>부가서비스1</itemName>" +
                "<amount>" + serviceFee1 + "</amount>" +
                "</details>" +
                "<details>" +
                "<itemCode>SVC002</itemCode>" +
                "<itemName>부가서비스2</itemName>" +
                "<amount>" + serviceFee2 + "</amount>" +
                "</details>" +
                "<discounts>" +
                "<discountCode>DISC001</discountCode>" +
                "<discountName>장기고객 할인</discountName>" +
                "<amount>" + discount1 + "</amount>" +
                "</discounts>" +
                "<discounts>" +
                "<discountCode>DISC002</discountCode>" +
                "<discountName>데이터 할인</discountName>" +
                "<amount>" + discount2 + "</amount>" +
                "</discounts>" +
                "<deviceInstallment>" +
                "<deviceId>DEVICE123</deviceId>" +
                "<model>Galaxy S21</model>" +
                "<amount>" + deviceFee + "</amount>" +
                "<remainingMonths>18</remainingMonths>" +
                "</deviceInstallment>" +
                "</BillingInfoResponse>";

        log.debug("Generated BillingInfoResponse for phone: {}, month: {}, totalFee: {}",
                phoneNumber, billingMonth, totalFee);
        return wrapInSoapEnvelope(responseBody);
    }

    /**
     * 고객 정보 응답 SOAP XML을 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @return SOAP XML 응답
     */
    public String generateCustomerInfoResponse(String phoneNumber) {
        // 입력 파라미터 검증
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            phoneNumber = "01012345678"; // 기본값 설정
            log.warn("Empty phone number provided, using default: {}", phoneNumber);
        }

        // 공유 데이터 소스에서 고객 정보 조회
        MockDataService.CustomerInfo customer = mockDataService.getCustomerDb().get(phoneNumber);

        if (customer == null) {
            // 기존 fallback 로직 유지
            String lastDigit = phoneNumber.substring(phoneNumber.length() - 1);
            String productCode = "5GX_STANDARD"; // 기본값

            try {
                int digit = Integer.parseInt(lastDigit);

                switch (digit % 5) {
                    case 0:
                        productCode = "5GX_BASIC";
                        break;
                    case 1:
                        productCode = "5GX_STANDARD";
                        break;
                    case 2:
                        productCode = "5GX_PREMIUM";
                        break;
                    case 3:
                        productCode = "LTE_BASIC";
                        break;
                    case 4:
                        productCode = "LTE_STANDARD";
                        break;
                }
            } catch (Exception e) {
                log.warn("Failed to parse last digit from {}, using default product", phoneNumber);
            }

            MockDataService.ProductInfo product = mockDataService.getProductDb().get(productCode);
            if (product == null) {
                productCode = "5GX_STANDARD";
                product = new MockDataService.ProductInfo(productCode, productCode + " 상품", 50000, "ACTIVE");
            }

            String responseBody = "<CustomerInfoResponse>" +
                    "<phoneNumber>" + phoneNumber + "</phoneNumber>" +
                    "<status>사용중</status>" +
                    "<currentProduct>" +
                    "<productCode>" + product.getProductCode() + "</productCode>" +
                    "<productName>" + product.getProductName() + "</productName>" +
                    "<fee>" + product.getFee() + "</fee>" +
                    "</currentProduct>" +
                    "</CustomerInfoResponse>";

            log.debug("Generated CustomerInfoResponse for phone: {}, product: {}",
                    phoneNumber, product.getProductCode());
            return wrapInSoapEnvelope(responseBody);
        } else {
            // 공유 데이터 소스에서 가져온 정보로 응답 생성
            String responseBody = "<CustomerInfoResponse>" +
                    "<phoneNumber>" + customer.getPhoneNumber() + "</phoneNumber>" +
                    "<status>" + customer.getStatus() + "</status>" +
                    "<currentProduct>" +
                    "<productCode>" + customer.getProductCode() + "</productCode>" +
                    "<productName>" + customer.getProductName() + "</productName>" +
                    "<fee>" + customer.getFee() + "</fee>" +
                    "</currentProduct>" +
                    "</CustomerInfoResponse>";

            log.debug("Generated CustomerInfoResponse for phone: {}, product: {}",
                    phoneNumber, customer.getProductCode());
            return wrapInSoapEnvelope(responseBody);
        }
    }

    /**
     * 상품 변경 응답 SOAP XML을 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 변경하려는 상품 코드
     * @param changeReason 변경 사유
     * @return SOAP XML 응답
     */
    public String generateProductChangeResponse(String phoneNumber, String productCode, String changeReason) {
        // 입력 파라미터 검증
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            phoneNumber = "01012345678"; // 기본값 설정
            log.warn("Empty phone number provided, using default: {}", phoneNumber);
        }

        if (productCode == null || productCode.trim().isEmpty()) {
            productCode = "5GX_PREMIUM"; // 기본값 설정
            log.warn("Empty product code provided, using default: {}", productCode);
        }

        // 공유 데이터 소스에서 고객 정보 및 상품 정보 조회
        MockDataService.CustomerInfo customer = mockDataService.getCustomerDb().get(phoneNumber);

        // 현재 상품은 고객 정보에서 가져오거나 기본값 사용
        MockDataService.ProductInfo oldProduct = null;
        String oldProductCode = "5GX_STANDARD";
        String oldProductName = "5G 스탠다드";
        int oldProductFee = 55000;

        if (customer != null) {
            oldProductCode = customer.getProductCode();
            oldProductName = customer.getProductName();
            oldProductFee = customer.getFee();
            oldProduct = mockDataService.getProductDb().get(oldProductCode);
        }

        // 변경하려는 상품
        MockDataService.ProductInfo newProduct = mockDataService.getProductDb().get(productCode);
        if (newProduct == null) {
            newProduct = new MockDataService.ProductInfo(productCode, productCode + " 상품", 75000, "ACTIVE");
        }

        // 추가 요금 계산 (신규 상품 요금이 더 비싼 경우 차액의 50%를 부과)
        int additionalFee = 0;
        if (newProduct.getFee() > oldProductFee) {
            additionalFee = (newProduct.getFee() - oldProductFee) / 2;
        }

        // 트랜잭션 ID 생성
        String transactionId = "TRX" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 변경 일시
        String changeDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String responseBody = "<ProductChangeResponse>" +
                "<success>true</success>" +
                "<message>상품 변경이 완료되었습니다.</message>" +
                "<transactionId>" + transactionId + "</transactionId>" +
                "<changeDate>" + changeDate + "</changeDate>" +
                "<previousProduct>" +
                "<productCode>" + oldProductCode + "</productCode>" +
                "<productName>" + oldProductName + "</productName>" +
                "<fee>" + oldProductFee + "</fee>" +
                "</previousProduct>" +
                "<newProduct>" +
                "<productCode>" + productCode + "</productCode>" +
                "<productName>" + newProduct.getProductName() + "</productName>" +
                "<fee>" + newProduct.getFee() + "</fee>" +
                "</newProduct>" +
                "<additionalFee>" + additionalFee + "</additionalFee>" +
                "</ProductChangeResponse>";

        log.debug("Generated ProductChangeResponse - from: {}, to: {}, additionalFee: {}",
                oldProductCode, productCode, additionalFee);
        return wrapInSoapEnvelope(responseBody);
    }

    /**
     * SOAP 엔벨로프로 래핑합니다.
     *
     * @param content XML 내용
     * @return SOAP 엔벨로프가 포함된 XML
     */
    private String wrapInSoapEnvelope(String content) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<soap:Body>" + content + "</soap:Body></soap:Envelope>";
    }
}