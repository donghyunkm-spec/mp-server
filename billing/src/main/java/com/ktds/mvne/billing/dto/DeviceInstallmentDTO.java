package com.ktds.mvne.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 단말기 할부 정보 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "단말기 할부 정보")
public class DeviceInstallmentDTO {

    @Schema(description = "단말기 ID", example = "DEVICE123")
    private String deviceId;
    
    @Schema(description = "단말기 모델명", example = "Galaxy S21")
    private String model;
    
    @Schema(description = "월 할부금", example = "25000")
    private Integer amount;
    
    @Schema(description = "남은 할부 개월 수", example = "18")
    private Integer remainingMonths;
}
