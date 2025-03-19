// File: mp-server\kos-mock\src\main\java\com\ktds\mvne\kos\mock\controller\BillingMockController.java
package com.ktds.mvne.kos.mock.controller;

import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequest;
import com.ktds.mvne.kos.mock.dto.NotificationResponse;
import com.ktds.mvne.kos.mock.service.MockBillingService;
import com.ktds.mvne.kos.mock.service.MockProductService;
import com.ktds.mvne.kos.mock.util.MockDataGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * KT 영업시스템의 청구/요금 서비스를 목업하는 컨트롤러
 */
@RestController
@RequestMapping("/mock")
@RequiredArgsConstructor
@Slf4j
public class BillingMockController {

    private final MockDataGenerator mockDataGenerator;
    private final MockBillingService mockBillingService;
    private final MockProductService mockProductService;


    /**
     * 고객 정보 조회 API
     */
    @GetMapping("/billings/customer-info")
    public ResponseEntity<String> getCustomerInfo(@RequestParam String phoneNumber) {
        log.info("목업: 고객 정보 조회 요청 - 휴대폰 번호: {}", phoneNumber);
        // mockProductService 사용
        String customerInfoXml = mockProductService.getCustomerInfoXml(phoneNumber);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(customerInfoXml);
    }

    /**
     * 요금정보 변경 이벤트 발송 API
     */
    @PostMapping("/billings/notification")
    public ResponseEntity<NotificationResponse> notifyBillingChange(@RequestBody BillingChangeNotificationRequest request) {
        log.info("목업: 요금정보 변경 이벤트 발송 - 휴대폰 번호: {}, 청구월: {}",
                request.getPhoneNumber(), request.getBillingMonth());

        NotificationResponse response = mockBillingService.handleBillingChangeNotification(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 당월 청구 데이터 존재 여부 확인 API
     */
    @GetMapping("/billings/billing-status")
    public ResponseEntity<String> checkBillingStatus(@RequestParam String phoneNumber) {
        log.info("목업: 당월 청구 데이터 존재 여부 확인 - 휴대폰 번호: {}", phoneNumber);
        String responseXml = mockDataGenerator.generateBillingStatusResponse(phoneNumber);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(responseXml);
    }

    /**
     * 요금정보 조회 API
     */
    @GetMapping("/billings/info")
    public ResponseEntity<String> getBillingInfo(
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String billingMonth) {
        log.info("목업: 요금정보 조회 요청 - 휴대폰 번호: {}, 청구월: {}", phoneNumber, billingMonth);

        // billingMonth가 없으면 현재 월을 사용
        if (billingMonth == null || billingMonth.isEmpty()) {
            billingMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        }

        String responseXml = mockDataGenerator.generateBillingInfoResponse(phoneNumber, billingMonth);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(responseXml);
    }
}