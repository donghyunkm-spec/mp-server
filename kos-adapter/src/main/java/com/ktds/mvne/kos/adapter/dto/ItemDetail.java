package com.ktds.mvne.kos.adapter.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDetail {
    private String itemCode;
    private String itemName;
    private Integer amount;

    // String 값을 처리할 수 있는 생성자 추가
    @JsonCreator
    public static ItemDetail fromString(String value) {
        ItemDetail itemDetail = new ItemDetail();
        // value가 "BASE_FEE"와 같은 코드값이라면 이를 적절히 처리
        itemDetail.setItemCode(value);
        itemDetail.setItemName(getItemNameFromCode(value));
        itemDetail.setAmount(0); // 기본값 설정 또는 코드에 따른 기본 금액 설정
        return itemDetail;
    }

    // 코드에 따른 이름 매핑 (실제 비즈니스 로직에 맞게 구현)
    private static String getItemNameFromCode(String code) {
        switch (code) {
            case "BASE_FEE":
                return "기본 요금";
            // 다른 코드에 대한 처리 추가
            default:
                return code;
        }
    }
}