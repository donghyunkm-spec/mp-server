// kosmock/src/main/java/com/ktds/mvne/kosmock/domain/Product.java
package com.ktds.mvne.kos.mock.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private String productCode;
    private String productName;
    private Integer fee;
    private String status; // ACTIVE, INACTIVE
}