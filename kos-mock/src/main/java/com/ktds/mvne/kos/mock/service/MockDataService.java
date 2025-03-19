package com.ktds.mvne.kos.mock.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 목업 데이터를 관리하는 서비스
 */
@Service
public class MockDataService {
    @Getter private final Map<String, CustomerInfo> customerDb = new ConcurrentHashMap<>();
    @Getter private final Map<String, ProductInfo> productDb = new ConcurrentHashMap<>();
    @Getter private final Map<String, Map<String, BillingInfo>> billingDb = new ConcurrentHashMap<>();

    /**
     * 서비스 초기화 - 샘플 데이터 생성
     */
    @PostConstruct
    public void init() {
        // 샘플 고객 1
        customerDb.put("01012345678", new CustomerInfo(
                "01012345678",
                "CUST001",
                "ACTIVE",
                "5GX_STANDARD",
                "5G 스탠다드",
                55000
        ));

        // 샘플 고객 2
        customerDb.put("01098765432", new CustomerInfo(
                "01098765432",
                "CUST002",
                "ACTIVE",
                "5GX_PREMIUM",
                "5G 프리미엄",
                75000
        ));

        // 샘플 고객 3 (정지 상태)
        customerDb.put("01011112222", new CustomerInfo(
                "01011112222",
                "CUST003",
                "SUSPENDED",
                "LTE_STANDARD",
                "LTE 스탠다드",
                45000
        ));

        // 샘플 상품 데이터
        productDb.put("5GX_STANDARD", new ProductInfo("5GX_STANDARD", "5G 스탠다드", 55000, "ACTIVE"));
        productDb.put("5GX_PREMIUM", new ProductInfo("5GX_PREMIUM", "5G 프리미엄", 75000, "ACTIVE"));
        productDb.put("5GX_SIGNATURE", new ProductInfo("5GX_SIGNATURE", "5G 시그니처", 95000, "ACTIVE"));
        productDb.put("LTE_STANDARD", new ProductInfo("LTE_STANDARD", "LTE 스탠다드", 45000, "ACTIVE"));
        productDb.put("LTE_PREMIUM", new ProductInfo("LTE_PREMIUM", "LTE 프리미엄", 65000, "ACTIVE"));
        productDb.put("LTE_BASIC", new ProductInfo("LTE_BASIC", "LTE 베이직", 35000, "ACTIVE"));

        // 샘플 요금 데이터 생성
        initBillingData();
    }

    /**
     * 샘플 요금 데이터 초기화
     */
    private void initBillingData() {
        // 현재 날짜로부터 과거 6개월간의 요금 데이터 생성
        String[] phoneNumbers = {"01012345678", "01098765432", "01011112222"};

        for (String phoneNumber : phoneNumbers) {
            Map<String, BillingInfo> customerBilling = new ConcurrentHashMap<>();
            billingDb.put(phoneNumber, customerBilling);

            CustomerInfo customer = customerDb.get(phoneNumber);
            if (customer != null) {
                YearMonth currentMonth = YearMonth.now();

                for (int i = 0; i < 6; i++) {
                    YearMonth targetMonth = currentMonth.minusMonths(i);
                    String billingMonth = targetMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

                    // 기본 요금 (약간의 변동 추가)
                    int baseMonthlyFee = customer.getFee();
                    int additionalServiceFee = 5000 + (int)(Math.random() * 5000);
                    int deviceInstallmentFee = (int)(Math.random() * 30000);
                    int totalFee = baseMonthlyFee + additionalServiceFee + deviceInstallmentFee;

                    // 할인 내역
                    List<DiscountDetail> discounts = new ArrayList<>();
                    discounts.add(new DiscountDetail("D001", "약정 할인", 5000));
                    discounts.add(new DiscountDetail("D002", "멤버십 할인", (int)(Math.random() * 3000)));

                    // 요금 상세 내역
                    List<ItemDetail> details = new ArrayList<>();
                    details.add(new ItemDetail("M001", "기본료", baseMonthlyFee));
                    details.add(new ItemDetail("A001", "데이터 부가서비스", additionalServiceFee));

                    // 단말기 할부 정보
                    DeviceInstallmentDetail deviceInstallment = null;
                    if (deviceInstallmentFee > 0) {
                        deviceInstallment = new DeviceInstallmentDetail(
                                "DEV" + (10000 + (int)(Math.random() * 90000)),
                                "갤럭시 S24 울트라",
                                deviceInstallmentFee,
                                24 - (int)(Math.random() * 12)
                        );
                    }

                    // 요금 정보 객체 생성
                    BillingInfo billingInfo = new BillingInfo(
                            phoneNumber,
                            billingMonth,
                            totalFee,
                            details,
                            discounts,
                            deviceInstallment
                    );

                    // 요금 정보 저장
                    customerBilling.put(billingMonth, billingInfo);
                }
            }
        }
    }

    /**
     * 고객 정보 클래스
     */
    @Getter
    public static class CustomerInfo {
        private String phoneNumber;
        private String custId;
        private String status; // ACTIVE, SUSPENDED 등
        private String productCode;
        private String productName;
        private Integer fee;

