// NotificationSender.java
package com.ktds.mvne.kos.mock.util;

import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequestDTO;
import com.ktds.mvne.kos.mock.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * 요금정보 변경 알림을 전송하는 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {

    @Value("${billing-service.url}")
    private String billingServiceUrl;

    private final RestTemplate restTemplate;

    /**
     * 요금정보 변경 알림을 요금조회 서비스로 전송
     * @param request 요금정보 변경 알림 요청
     * @return 알림 처리 결과
     */
    public NotificationResponseDTO sendBillingChangeNotification(BillingChangeNotificationRequestDTO request) {
        try {
            String notificationEndpoint = billingServiceUrl + "/api/notifications/billing-change";

            log.debug("요금정보 변경 알림 전송 - URL: {}, 요청: {}", notificationEndpoint, request);

            ResponseEntity<NotificationResponseDTO> response =
                    restTemplate.postForEntity(notificationEndpoint, request, NotificationResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("요금정보 변경 알림 전송 성공: {}", response.getBody());
                return response.getBody();
            } else {
                log.warn("요금정보 변경 알림 전송 실패 - 응답 코드: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("요금정보 변경 알림 전송 중 오류 발생", e);
        }

        // 실패 시 기본 응답 생성
        return NotificationResponseDTO.builder()
                .success(false)
                .notificationId(UUID.randomUUID().toString())
                .message("요금정보 변경 알림 전송 실패")
                .build();
    }
}