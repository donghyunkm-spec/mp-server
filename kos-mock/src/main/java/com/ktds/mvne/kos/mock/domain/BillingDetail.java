// kosmock/src/main/java/com/ktds/mvne/kosmock/domain/BillingDetail.java
package com.ktds.mvne.kos.mock.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "billing_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_info_id")
    private com.ktds.mvne.kos.mock.domain.BillingInfo billingInfo;

    private String itemCode;
    private String itemName;
    private Integer amount;
}