package com.ktds.mvne.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 변경 요청 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 변경 요청")
public class ProductChangeRequest {

    @Schema(description = "회선 번호", example = "01012345678")
    private String phoneNumber;
    
    @Schema(description = "변경하려는 상품 코드", example = "5GX_PREMIUM")
    private String productCode;
    
    @Schema(description = "변경 사유", example = "데이터 사용량 증가")
    private String changeReason;
}
