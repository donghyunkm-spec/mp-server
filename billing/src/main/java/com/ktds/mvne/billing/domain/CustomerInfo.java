package com.ktds.mvne.billing.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 고객 정보 엔티티입니다.
 */
@Entity
@Table(name = "customer_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerInfoId;

    @Column(nullable = false)
    private String custId;

    @Column(nullable = false, length = 20)
    private String lineNumber;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, length = 50)
    private String productCode;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDate baseDate;

    private Integer fee;

    @Column(nullable = false)
    private String stampId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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
