package com.ktds.mvne.kos.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 요금 항목 상세 정보 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "요금 항목 상세 정보")
public class ItemDetail {

    @Schema(description = "항목 코드", example = "BASE_FEE")
    private String itemCode;
    
    @Schema(description = "항목 이름", example = "기본 요금")
    private String itemName;
    
    @Schema(description = "금액", example = "30000")
    private Integer amount;
}
