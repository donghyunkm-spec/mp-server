// ProductMockController.java
package com.ktds.mvne.kos.mock.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ktds.mvne.kos.mock.dto.ProductChangeRequest;
import com.ktds.mvne.kos.mock.service.MockProductService;
import com.ktds.mvne.kos.mock.util.MockDataGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



/**
 * KT 영업시스템의 상품변경 서비스를 목업하는 컨트롤러
 */
@RestController
@RequestMapping("/mock")
@RequiredArgsConstructor
@Slf4j
public class ProductMockController {

    private final MockProductService mockProductService;
    private final MockDataGenerator mockDataGenerator;

    /**
     * 고객 정보 조회 API
     */
    @GetMapping("/products/customer-info")
    public ResponseEntity<String> getCustomerInfo(@RequestParam String phoneNumber) {
        log.info("목업: 고객 정보 조회 요청 - 휴대폰 번호: {}", phoneNumber);
        String customerInfoXml = mockDataGenerator.generateCustomerInfoResponse(phoneNumber);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(customerInfoXml);
    }

    /**
     * 상품 정보 조회 API
     */
    @GetMapping("/products/product-info")
    public ResponseEntity<String> getProductInfo(@RequestParam String productCode) {
        log.info("목업: 상품 정보 조회 요청 - 상품 코드: {}", productCode);
        String productInfoXml = mockProductService.getProductInfoXml(productCode);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(productInfoXml);
    }

    /**
     * 상품 변경 가능 여부 체크 API
     */
    @GetMapping("/products/check")
    public ResponseEntity<String> checkProductChange(
            @RequestParam String phoneNumber,
            @RequestParam String productCode) {
        log.info("목업: 상품 변경 가능 여부 체크 - 휴대폰 번호: {}, 상품 코드: {}", phoneNumber, productCode);
        String checkResponseXml = mockProductService.checkProductChangeXml(phoneNumber, productCode);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(checkResponseXml);
    }


    /**
     * 상품 변경 요청 처리 API - 빌링 컨트롤러에서 호출하는 경로
     */
    /**
     * 상품 변경 요청 처리 API - 빌링 컨트롤러에서 호출하는 경로
     */
    @PostMapping("/billings/change")
    public ResponseEntity<String> changeBillingProduct(
            HttpServletRequest request) {

        // 요청 본문 읽어오기
        String requestBody = "";
        try {
            BufferedReader reader = request.getReader();
            if (reader != null) {
                requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (Exception e) {
            log.warn("요청 본문 읽기 실패: {}", e.getMessage());
        }

        log.info("수신한 요청 본문: {}", requestBody);

        // 기본 응답 생성
        String phoneNumber = request.getParameter("phoneNumber");
        String productCode = request.getParameter("productCode");
        String changeReason = request.getParameter("changeReason");

        // XML 요청 본문에서 값 추출 시도
        if (requestBody != null && !requestBody.isEmpty()) {
            // 간단한 파싱으로 값 추출 (전체 XML 파싱은 복잡할 수 있음)
            if (phoneNumber == null && requestBody.contains("<phoneNumber>")) {
                phoneNumber = extractValue(requestBody, "phoneNumber");
            }
            if (productCode == null && requestBody.contains("<productCode>")) {
                productCode = extractValue(requestBody, "productCode");
            }
            if (changeReason == null && requestBody.contains("<changeReason>")) {
                changeReason = extractValue(requestBody, "changeReason");
            }
        }

        log.info("목업: 빌링 경로 상품 변경 요청 - 휴대폰 번호: {}, 상품 코드: {}, 변경 사유: {}",
                phoneNumber, productCode, changeReason);

        // 기본값 처리
        if (phoneNumber == null) phoneNumber = "01012345678";
        if (productCode == null) productCode = "5GX_PREMIUM";
        if (changeReason == null) changeReason = "API 요청";

        // 응답 생성
        String responseXml = mockProductService.changeProductXml(phoneNumber, productCode, changeReason);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(responseXml);
    }

    /**
     * XML 문자열에서 특정 태그의 값을 추출
     */
    private String extractValue(String xml, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }



    /**
     * 상품 정보 조회 API - 빌링 컨트롤러에서 호출하는 경로
     */
    @GetMapping("/billings/product-info")
    public ResponseEntity<String> getBillingProductInfo(@RequestParam String productCode) {
        log.info("목업: 빌링 경로 상품 정보 조회 요청 - 상품 코드: {}", productCode);
        String productInfoXml = mockProductService.getProductInfoXml(productCode);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(productInfoXml);
    }
}