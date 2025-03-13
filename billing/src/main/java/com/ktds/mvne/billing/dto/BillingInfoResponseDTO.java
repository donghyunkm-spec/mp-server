package com.ktds.mvne.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 요금 정보 응답 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "요금 정보 응답")
public class BillingInfoResponseDTO {

    @Schema(description = "회선 번호", example = "01012345678")
    private String phoneNumber;
    
    @Schema(description = "청구 년월 (YYYYMM 형식)", example = "202403")
    private String billingMonth;
    
    @Schema(description = "총 요금", example = "35000")
    private Integer totalFee;
    
    @Schema(description = "요금 상세 내역")
    private List<FeeDetailDTO> details;
    
    @Schema(description = "할인 내역")
    private List<DiscountDTO> discounts;
    
    @Schema(description = "단말기 할부 정보")
    private DeviceInstallmentDTO deviceInstallment;
}
