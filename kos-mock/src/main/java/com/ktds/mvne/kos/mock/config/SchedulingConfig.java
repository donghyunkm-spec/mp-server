// File: mp-server\kos-mock\src\main\java\com\ktds\mvne\kos\mock\config\SchedulingConfig.java
package com.ktds.mvne.kos.mock.config;

import com.ktds.mvne.kos.mock.dto.BillingChangeDetail;
import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequest;
import com.ktds.mvne.kos.mock.dto.BillingChangeDetailDTO;
import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequestDTO;
import com.ktds.mvne.kos.mock.service.MockBillingService;
import com.ktds.mvne.kos.mock.util.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 스케줄링 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "mock.notification.billing-change.enabled", havingValue = "true")
public class SchedulingConfig {

    private final NotificationSender notificationSender;
    private final Random random = new Random();

    // 전화번호 목록
    private final List<String> phoneNumbers = List.of(
            "01012345678",
            "01098765432",
            "01011112222"
    );

    // 청구 항목 코드 목록
    private final List<String> itemCodes = List.of(
            "MONTHLY_FEE",
            "ADD_SVC",
            "ROAMING",
            "DATA_USAGE"
    );

    // 변경 사유 목록
    private final List<String> changeReasons = List.of(
            "정기 업데이트",
            "사용량 증가",
            "요금제 변경",
            "할인 적용"
    );

    /**
     * 요금정보 변경 이벤트 주기적 발생 (매 10분마다 실행)
     */
    @Scheduled(cron = "${mock.notification.billing-change.cron:0 */10 * * * *}")
    public void sendRandomBillingChangeNotification() {
        // 현재 월 YYYYMM 형식
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 랜덤 전화번호 선택
        String phoneNumber = phoneNumbers.get(random.nextInt(phoneNumbers.size()));

        // 2-4개의 랜덤 청구 항목 변경 상세 정보 생성
        int detailCount = random.nextInt(3) + 2;
        List<BillingChangeNotificationRequest.BillingChangeDetail> details = new ArrayList<>();

        for (int i = 0; i < detailCount; i++) {
            String itemCode = itemCodes.get(random.nextInt(itemCodes.size()));
            int amount = (random.nextInt(10) + 1) * 1000; // 1,000 ~ 10,000
            String changeReason = changeReasons.get(random.nextInt(changeReasons.size()));

            details.add(BillingChangeNotificationRequest.BillingChangeDetail.builder()
                    .itemCode(itemCode)
                    .amount(amount)
                    .changeReason(changeReason)
                    .build());
        }

        // 요금정보 변경 알림 요청 생성
        BillingChangeNotificationRequest request = BillingChangeNotificationRequest.builder()
                .phoneNumber(phoneNumber)
                .billingMonth(currentMonth)
                .changeType("UPDATE")
                .details(details)
                .build();

        log.info("스케줄링: 요금정보 변경 이벤트 발생 - 휴대폰 번호: {}, 청구월: {}", phoneNumber, currentMonth);

        // DTO로 변환하여 알림 전송
        BillingChangeNotificationRequestDTO requestDTO = convertToDTO(request);
        notificationSender.sendBillingChangeNotification(requestDTO);
    }

    /**
     * BillingChangeNotificationRequest를 BillingChangeNotificationRequestDTO로 변환
     */
    private BillingChangeNotificationRequestDTO convertToDTO(BillingChangeNotificationRequest request) {
        // 상세 항목 변환
        List<BillingChangeDetailDTO> detailDTOs = request.getDetails().stream()
                .map(detail -> new BillingChangeDetailDTO(
                        detail.getItemCode(),
                        detail.getAmount(),
                        detail.getChangeReason()
                ))
                .toList();

        // DTO 생성 및 반환
        return BillingChangeNotificationRequestDTO.builder()
                .phoneNumber(request.getPhoneNumber())
                .billingMonth(request.getBillingMonth())
                .changeType(request.getChangeType())
                .details(detailDTOs)
                .build();
    }
}