        public CustomerInfo(String phoneNumber, String custId, String status, String productCode, String productName, Integer fee) {
            this.phoneNumber = phoneNumber;
            this.custId = custId;
            this.status = status;
            this.productCode = productCode;
            this.productName = productName;
            this.fee = fee;
        }

        // Setters
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setCustId(String custId) {
            this.custId = custId;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public void setFee(Integer fee) {
            this.fee = fee;
        }
    }

    /**
     * 상품 정보 클래스
     */
    @Getter
    public static class ProductInfo {
        private String productCode;
        private String productName;
        private Integer fee;
        private String status; // ACTIVE, INACTIVE

        public ProductInfo(String productCode, String productName, Integer fee, String status) {
            this.productCode = productCode;
            this.productName = productName;
            this.fee = fee;
            this.status = status;
        }

        // Setters
        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public void setFee(Integer fee) {
            this.fee = fee;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    /**
     * 요금 정보 클래스
     */
    @Getter
    public static class BillingInfo {
        private String phoneNumber;
        private String billingMonth;
        private Integer totalFee;
        private List<ItemDetail> details;
        private List<DiscountDetail> discounts;
        private DeviceInstallmentDetail deviceInstallment;

        public BillingInfo(
                String phoneNumber,
                String billingMonth,
                Integer totalFee,
                List<ItemDetail> details,
                List<DiscountDetail> discounts,
                DeviceInstallmentDetail deviceInstallment
        ) {
            this.phoneNumber = phoneNumber;
            this.billingMonth = billingMonth;
            this.totalFee = totalFee;
            this.details = details;
            this.discounts = discounts;
            this.deviceInstallment = deviceInstallment;
        }
    }

    /**
     * 요금 상세 항목 클래스
     */
    @Getter
    public static class ItemDetail {
        private String itemCode;
        private String itemName;
        private Integer amount;

        public ItemDetail(String itemCode, String itemName, Integer amount) {
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.amount = amount;
        }
    }

    /**
     * 할인 상세 정보 클래스
     */
    @Getter
    public static class DiscountDetail {
        private String discountCode;
        private String discountName;
        private Integer amount;

        public DiscountDetail(String discountCode, String discountName, Integer amount) {
            this.discountCode = discountCode;
            this.discountName = discountName;
            this.amount = amount;
        }
    }

    /**
     * 단말기 할부 정보 클래스
     */
    @Getter
    public static class DeviceInstallmentDetail {
        private String deviceId;
        private String model;
        private Integer amount;
        private Integer remainingMonths;

        public DeviceInstallmentDetail(String deviceId, String model, Integer amount, Integer remainingMonths) {
            this.deviceId = deviceId;
            this.model = model;
            this.amount = amount;
            this.remainingMonths = remainingMonths;
        }
    }

    /**
     * 현재 청구월 정보 조회
     * @return 현재 청구월 (yyyyMM 형식)
     */
    public String getCurrentBillingMonth() {
        // 현재 달의 15일 이후면 당월 청구 데이터가 생성된 것으로 가정
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        if (today.getDayOfMonth() >= 15) {
            return currentMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        } else {
            return currentMonth.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyyMM"));
        }
    }

    /**
     * 고객별 청구 요금 정보 조회
     * @param phoneNumber 전화번호
     * @param billingMonth 청구월 (yyyyMM 형식)
     * @return 청구 요금 정보
     */
    public BillingInfo getBillingInfo(String phoneNumber, String billingMonth) {
        Map<String, BillingInfo> customerBilling = billingDb.get(phoneNumber);
        if (customerBilling != null) {
            return customerBilling.get(billingMonth);
        }
        return null;
    }

    /**
     * 상품 변경 처리
     * @param phoneNumber 전화번호
     * @param newProductCode 변경할 상품 코드
     * @param changeReason 변경 사유
     * @return 처리 결과 (성공 시 true)
     */
    public boolean changeProduct(String phoneNumber, String newProductCode, String changeReason) {
        CustomerInfo customer = customerDb.get(phoneNumber);
        ProductInfo newProduct = productDb.get(newProductCode);

        if (customer != null && newProduct != null) {
            String previousProductCode = customer.getProductCode();

            // 상품 변경 처리
            customer.setProductCode(newProductCode);
            customer.setProductName(newProduct.getProductName());
            customer.setFee(newProduct.getFee());

            return true;
        }

        return false;
    }

    /**
     * 상품 변경 가능 여부 체크
     * @param phoneNumber 전화번호
     * @param productCode 변경할 상품 코드
     * @return 가능 여부 (가능 시 true)
     */
    public boolean isProductChangeEligible(String phoneNumber, String productCode) {
        CustomerInfo customer = customerDb.get(phoneNumber);
        ProductInfo product = productDb.get(productCode);

        if (customer == null || product == null) {
            return false;
        }

        // 고객 상태가 활성인지 확인
        if (!"ACTIVE".equals(customer.getStatus())) {
            return false;
        }

        // 상품이 활성 상태인지 확인
        if (!"ACTIVE".equals(product.getStatus())) {
            return false;
        }

        // 이미 같은 상품을 사용 중인 경우 변경 불필요
        if (customer.getProductCode().equals(productCode)) {
            return false;
        }

        return true;
    }

    /**
     * 트랜잭션 ID 생성
     * @return 트랜잭션 ID
     */
    public String generateTransactionId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}