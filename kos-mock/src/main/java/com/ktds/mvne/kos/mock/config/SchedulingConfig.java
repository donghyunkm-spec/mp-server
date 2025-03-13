package com.ktds.mvne.kos.mock.config;

import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequest;
import com.ktds.mvne.kos.mock.dto.BillingChangeNotificationRequest.BillingChangeDetail;
import com.ktds.mvne.kos.mock.util.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * 스케줄링 설정 클래스입니다.
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final NotificationSender notificationSender;
    private final Random random = new Random();

    private static final String[] PHONE_NUMBERS = {
            "01012345678", "01023456789", "01034567890", "01045678901", "01056789012"
    };

    private static final String[] ITEM_CODES = {
            "BASE_FEE", "DATA_FEE", "SVC001", "SVC002"
    };

    private static final String[] CHANGE_REASONS = {
            "요금제 변경", "프로모션 적용", "할인 조정", "부가서비스 변경"
    };

    @Value("${notification.schedule.enabled:false}")
    private boolean notificationScheduleEnabled;

    /**
     * 주기적으로 요금 정보 변경 알림을 발송합니다.
     */
    @Scheduled(cron = "${notification.schedule.cron:0 0/5 * * * ?}")
    public void sendPeriodicBillingChangeNotification() {
        if (!notificationScheduleEnabled) {
            return;
        }

        log.info("Sending scheduled billing change notification");

        // 현재 월을 기준으로 청구 년월 생성
        YearMonth currentMonth = YearMonth.now();
        String yearMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 무작위 회선 번호 선택
        String phoneNumber = PHONE_NUMBERS[random.nextInt(PHONE_NUMBERS.length)];

        // 무작위 항목 코드 선택
        String itemCode = ITEM_CODES[random.nextInt(ITEM_CODES.length)];

        // 무작위 변경 사유 선택
        String changeReason = CHANGE_REASONS[random.nextInt(CHANGE_REASONS.length)];

        // 무작위 금액 생성 (1000 ~ 10000 사이)
        int amount = (random.nextInt(10) + 1) * 1000;

        // 알림 요청 생성
        BillingChangeNotificationRequest request = BillingChangeNotificationRequest.builder()
                .phoneNumber(phoneNumber)
                .billingMonth(yearMonth)
                .changeType("UPDATE")
                .details(List.of(
                        BillingChangeDetail.builder()
                                .itemCode(itemCode)
                                .amount(amount)
                                .changeReason(changeReason)
                                .build()
                ))
                .build();

        // 알림 발송
        notificationSender.sendBillingChangeNotification(request);
    }
}
