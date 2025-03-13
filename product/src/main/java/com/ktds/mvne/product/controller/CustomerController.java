package com.ktds.mvne.product.controller;

import com.ktds.mvne.common.dto.ApiResponse;
import com.ktds.mvne.product.dto.CustomerInfoResponseDTO;
import com.ktds.mvne.product.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 고객 정보 조회 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "고객 정보 조회 API", description = "고객 정보 조회 관련 API를 제공합니다.")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @GetMapping("/{phoneNumber}")
    @Operation(summary = "고객 정보 조회", description = "고객의 회선 상태와 현재 사용 중인 상품 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<CustomerInfoResponseDTO>> getCustomerInfo(
            @Parameter(description = "회선 번호", example = "01012345678")
            @PathVariable("phoneNumber") String phoneNumber) {
        log.debug("getCustomerInfo request for phoneNumber: {}", phoneNumber);
        CustomerInfoResponseDTO response = customerService.getCustomerInfo(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
