// kosmock/src/main/java/com/ktds/mvne/kosmock/dto/ProductDetailDTO.java
package com.ktds.mvne.kos.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {
    private String productCode;
    private String productName;
    private Integer fee;
}