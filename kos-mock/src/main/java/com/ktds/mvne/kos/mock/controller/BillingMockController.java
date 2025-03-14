package com.ktds.mvne.kos.mock.controller;

import com.ktds.mvne.common.dto.ApiResponse;
import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequest;
import com.ktds.mvne.kos.mock.dto.NotificationResponse;
import com.ktds.mvne.kos.mock.util.MockDataGenerator;
import com.ktds.mvne.kos.mock.util.NotificationSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * KT 영업시스템의 요금 조회 관련 API를 목업으로 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/mock/billings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "요금 조회 목업 API", description = "KT 영업시스템의 요금 조회 관련 API를 목업으로 제공합니다.")
public class BillingMockController {

    private final MockDataGenerator mockDataGenerator;
    private final NotificationSender notificationSender;

    /**
     * 요금 정보 변경 알림을 발송합니다.
     *
     * @param request 요금 정보 변경 알림 요청
     * @return 알림 발송 결과
     */
    @PostMapping("/notification")
    @Operation(summary = "요금 정보 변경 알림 발송", description = "KT 영업시스템에서 요금 정보 변경 알림을 발송합니다.")
    public ResponseEntity<ApiResponse<NotificationResponse>> notifyBillingChange(
            @RequestBody BillingChangeNotificationRequest request) {
        log.info("Sending billing change notification for {}, {}, type: {}",
                request.getPhoneNumber(), request.getBillingMonth(), request.getChangeType());

        NotificationResponse response = notificationSender.sendBillingChangeNotification(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 당월 청구 데이터 존재 여부를 확인합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 청구 상태 정보
     */
    @GetMapping(value = "/billing-status", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "청구 상태 확인", description = "당월 청구 데이터 존재 여부를 확인합니다.")
    public ResponseEntity<String> checkBillingStatus(
            @Parameter(description = "회선 번호", example = "01012345678")
            @RequestParam("phoneNumber") String phoneNumber) {
        log.debug("Mock checkBillingStatus request for phoneNumber: {}", phoneNumber);
        String response = mockDataGenerator.generateBillingStatusResponse(phoneNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * 고객 정보를 조회합니다. (POST 방식)
     * POST 요청의 경우 요청 본문에서 파라미터를 추출합니다.
     *
     * @param requestBody 요청 본문
     * @return 고객 정보
     */
    @PostMapping(value = "/customer-info", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "고객 정보 조회", description = "고객 정보를 조회합니다.")
    public ResponseEntity<String> getCustomerInfoPost(
            @RequestBody(required = false) Map<String, String> requestBody) {

        // 요청 본문이 null이거나 phoneNumber가 없는 경우 기본값 사용
        String phoneNumber = (requestBody != null && requestBody.containsKey("phoneNumber"))
                ? requestBody.get("phoneNumber")
                : "01012345678";

        String billingMonth = (requestBody != null && requestBody.containsKey("billingMonth"))
                ? requestBody.get("billingMonth")
                : null;

        log.debug("Mock getCustomerInfo POST request for phoneNumber: {}, billingMonth: {}",
                phoneNumber, billingMonth);

        String response = mockDataGenerator.generateBillingInfoResponse(phoneNumber,
                billingMonth != null ? billingMonth : getCurrentMonth());

        return ResponseEntity.ok(response);
    }

    /**
     * 요금 정보를 조회합니다. (GET 방식)
     *
     * @param phoneNumber 회선 번호
     * @param billingMonth 청구 년월 (YYYYMM 형식)
     * @return 요금 정보
     */
    @GetMapping(value = "/info", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "요금 정보 조회", description = "지정된 월의 요금 정보를 조회합니다.")
    public ResponseEntity<String> getBillingInfo(
            @Parameter(description = "회선 번호", example = "01012345678")
            @RequestParam("phoneNumber") String phoneNumber,
            @Parameter(description = "청구 년월 (YYYYMM 형식)", example = "202403")
            @RequestParam("billingMonth") String billingMonth) {
        log.debug("Mock getBillingInfo request for phoneNumber: {}, billingMonth: {}",
                phoneNumber, billingMonth);

        String response = mockDataGenerator.generateBillingInfoResponse(phoneNumber, billingMonth);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 월을 "YYYYMM" 형식으로 반환합니다.
     *
     * @return 현재 월 (YYYYMM 형식)
     */
    private String getCurrentMonth() {
        return new java.text.SimpleDateFormat("yyyyMM").format(new java.util.Date());
    }
}