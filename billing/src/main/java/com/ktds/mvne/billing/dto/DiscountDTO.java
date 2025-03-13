package com.ktds.mvne.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 할인 정보 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "할인 정보")
public class DiscountDTO {

    @Schema(description = "할인 코드", example = "DISC001")
    private String discountCode;
    
    @Schema(description = "할인 이름", example = "장기고객 할인")
    private String discountName;
    
    @Schema(description = "할인 금액", example = "5000")
    private Integer amount;
}
