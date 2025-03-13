package com.ktds.mvne.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 청구 상태 정보 응답 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "청구 상태 정보 응답")
public class BillingStatusResponse {

    @Schema(description = "회선 번호", example = "01012345678")
    private String phoneNumber;
    
    @Schema(description = "현재 청구 년월 (YYYYMM 형식)", example = "202403")
    private String currentBillingMonth;
    
    @Schema(description = "청구 데이터 생성 여부", example = "true")
    private boolean billingGenerated;
}
