package com.ktds.mvne.product.repository;

import com.ktds.mvne.product.domain.ProductChangeResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 상품 변경 결과를 저장하고 조회하는 리포지토리 인터페이스입니다.
 */
@Repository
public interface ProductChangeResultRepository extends JpaRepository<ProductChangeResult, Long> {

    /**
     * 요청 ID로 상품 변경 결과를 조회합니다.
     *
     * @param requestId 요청 ID
     * @return 상품 변경 결과
     */
    Optional<ProductChangeResult> findByRequestId(String requestId);

    /**
     * 상태별로 상품 변경 결과를 조회합니다.
     *
     * @param status 상태
     * @return 상품 변경 결과 목록
     */
    List<ProductChangeResult> findByStatus(String status);

    /**
     * 회선 번호로 상품 변경 결과를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 상품 변경 결과 목록
     */
    List<ProductChangeResult> findByPhoneNumber(String phoneNumber);
}
