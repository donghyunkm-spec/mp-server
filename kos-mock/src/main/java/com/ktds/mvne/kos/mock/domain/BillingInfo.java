// kosmock/src/main/java/com/ktds/mvne/kosmock/domain/BillingInfo.java
package com.ktds.mvne.kos.mock.domain;

import com.ktds.mvne.kos.mock.domain.BillingDetail;
import com.ktds.mvne.kos.mock.domain.BillingDiscount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billing_infos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String billingMonth; // YYYYMM 형식
    private Integer totalFee;
    private Integer monthlyFee;
    private Integer additionalServiceFee;
    private Integer deviceInstallmentFee;

    @OneToMany(mappedBy = "billingInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillingDetail> details = new ArrayList<>();

    @OneToMany(mappedBy = "billingInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillingDiscount> discounts = new ArrayList<>();

    @Embedded
    private com.ktds.mvne.kos.mock.domain.DeviceInstallment deviceInstallment;
}