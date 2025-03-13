package com.ktds.mvne.kos.mock.util;

import com.ktds.mvne.common.exception.ExternalSystemException;
import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequest;
import com.ktds.mvne.kos.mock.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

/**
 * 알림을 발송하는 유틸리티 클래스입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {

    private final WebClient webClient;

    @Value("${notification.billing-change.target-url}")
    private String billingChangeNotificationUrl;

    /**
     * 요금 정보 변경 알림을 요금조회 서비스로 발송합니다.
     *
     * @param notification 요금 정보 변경 알림 요청
     * @return 알림 발송 결과
     */
    public NotificationResponse sendBillingChangeNotification(BillingChangeNotificationRequest notification) {
        log.info("Sending billing change notification to {}", billingChangeNotificationUrl);
        log.debug("Notification details: {}", notification);
        
        try {
            return webClient.post()
                    .uri(billingChangeNotificationUrl)
                    .bodyValue(notification)
                    .retrieve()
                    .bodyToMono(NotificationResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Failed to send notification with status {}: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            return createErrorResponse("Failed to send notification: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage(), e);
            return createErrorResponse("Failed to send notification: " + e.getMessage());
        }
    }

    /**
     * 오류 응답을 생성합니다.
     *
     * @param message 오류 메시지
     * @return 오류 응답
     */
    private NotificationResponse createErrorResponse(String message) {
        return NotificationResponse.builder()
                .success(false)
                .notificationId(UUID.randomUUID().toString())
                .message(message)
                .build();
    }
}
