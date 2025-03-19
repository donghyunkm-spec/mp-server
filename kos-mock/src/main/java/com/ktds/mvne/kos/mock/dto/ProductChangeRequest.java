// File: mp-server\kos-mock\src\main\java\com\ktds\mvne\kos\mock\dto\ProductChangeRequest.java
package com.ktds.mvne.kos.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductChangeRequest {
    private String phoneNumber;
    private String productCode;
    private String changeReason;
}