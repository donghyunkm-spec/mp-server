package com.ktds.mvne.kos.adapter.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
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
public class DiscountDetail {

    @Schema(description = "할인 코드", example = "DISC001")
    private String discountCode;
    
    @Schema(description = "할인 이름", example = "장기고객 할인")
    private String discountName;
    
    @Schema(description = "할인 금액", example = "5000")
    private Integer amount;


    /**
     * 문자열에서 할인 정보를 생성합니다.
     * 이 생성자는 XML에서 단순 문자열 값만 있을 때 사용됩니다.
     *
     * @param discountCode 할인 코드
     */
    @JsonCreator
    public DiscountDetail(String discountCode) {
        this.discountCode = discountCode;
        this.discountName = "Unknown Discount";
        this.amount = 0;
    }

}
