package com.ktds.mvne.product.controller;

import com.ktds.mvne.common.dto.ApiResponse;
import com.ktds.mvne.product.dto.ProductChangeRequest;
import com.ktds.mvne.product.dto.ProductChangeResponse;
import com.ktds.mvne.product.service.ProductService;
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
        ProductChangeResponse response = productService.changeProduct(
                request.getPhoneNumber(), request.getProductCode(), request.getChangeReason());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
