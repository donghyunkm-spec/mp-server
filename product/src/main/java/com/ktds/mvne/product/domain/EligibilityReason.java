package com.ktds.mvne.product.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 변경 적격성 검사 결과의 사유 엔티티입니다.
 */
@Entity
@Table(name = "eligibility_reasons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reasonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_id", nullable = false)
    private EligibilityCheck eligibilityCheck;

    @Column(nullable = false)
    private String reason;
}
