// File: mp-server\kos-adapter\src\main\java\com\ktds\mvne\kos\adapter\controller\ProductAdapterController.java
package com.ktds.mvne.kos.adapter.controller;

import com.ktds.mvne.kos.adapter.dto.CustomerInfoResponse;
import com.ktds.mvne.kos.adapter.dto.ProductChangeResponse;
import com.ktds.mvne.kos.adapter.dto.ProductDetail;
import com.ktds.mvne.kos.adapter.service.ProductAdapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * KT 영업시스템의 상품 관련 API를 제공하는 어댑터 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/kos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "상품 어댑터 API", description = "KT 영업시스템의 상품 관련 API를 제공합니다.")
public class ProductAdapterController {

    private final ProductAdapterService productAdapterService;

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @GetMapping("/customers/{phoneNumber}")
    @Operation(summary = "고객 정보 조회", description = "고객의 회선 상태와 현재 사용 중인 상품 정보를 조회합니다.")
    public ResponseEntity<CustomerInfoResponse> getCustomerInfo(
            @Parameter(description = "회선 번호", example = "01012345678")
            @PathVariable("phoneNumber") String phoneNumber) {
        log.debug("getCustomerInfo request for phoneNumber: {}", phoneNumber);
        CustomerInfoResponse response = productAdapterService.getCustomerInfo(phoneNumber);

        // response 객체 로깅 - 문제 진단용
        log.debug("CustomerInfo response before returning: {}", response);

        // phoneNumber가 null인 경우 요청 값으로 설정
        if (response.getPhoneNumber() == null || response.getPhoneNumber().isEmpty()) {
            log.warn("응답의 phoneNumber가 null입니다. 요청값으로 설정합니다: {}", phoneNumber);
            response.setPhoneNumber(phoneNumber);
        }

        // ApiResponse.success()를 제거하고 response 객체를 직접 반환
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 정보를 조회합니다.
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    @GetMapping("/products/{productCode}")
    @Operation(summary = "상품 정보 조회", description = "상품 정보를 조회합니다.")
    public ResponseEntity<ProductDetail> getProductInfo(
            @Parameter(description = "상품 코드", example = "5GX_STANDARD")
            @PathVariable("productCode") String productCode) {
        log.debug("getProductInfo request for productCode: {}", productCode);
        ProductDetail response = productAdapterService.getProductInfo(productCode);

        // productCode가 null인 경우 요청 값으로 설정
        if (response.getProductCode() == null || response.getProductCode().isEmpty()) {
            log.warn("응답의 productCode가 null입니다. 요청값으로 설정합니다: {}", productCode);
            response.setProductCode(productCode);
        }

        // ApiResponse.success()를 제거하고 response 객체를 직접 반환
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
    @PostMapping("/products/change")
    @Operation(summary = "상품 변경", description = "현재 사용 중인 상품을 다른 상품으로 변경합니다.")
    public ResponseEntity<ProductChangeResponse> changeProduct(
            @Parameter(description = "회선 번호", example = "01012345678")
            @RequestParam("phoneNumber") String phoneNumber,
            @Parameter(description = "변경하려는 상품 코드", example = "5GX_PREMIUM")
            @RequestParam("productCode") String productCode,
            @Parameter(description = "변경 사유", example = "데이터 사용량 증가")
            @RequestParam("changeReason") String changeReason) {
        log.debug("changeProduct request for phoneNumber: {}, productCode: {}, changeReason: {}",
                phoneNumber, productCode, changeReason);
        ProductChangeResponse response = productAdapterService.changeProduct(phoneNumber, productCode, changeReason);

        // ApiResponse.success()를 제거하고 response 객체를 직접 반환
        return ResponseEntity.ok(response);
    }
}