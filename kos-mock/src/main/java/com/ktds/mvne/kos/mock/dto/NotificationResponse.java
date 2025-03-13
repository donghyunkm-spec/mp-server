package com.ktds.mvne.kos.mock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 알림 발송 응답 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알림 발송 응답")
public class NotificationResponse {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;
    
    @Schema(description = "알림 ID", example = "NOTIF12345678")
    private String notificationId;
    
    @Schema(description = "메시지", example = "알림이 성공적으로 발송되었습니다.")
    private String message;
}
