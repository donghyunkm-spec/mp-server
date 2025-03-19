// File: mp-server\kos-mock\src\main\java\com\ktds\mvne\kos\mock\service\MockBillingService.java
package com.ktds.mvne.kos.mock.service;

import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequest;
import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequestDTO;
import com.ktds.mvne.kos.mock.dto.NotificationResponse;
import com.ktds.mvne.kos.mock.util.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 요금 정보 관련 목업 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MockBillingService {

    private final NotificationSender notificationSender;

    /**
     * 요금정보 변경 알림 처리
     * @param request 요금정보 변경 알림 요청
     * @return 알림 처리 결과
     */
    public NotificationResponse handleBillingChangeNotification(BillingChangeNotificationRequest request) {
        log.info("요금정보 변경 알림 처리 - 휴대폰 번호: {}, 청구월: {}",
                request.getPhoneNumber(), request.getBillingMonth());

        try {
            // DTO로 변환하여 알림 전송
            BillingChangeNotificationRequestDTO requestDTO = convertToDTO(request);
            notificationSender.sendBillingChangeNotification(requestDTO);

            // 성공 응답 생성
            return NotificationResponse.builder()
                    .success(true)
                    .notificationId("NOTIF-" + UUID.randomUUID().toString().substring(0, 8))
                    .message("요금정보 변경 알림이 성공적으로 처리되었습니다.")
                    .build();
        } catch (Exception e) {
            log.error("요금정보 변경 알림 처리 중 오류 발생", e);

            // 실패 응답 생성
            return NotificationResponse.builder()
                    .success(false)
                    .notificationId("NOTIF-" + UUID.randomUUID().toString().substring(0, 8))
                    .message("요금정보 변경 알림 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * BillingChangeNotificationRequest를 BillingChangeNotificationRequestDTO로 변환
     */
    private BillingChangeNotificationRequestDTO convertToDTO(BillingChangeNotificationRequest request) {
        return BillingChangeNotificationRequestDTO.builder()
                .phoneNumber(request.getPhoneNumber())
                .billingMonth(request.getBillingMonth())
                .changeType(request.getChangeType())
                .details(request.getDetails().stream()
                        .map(detail -> new com.ktds.mvne.kos.mock.dto.BillingChangeDetailDTO(
                                detail.getItemCode(),
                                detail.getAmount(),
                                detail.getChangeReason()
                        ))
                        .collect(Collectors.toList()))
                .build();
    }
}