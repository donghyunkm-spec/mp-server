package com.ktds.mvne.kos.mock.controller;

import com.ktds.mvne.kos.mock.util.MockDataGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * KT 영업시스템의 상품 관련 API를 목업으로 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/mock")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "상품 목업 API", description = "KT 영업시스템의 상품 관련 API를 목업으로 제공합니다.")
public class ProductMockController {

    private final MockDataGenerator mockDataGenerator;

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @GetMapping(value = "/customers/{phoneNumber}", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "고객 정보 조회", description = "고객의 회선 상태와 현재 사용 중인 상품 정보를 조회합니다.")
    public ResponseEntity<String> getCustomerInfo(
            @Parameter(description = "회선 번호", example = "01012345678")
            @PathVariable("phoneNumber") String phoneNumber) {
        log.debug("Mock getCustomerInfo request for phoneNumber: {}", phoneNumber);
        String response = mockDataGenerator.generateCustomerInfoResponse(phoneNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 정보를 조회합니다.
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    @GetMapping(value = "/products/{productCode}", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "상품 정보 조회", description = "상품 정보를 조회합니다.")
    public ResponseEntity<String> getProductInfo(
            @Parameter(description = "상품 코드", example = "5GX_STANDARD")
            @PathVariable("productCode") String productCode) {
        log.debug("Mock getProductInfo request for productCode: {}", productCode);
        String response = mockDataGenerator.generateProductInfoResponse(productCode);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품을 변경합니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 변경하려는 상품 코드
     * @param changeReason 변경 사유
     * @return 상품 변경 결과
     */
    @PostMapping(value = "/products/change", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "상품 변경", description = "현재 사용 중인 상품을 다른 상품으로 변경합니다.")
    public ResponseEntity<String> changeProduct(
            @Parameter(description = "회선 번호", example = "01012345678")
            @RequestParam("phoneNumber") String phoneNumber,
            @Parameter(description = "변경하려는 상품 코드", example = "5GX_PREMIUM")
            @RequestParam("productCode") String productCode,
            @Parameter(description = "변경 사유", example = "데이터 사용량 증가")
            @RequestParam("changeReason") String changeReason) {
        log.debug("Mock changeProduct request for phoneNumber: {}, productCode: {}, changeReason: {}",
                phoneNumber, productCode, changeReason);
        String response = mockDataGenerator.generateProductChangeResponse(phoneNumber, productCode, changeReason);
        return ResponseEntity.ok(response);
    }
}
