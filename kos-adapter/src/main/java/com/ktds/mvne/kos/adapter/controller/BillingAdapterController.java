package com.ktds.mvne.kos.adapter.controller;

import com.ktds.mvne.common.dto.ApiResponse;
import com.ktds.mvne.kos.adapter.dto.BillingInfoResponse;
import com.ktds.mvne.kos.adapter.dto.BillingStatusResponse;
import com.ktds.mvne.kos.adapter.service.BillingAdapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * KT 영업시스템의 요금 조회 관련 API를 제공하는 어댑터 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/kos/billings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "요금 조회 어댑터 API", description = "KT 영업시스템의 요금 조회 관련 API를 제공합니다.")
public class BillingAdapterController {

    private final BillingAdapterService billingAdapterService;

    /**
     * 당월 청구 데이터 존재 여부를 확인합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 청구 상태 정보
     */
    @GetMapping("/billing-status")
    @Operation(summary = "청구 상태 확인", description = "당월 청구 데이터 존재 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<BillingStatusResponse>> checkBillingStatus(
            @Parameter(description = "회선 번호", example = "01012345678")
            @RequestParam("phoneNumber") String phoneNumber) {
        log.debug("checkBillingStatus request for phoneNumber: {}", phoneNumber);
        BillingStatusResponse response = billingAdapterService.checkBillingStatus(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 요금 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 요금 정보
     */
    @GetMapping("/info")
    @Operation(summary = "요금 정보 조회", description = "지정된 월의 요금 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<BillingInfoResponse>> getBillingInfo(
            @Parameter(description = "회선 번호", example = "01012345678")
            @RequestParam("phoneNumber") String phoneNumber,
            @Parameter(description = "청구 년월 (YYYYMM 형식)", example = "202403")
            @RequestParam("billingMonth") String billingMonth) {
        log.debug("getBillingInfo request for phoneNumber: {}, billingMonth: {}", phoneNumber, billingMonth);
        BillingInfoResponse response = billingAdapterService.getBillingInfo(phoneNumber, billingMonth);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
