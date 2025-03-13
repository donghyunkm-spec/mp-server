package com.ktds.mvne.billing.controller;

import com.ktds.mvne.billing.dto.BillingInfoResponseDTO;
import com.ktds.mvne.billing.service.BillingService;
import com.ktds.mvne.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 요금 조회 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/billings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "요금 조회 API", description = "요금 조회 관련 API를 제공합니다.")
public class BillingController {

    private final BillingService billingService;

    /**
     * 현재 또는 전월 요금을 조회합니다.
     * 당월 청구 데이터가 생성된 경우 당월 요금을, 그렇지 않은 경우 전월 요금을 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 요금 정보
     */
    @GetMapping("/{phoneNumber}/current")
    @Operation(summary = "현재/전월 요금 조회", description = "당월 청구 데이터 존재 시 당월 요금을, 미 존재 시 전월 요금을 조회합니다.")
    public ResponseEntity<ApiResponse<BillingInfoResponseDTO>> getCurrentBilling(
            @Parameter(description = "회선 번호", example = "01012345678")
            @PathVariable("phoneNumber") String phoneNumber) {
        log.debug("getCurrentBilling request for phoneNumber: {}", phoneNumber);
        BillingInfoResponseDTO response = billingService.getCurrentBilling(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 특정 월의 요금을 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 요금 정보
     */
    @GetMapping("/{phoneNumber}/specific")
    @Operation(summary = "특정월 요금 조회", description = "특정 월의 요금을 조회합니다.")
    public ResponseEntity<ApiResponse<BillingInfoResponseDTO>> getSpecificBilling(
            @Parameter(description = "회선 번호", example = "01012345678")
            @PathVariable("phoneNumber") String phoneNumber,
            @Parameter(description = "청구 년월 (YYYYMM 형식)", example = "202403")
            @RequestParam("billingMonth") String billingMonth) {
        log.debug("getSpecificBilling request for phoneNumber: {}, billingMonth: {}", phoneNumber, billingMonth);
        BillingInfoResponseDTO response = billingService.getSpecificBilling(phoneNumber, billingMonth);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
