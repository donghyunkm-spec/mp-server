package com.ktds.mvne.product.controller;

import com.ktds.mvne.common.dto.ApiResponse;
import com.ktds.mvne.product.dto.ProductChangeRequest;
import com.ktds.mvne.product.dto.ProductChangeResponse;
import com.ktds.mvne.product.service.ProductService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 상품 변경 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "상품 변경 API", description = "상품 변경 관련 API를 제공합니다.")
public class ProductChangeController {

    private final ProductService productService;
    private final Counter productChangeRequestCounter;
    private final Counter productChangeSuccessCounter;
    private final Counter productChangeErrorCounter;
    private final Timer productChangeOperationTimer;

    /**
     * 상품을 변경합니다.
     *
     * @param request 상품 변경 요청
     * @return 상품 변경 결과
     */
    @PostMapping("/change")
    @Operation(summary = "상품 변경", description = "현재 사용 중인 상품을 다른 상품으로 변경합니다.")
    public ResponseEntity<ApiResponse<ProductChangeResponse>> changeProduct(
            @RequestBody ProductChangeRequest request) {
        log.debug("changeProduct request for phoneNumber: {}, productCode: {}, changeReason: {}",
                request.getPhoneNumber(), request.getProductCode(), request.getChangeReason());
        productChangeRequestCounter.increment();

        try {
            Timer.Sample sample = Timer.start();
            ProductChangeResponse response = productService.changeProduct(
                    request.getPhoneNumber(), request.getProductCode(), request.getChangeReason());
            sample.stop(productChangeOperationTimer);

            productChangeSuccessCounter.increment();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error in changeProduct for phoneNumber: {}, productCode: {}: {}",
                    request.getPhoneNumber(), request.getProductCode(), e.getMessage(), e);
            productChangeErrorCounter.increment();
            return ResponseEntity.status(500).body(
                    ApiResponse.of(500, "요금 정보 조회 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }
}