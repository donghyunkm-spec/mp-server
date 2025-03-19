// kosmock/src/main/java/com/ktds/mvne/kosmock/repository/ProductChangeHistoryRepository.java
package com.ktds.mvne.kos.mock.repository;

import com.ktds.mvne.kos.mock.domain.ProductChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductChangeHistoryRepository extends JpaRepository<ProductChangeHistory, String> {
    List<ProductChangeHistory> findByPhoneNumberOrderByChangeDateDesc(String phoneNumber);
}