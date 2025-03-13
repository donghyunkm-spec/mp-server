package com.ktds.mvne.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 정보 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 정보")
public class ProductInfoDTO {

    @Schema(description = "상품 코드", example = "5GX_STANDARD")
    private String productCode;
    
    @Schema(description = "상품명", example = "5G 스탠다드")
    private String productName;
    
    @Schema(description = "요금", example = "55000")
    private Integer fee;
}
