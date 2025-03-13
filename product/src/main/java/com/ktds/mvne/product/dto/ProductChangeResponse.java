package com.ktds.mvne.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 변경 응답 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 변경 응답")
public class ProductChangeResponse {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;
    
    @Schema(description = "메시지", example = "상품 변경이 완료되었습니다.")
    private String message;
    
    @Schema(description = "트랜잭션 ID", example = "TRX12345678")
    private String transactionId;
    
    @Schema(description = "변경 일시", example = "2025-03-13T14:30:00")
    private String changeDate;
    
    @Schema(description = "이전 상품 정보")
    private ProductInfoDTO previousProduct;
    
    @Schema(description = "새 상품 정보")
    private ProductInfoDTO newProduct;
    
    @Schema(description = "추가 요금", example = "5000")
    private Integer additionalFee;
}
