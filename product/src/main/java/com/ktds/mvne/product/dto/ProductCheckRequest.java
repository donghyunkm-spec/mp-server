package com.ktds.mvne.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 변경 가능 여부 확인 요청 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 변경 가능 여부 확인 요청")
public class ProductCheckRequest {

    @Schema(description = "회선 번호", example = "01012345678")
    private String phoneNumber;
    
    @Schema(description = "변경하려는 상품 코드", example = "5GX_PREMIUM")
    private String productCode;
}
