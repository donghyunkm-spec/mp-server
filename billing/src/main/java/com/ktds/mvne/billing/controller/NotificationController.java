package com.ktds.mvne.billing.controller;

import com.ktds.mvne.billing.dto.BillingChangeNotificationRequest;
import com.ktds.mvne.billing.service.SyncService;
import com.ktds.mvne.common.dto.ApiResponse;
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
 * 요금 정보 변경 알림을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "알림 API", description = "요금 정보 변경 알림 관련 API를 제공합니다.")
public class NotificationController {

    private final SyncService syncService;

    /**
     * 요금 정보 변경 알림을 처리합니다.
     *
     * @param request 요금 정보 변경 알림 요청
     * @return 처리 결과
     */
    @PostMapping("/billing-change")
    @Operation(summary = "요금 정보 변경 알림 처리", description = "KT 영업시스템으로부터의 요금 정보 변경 알림을 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> handleBillingChangeNotification(
            @RequestBody BillingChangeNotificationRequest request) {
        log.info("Received billing change notification for {}, {}, type: {}", 
                request.getPhoneNumber(), request.getBillingMonth(), request.getChangeType());
        
        syncService.processBillingChangeEvent(request);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
