package com.ktds.mvne.kos.mock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 요금 정보 변경 알림 요청 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "요금 정보 변경 알림 요청")
public class BillingChangeNotificationRequest {

    @Schema(description = "회선 번호", example = "01012345678")
    private String phoneNumber;
    
    @Schema(description = "청구 년월 (YYYYMM 형식)", example = "202403")
    private String billingMonth;
    
    @Schema(description = "변경 타입 (NEW, UPDATE, DELETE)", example = "UPDATE")
    private String changeType;
    
    @Schema(description = "변경 상세 내역")
    private List<BillingChangeDetail> details;

    /**
     * 요금 변경 상세 내역 DTO입니다.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "요금 변경 상세 내역")
    public static class BillingChangeDetail {
        
        @Schema(description = "항목 코드", example = "BASE_FEE")
        private String itemCode;
        
        @Schema(description = "금액", example = "30000")
        private Integer amount;
        
        @Schema(description = "변경 사유", example = "요금제 변경")
        private String changeReason;
    }
}
