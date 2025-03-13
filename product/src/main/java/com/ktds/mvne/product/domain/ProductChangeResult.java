package com.ktds.mvne.product.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 상품 변경 요청 처리 결과 엔티티입니다.
 */
@Entity
@Table(name = "product_change_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductChangeResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String requestId;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String productCode;

    private String changeReason;

    @Column(nullable = false)
    private String status;

    private String transactionId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String errorMessage;
}