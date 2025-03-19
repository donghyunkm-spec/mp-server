// kosmock/src/main/java/com/ktds/mvne/kosmock/dto/ProductChangeResponseDTO.java
package com.ktds.mvne.kos.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductChangeResponseDTO {
    private boolean success;
    private String message;
    private String transactionId;
    private String changeDate;
    private com.ktds.mvne.kos.mock.dto.ProductDetailDTO previousProduct;
    private com.ktds.mvne.kos.mock.dto.ProductDetailDTO newProduct;
    private Integer additionalFee;
}