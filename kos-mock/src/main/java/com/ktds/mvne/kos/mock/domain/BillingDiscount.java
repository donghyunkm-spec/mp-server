// kosmock/src/main/java/com/ktds/mvne/kosmock/domain/BillingDiscount.java
package com.ktds.mvne.kos.mock.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "billing_discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_info_id")
    private com.ktds.mvne.kos.mock.domain.BillingInfo billingInfo;

    private String discountCode;
    private String discountName;
    private Integer amount;
}