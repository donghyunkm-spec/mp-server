// kosmock/src/main/java/com/ktds/mvne/kosmock/service/DataInitializationService.java
package com.ktds.mvne.kosmock.service;

import com.ktds.mvne.kos.mock.domain.*;
import com.ktds.mvne.kos.mock.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BillingInfoRepository billingInfoRepository;
    private final ProductChangeHistoryRepository productChangeHistoryRepository;

    @PostConstruct
    @Transactional
    public void initializeData() {
        initializeProducts();
        initializeCustomers();
        initializeBillingInfo();

        log.info("데이터 초기화 완료");
    }

    private void initializeProducts() {
        List<Product> products = Arrays.asList(
                Product.builder()
                        .productCode("5GX_STANDARD")
                        .productName("5G 스탠다드")
                        .fee(55000)
                        .status("ACTIVE")
                        .build(),
                Product.builder()
                        .productCode("5GX_PREMIUM")
                        .productName("5G 프리미엄")
                        .fee(75000)
                        .status("ACTIVE")
                        .build(),
                Product.builder()
                        .productCode("5GX_SIGNATURE")
                        .productName("5G 시그니처")
                        .fee(95000)
                        .status("ACTIVE")
                        .build(),
                Product.builder()
                        .productCode("LTE_STANDARD")
                        .productName("LTE 스탠다드")
                        .fee(45000)
                        .status("ACTIVE")
                        .build(),
                Product.builder()
                        .productCode("LTE_PREMIUM")
                        .productName("LTE 프리미엄")
                        .fee(65000)
                        .status("ACTIVE")
                        .build()
        );

        productRepository.saveAll(products);
    }

    private void initializeCustomers() {
        List<Customer> customers = Arrays.asList(
                Customer.builder()
                        .phoneNumber("01012345678")
                        .custId("CUST001")
                        .status("ACTIVE")
                        .productCode("5GX_STANDARD")
                        .productName("5G 스탠다드")
                        .productFee(55000)
                        .build(),
                Customer.builder()
                        .phoneNumber("01098765432")
                        .custId("CUST002")
                        .status("ACTIVE")
                        .productCode("LTE_PREMIUM")
                        .productName("LTE 프리미엄")
                        .productFee(65000)
                        .build(),
                Customer.builder()
                        .phoneNumber("01011112222")
                        .custId("CUST003")
                        .status("SUSPENDED")
                        .productCode("5GX_PREMIUM")
                        .productName("5G 프리미엄")
                        .productFee(75000)
                        .build()
        );

        customerRepository.saveAll(customers);
    }

    private void initializeBillingInfo() {
        // 현재 월 및 이전 2개월에 대한 청구 정보 생성
        YearMonth currentMonth = YearMonth.now();
        List<String> phoneNumbers = Arrays.asList("01012345678", "01098765432", "01011112222");

        for (String phoneNumber : phoneNumbers) {
            Customer customer = customerRepository.findByPhoneNumber(phoneNumber).orElseThrow();

            // 현재 월 청구 정보
            createBillingInfo(phoneNumber, currentMonth, customer);

            // 이전 월 청구 정보
            createBillingInfo(phoneNumber, currentMonth.minusMonths(1), customer);

            // 2개월 전 청구 정보
            createBillingInfo(phoneNumber, currentMonth.minusMonths(2), customer);
        }
    }

    private void createBillingInfo(String phoneNumber, YearMonth yearMonth, Customer customer) {
        String billingMonth = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 기본 청구 정보
        BillingInfo billingInfo = BillingInfo.builder()
                .phoneNumber(phoneNumber)
                .billingMonth(billingMonth)
                .monthlyFee(customer.getProductFee())
                .additionalServiceFee(5000)
                .deviceInstallmentFee(20000)
                .totalFee(customer.getProductFee() + 5000 + 20000 - 10000) // 기본료 + 부가서비스 + 할부금 - 할인
                .build();

        // 단말기 할부 정보
        DeviceInstallment deviceInstallment = DeviceInstallment.builder()
                .deviceId("DEVICE" + phoneNumber.substring(phoneNumber.length() - 4))
                .model("Galaxy S22")
                .amount(20000)
                .remainingMonths(18)
                .build();

        billingInfo.setDeviceInstallment(deviceInstallment);

        // 저장
        BillingInfo savedBillingInfo = billingInfoRepository.save(billingInfo);

        // 청구 상세 항목
        BillingDetail monthlyFeeDetail = BillingDetail.builder()
                .billingInfo(savedBillingInfo)
                .itemCode("MONTHLY_FEE")
                .itemName("기본 월정액")
                .amount(customer.getProductFee())
                .build();

        BillingDetail additionalServiceDetail = BillingDetail.builder()
                .billingInfo(savedBillingInfo)
                .itemCode("ADD_SVC")
                .itemName("부가서비스 이용료")
                .amount(5000)
                .build();

        // 할인 정보
        BillingDiscount discount = BillingDiscount.builder()
                .billingInfo(savedBillingInfo)
                .discountCode("PROMO_DISCOUNT")
                .discountName("프로모션 할인")
                .amount(10000)
                .build();

        // 연관관계 설정 및 저장
        savedBillingInfo.getDetails().add(monthlyFeeDetail);
        savedBillingInfo.getDetails().add(additionalServiceDetail);
        savedBillingInfo.getDiscounts().add(discount);

        billingInfoRepository.save(savedBillingInfo);
    }
}