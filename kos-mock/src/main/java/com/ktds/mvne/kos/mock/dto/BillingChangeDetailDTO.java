// kosmock/src/main/java/com/ktds/mvne/kosmock/dto/BillingChangeDetailDTO.java
package com.ktds.mvne.kos.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingChangeDetailDTO {
    private String itemCode;
    private Integer amount;
    private String changeReason;
}