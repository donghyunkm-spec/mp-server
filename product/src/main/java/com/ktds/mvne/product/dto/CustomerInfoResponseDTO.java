package com.ktds.mvne.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 고객 정보 응답 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "고객 정보 응답")
public class CustomerInfoResponseDTO {

    @Schema(description = "회선 번호", example = "01012345678")
    private String phoneNumber;
    
    @Schema(description = "회선 상태 (사용중, 정지, 해지 등)", example = "사용중")
    private String status;
    
    @Schema(description = "현재 사용 중인 상품 정보")
    private ProductInfoDTO currentProduct;
}
