package com.ktds.mvne.product.controller;

import com.ktds.mvne.common.dto.ApiResponse;
import com.ktds.mvne.product.dto.ProductCheckRequest;
import com.ktds.mvne.product.dto.ProductCheckResponse;
import com.ktds.mvne.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 상품 정보 조회 및 변경 가능 여부 확인 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "상품 정보 API", description = "상품 정보 조회 및 변경 가능 여부 확인 관련 API를 제공합니다.")
public class ProductInfoController {

    private final ProductService productService;

    /**
     * 상품 변경 가능 여부를 확인합니다.
     *
     * @param request 상품 변경 가능 여부 확인 요청
     * @return 상품 변경 가능 여부 및 정보
     */
    @GetMapping("/check")
    @Operation(summary = "상품 변경 가능 여부 확인", description = "현재 상품에서 다른 상품으로 변경 가능한지 확인합니다.")
    public ResponseEntity<ApiResponse<ProductCheckResponse>> checkProductChange(
            ProductCheckRequest request) {
        log.debug("checkProductChange request for phoneNumber: {}, productCode: {}", 
                request.getPhoneNumber(), request.getProductCode());
        ProductCheckResponse response = productService.checkProductChangeAvailability(
                request.getPhoneNumber(), request.getProductCode());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
