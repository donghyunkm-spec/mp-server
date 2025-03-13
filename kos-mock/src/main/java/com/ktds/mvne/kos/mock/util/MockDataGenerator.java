package com.ktds.mvne.kos.mock.util;

import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 목업 데이터를 생성하는 유틸리티 클래스입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataGenerator {

    // 상품 정보 맵
    private static final Map<String, ProductInfo> PRODUCT_MAP = new HashMap<>();
    
    static {
        PRODUCT_MAP.put("5GX_BASIC", new ProductInfo("5GX_BASIC", "5G 베이직", 45000));
        PRODUCT_MAP.put("5GX_STANDARD", new ProductInfo("5GX_STANDARD", "5G 스탠다드", 55000));
        PRODUCT_MAP.put("5GX_PREMIUM", new ProductInfo("5GX_PREMIUM", "5G 프리미엄", 75000));
        PRODUCT_MAP.put("5GX_ULTRA", new ProductInfo("5GX_ULTRA", "5G 울트라", 95000));
        PRODUCT_MAP.put("LTE_BASIC", new ProductInfo("LTE_BASIC", "LTE 베이직", 35000));
        PRODUCT_MAP.put("LTE_STANDARD", new ProductInfo("LTE_STANDARD", "LTE 스탠다드", 45000));
        PRODUCT_MAP.put("LTE_PREMIUM", new ProductInfo("LTE_PREMIUM", "LTE 프리미엄", 65000));
    }

    /**
     * 청구 상태 응답 SOAP XML을 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @return SOAP XML 응답
     */
    public String generateBillingStatusResponse(String phoneNumber) {
        // 현재 월은 청구 데이터가 생성된 것으로 가정
        YearMonth currentMonth = YearMonth.now();
        String yearMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        
        String responseBody = "<BillingStatusResponse>" +
                "<phoneNumber>" + phoneNumber + "</phoneNumber>" +
                "<currentBillingMonth>" + yearMonth + "</currentBillingMonth>" +
                "<billingGenerated>true</billingGenerated>" +
                "</BillingStatusResponse>";
        
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
        // 기본 요금과 부가 서비스 요금 생성
        int baseFee = 55000;
        int dataFee = 10000;
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
        
        return wrapInSoapEnvelope(responseBody);
    }

    /**
     * 고객 정보 응답 SOAP XML을 생성합니다.
     *
     * @param phoneNumber 회선 번호
     * @return SOAP XML 응답
     */
    public String generateCustomerInfoResponse(String phoneNumber) {
        // 고객 정보 생성
        String productCode = "5GX_STANDARD";
        String productName = "5G 스탠다드";
        int fee = 55000;
        
        String responseBody = "<CustomerInfoResponse>" +
                "<phoneNumber>" + phoneNumber + "</phoneNumber>" +
                "<status>사용중</status>" +
                "<currentProduct>" +
                "<productCode>" + productCode + "</productCode>" +
                "<productName>" + productName + "</productName>" +
                "<fee>" + fee + "</fee>" +
                "</currentProduct>" +
                "</CustomerInfoResponse>";
        
        return wrapInSoapEnvelope(responseBody);
    }

    /**
     * 상품 정보 응답 SOAP XML을 생성합니다.
     *
     * @param productCode 상품 코드
     * @return SOAP XML 응답
     */
    public String generateProductInfoResponse(String productCode) {
        ProductInfo product = getProductInfo(productCode);
        
        String responseBody = "<ProductDetail>" +
                "<productCode>" + product.code() + "</productCode>" +
                "<productName>" + product.name() + "</productName>" +
                "<fee>" + product.fee() + "</fee>" +
                "</ProductDetail>";
        
        return wrapInSoapEnvelope(responseBody);
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
        // 현재 상품은 5GX_STANDARD로 가정
        ProductInfo oldProduct = getProductInfo("5GX_STANDARD");
        
        // 변경하려는 상품
        ProductInfo newProduct = getProductInfo(productCode);
        
        // 추가 요금 계산 (신규 상품 요금이 더 비싼 경우 차액의 50%를 부과)
        int additionalFee = 0;
        if (newProduct.fee() > oldProduct.fee()) {
            additionalFee = (newProduct.fee() - oldProduct.fee()) / 2;
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
                "<productCode>" + oldProduct.code() + "</productCode>" +
                "<productName>" + oldProduct.name() + "</productName>" +
                "<fee>" + oldProduct.fee() + "</fee>" +
                "</previousProduct>" +
                "<newProduct>" +
                "<productCode>" + newProduct.code() + "</productCode>" +
                "<productName>" + newProduct.name() + "</productName>" +
                "<fee>" + newProduct.fee() + "</fee>" +
                "</newProduct>" +
                "<additionalFee>" + additionalFee + "</additionalFee>" +
                "</ProductChangeResponse>";
        
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

    /**
     * 상품 정보를 가져옵니다.
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     * @throws BizException 상품 정보가 없는 경우
     */
    private ProductInfo getProductInfo(String productCode) {
        ProductInfo product = PRODUCT_MAP.get(productCode);
        if (product == null) {
            log.warn("Product not found for code: {}", productCode);
            throw new BizException(ErrorCode.BAD_REQUEST, "Invalid product code: " + productCode);
        }
        return product;
    }

    /**
     * 상품 정보 레코드입니다.
     */
    private record ProductInfo(String code, String name, int fee) {
    }
}
