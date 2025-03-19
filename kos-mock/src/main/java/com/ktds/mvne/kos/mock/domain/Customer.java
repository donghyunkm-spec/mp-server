// kosmock/src/main/java/com/ktds/mvne/kosmock/domain/Customer.java
package com.ktds.mvne.kos.mock.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    private String phoneNumber;
    private String custId;
    private String status; // ACTIVE, SUSPENDED 등
    private String productCode;
    private String productName;
    private Integer productFee;
}