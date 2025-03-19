package com.ktds.mvne.kos.mock.repository;

import com.ktds.mvne.kos.mock.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByPhoneNumber(String phoneNumber);
}