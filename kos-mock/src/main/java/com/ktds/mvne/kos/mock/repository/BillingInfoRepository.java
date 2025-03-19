// kosmock/src/main/java/com/ktds/mvne/kosmock/repository/BillingInfoRepository.java
package com.ktds.mvne.kos.mock.repository;

import com.ktds.mvne.kos.mock.domain.BillingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingInfoRepository extends JpaRepository<BillingInfo, Long> {
    Optional<BillingInfo> findByPhoneNumberAndBillingMonth(String phoneNumber, String billingMonth);
}
