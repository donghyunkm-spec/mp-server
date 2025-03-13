package com.ktds.mvne.product.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상품 변경 적격성 검사 엔티티입니다.
 */
@Entity
@Table(name = "eligibility_checks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkId;

    @Column(nullable = false)
    private String custId;

    @Column(nullable = false, length = 20)
    private String lineNumber;

    @Column(nullable = false, length = 50)
    private String productCode;

    private boolean eligible;

    @Column(length = 20)
    private String lineStatus;

    @Column(length = 20)
    private String productStatus;

    @Column(nullable = false)
    private String stampId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "eligibilityCheck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EligibilityReason> reasons = new ArrayList<>();

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
