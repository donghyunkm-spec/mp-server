// kosmock/src/main/java/com/ktds/mvne/kosmock/repository/ProductRepository.java
package com.ktds.mvne.kos.mock.repository;

import com.ktds.mvne.kos.mock.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByProductCode(String productCode);
}