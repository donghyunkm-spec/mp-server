package com.ktds.mvne.product.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 상품 변경 내역 엔티티입니다.
 */
@Entity
@Table(name = "product_changes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long changeId;

    @Column(nullable = false)
    private String custId;

    @Column(nullable = false, length = 20)
    private String lineNumber;

    @Column(nullable = false, length = 50)
    private String oldProductCode;

    @Column(nullable = false, length = 50)
    private String newProductCode;

    @Column(nullable = false)
    private LocalDateTime changeDate;

    private boolean success;

    private String errorMessage;

    private Integer additionalFee;

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
