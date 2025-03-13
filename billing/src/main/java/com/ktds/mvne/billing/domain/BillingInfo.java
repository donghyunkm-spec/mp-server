package com.ktds.mvne.billing.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 요금 정보 엔티티입니다.
 */
@Entity
@Table(name = "billing_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billingId;

    @Column(nullable = false)
    private String custId;

    @Column(nullable = false, length = 6)
    private String yearMonth;

    @Column(nullable = false)
    private LocalDate baseDate;

    private Integer totalFee;

    private Integer monthlyFee;

    private Integer additionalServiceFee;

    private Integer deviceInstallmentFee;

    @Column(nullable = false)
    private String stampId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "billingInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discount> discounts = new ArrayList<>();

    @OneToMany(mappedBy = "billingInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdditionalService> additionalServices = new ArrayList<>();

    /**
     * 생성 시간을 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * 수정 시간을 설정합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
