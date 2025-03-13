package com.ktds.mvne.billing.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 부가 서비스 정보 엔티티입니다.
 */
@Entity
@Table(name = "additional_services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", nullable = false)
    private BillingInfo billingInfo;

    @Column(nullable = false)
    private String name;

    private Integer fee;
}

