package com.ktds.mvne.product.service;

import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import com.ktds.mvne.common.exception.ExternalSystemException;
import com.ktds.mvne.product.adapter.KTAdapter;
import com.ktds.mvne.product.dto.CustomerInfoResponseDTO;
import com.ktds.mvne.product.dto.ProductCheckResponse;
import com.ktds.mvne.product.dto.ProductChangeResponse;
import com.ktds.mvne.product.dto.ProductInfoDTO;
import com.ktds.mvne.product.repository.ProductChangeResultRepository;
import com.ktds.mvne.product.domain.ProductChangeResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 상품 정보 및 변경 관련 서비스 구현체입니다.
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final KTAdapter ktAdapter;
    private final CustomerService customerService;
    private final ProductChangeResultRepository resultRepository;
    private final WebClient webClient;

    @Value("${kos.adapter.base-url}")
    private String kosAdapterBaseUrl;

    public ProductServiceImpl(KTAdapter ktAdapter, CustomerService customerService,
                              ProductChangeResultRepository resultRepository, WebClient webClient) {
        this.ktAdapter = ktAdapter;
        this.customerService = customerService;
        this.resultRepository = resultRepository;
        this.webClient = webClient;
    }

    /**
     * 상품 변경 가능 여부를 확인합니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 변경하려는 상품 코드
     * @return 상품 변경 가능 여부 및 정보
     */
    @Override
    public ProductCheckResponse checkProductChangeAvailability(String phoneNumber, String productCode) {
        validatePhoneNumber(phoneNumber);
        validateProductCode(productCode);

        try {
            // 1. 고객 정보 및 상태 확인
            CustomerInfoResponseDTO customerInfo = customerService.getCustomerInfo(phoneNumber);
            System.out.println(customerInfo.toString());
            // 고객 정보나 현재 상품 정보가 없는 경우 처리
            if (customerInfo == null || customerInfo.getCurrentProduct() == null) {
                log.warn("Customer info or current product is null for phoneNumber: {}", phoneNumber);
                return ProductCheckResponse.builder()
                        .available(false)
                        .message("고객 정보를 조회할 수 없습니다.")
                        .build();
            }

            // 2. 현재 사용 중인 상품과 변경하려는 상품이 같은 경우
            if (customerInfo.getCurrentProduct().getProductCode().equals(productCode)) {
                log.info("Customer already using the requested product: {}", productCode);
                return ProductCheckResponse.builder()
                        .available(false)
                        .message("이미 해당 상품을 사용 중입니다.")
                        .currentProduct(customerInfo.getCurrentProduct())
                        .targetProduct(customerInfo.getCurrentProduct())
                        .build();
            }

            // 3. 회선 상태 확인 (정지 상태가 아니어야 함)
            if (!"ACTIVE".equals(customerInfo.getStatus())) {
                log.info("Line status is not active: {}", customerInfo.getStatus());
                return ProductCheckResponse.builder()
                        .available(false)
                        .message("정지 상태의 회선은 상품 변경이 불가능합니다.")
                        .currentProduct(customerInfo.getCurrentProduct())
                        .targetProduct(null)
                        .build();
            }

            // 4. 변경하려는 상품 정보 조회
            ProductInfoDTO targetProduct = null;
            try {
                targetProduct = ktAdapter.getProductInfo(productCode);
            } catch (Exception e) {
                log.error("Failed to get target product info: {}", e.getMessage(), e);
            }

            if (targetProduct == null) {
                log.warn("Target product info is null for productCode: {}", productCode);
                return ProductCheckResponse.builder()
                        .available(false)
                        .message("존재하지 않는 상품입니다.")
                        .currentProduct(customerInfo.getCurrentProduct())
                        .targetProduct(null)
                        .build();
            }

            // 5. 변경 가능 여부 확인 - 정상적으로 처리된 경우
            log.info("Product change is available from {} to {}",
                    customerInfo.getCurrentProduct().getProductCode(), productCode);
            return ProductCheckResponse.builder()
                    .available(true)
                    .message("상품 변경이 가능합니다.")
                    .currentProduct(customerInfo.getCurrentProduct())
                    .targetProduct(targetProduct)
                    .build();
        } catch (Exception e) {
            log.error("Error checking product change availability for {}, {}: {}",
                    phoneNumber, productCode, e.getMessage(), e);

            // 예외 발생 시 false 응답 반환 (서비스 장애 방지)
            return ProductCheckResponse.builder()
                    .available(false)
                    .message("상품 변경 가능 여부 확인 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 상품을 변경합니다.
     * 서킷 브레이커 패턴이 적용되어 있어, KT 어댑터 호출 실패 시 지정된 폴백 메소드가 실행됩니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 변경하려는 상품 코드
     * @param changeReason 변경 사유
     * @return 상품 변경 결과
     */
    @Override
    @Transactional
    @CircuitBreaker(name = "productChange", fallbackMethod = "changeProductFallback")
    public ProductChangeResponse changeProduct(String phoneNumber, String productCode, String changeReason) {
        validatePhoneNumber(phoneNumber);
        validateProductCode(productCode);

        log.info("Requesting product change - phoneNumber: {}, productCode: {}, reason: {}",
                phoneNumber, productCode, changeReason);

        // 1. 상품 변경 가능 여부 확인
        ProductCheckResponse checkResponse = checkProductChangeAvailability(phoneNumber, productCode);
        if (!checkResponse.isAvailable()) {
            log.warn("Product change not available: {}", checkResponse.getMessage());
            throw new BizException(ErrorCode.BAD_REQUEST, checkResponse.getMessage());
        }

        // 2. 상품 변경 요청 생성
        String requestId = UUID.randomUUID().toString();
        log.info("Creating product change request with ID: {}", requestId);

        ProductChangeResult result = ProductChangeResult.builder()
                .requestId(requestId)
                .phoneNumber(phoneNumber)
                .productCode(productCode)
                .changeReason(changeReason)
                .status("REQUESTED")
                .timestamp(LocalDateTime.now())
                .build();
        resultRepository.save(result);

        try {
            // 3. KT 어댑터를 통해 상품 변경 요청
            ProductChangeResponse response = ktAdapter.changeProduct(phoneNumber, productCode, changeReason);

            // 4. 변경 결과 저장
            result.setStatus(response.isSuccess() ? "COMPLETED" : "FAILED");
            result.setTransactionId(response.getTransactionId());
            result.setErrorMessage(response.isSuccess() ? null : response.getMessage());
            resultRepository.save(result);

            log.info("Product change request processed - status: {}, transactionId: {}",
                    result.getStatus(), result.getTransactionId());

            return response;
        } catch (Exception e) {
            // 예외 발생 시 실패 상태로 저장
            log.error("Exception occurred during product change: {}", e.getMessage(), e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
            resultRepository.save(result);

            // Circuit breaker 에 의해 fallback이 호출되지 않은 경우 예외 재발생
            throw e;
        }
    }

    /**
     * 상품 변경 서킷 브레이커 폴백 메소드입니다.
     * KT 어댑터 호출 실패 시 비동기 처리를 위해 요청을 큐에 저장합니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 변경하려는 상품 코드
     * @param changeReason 변경 사유
     * @param t 발생한 예외
     * @return 폴백 응답
     */
    public ProductChangeResponse changeProductFallback(String phoneNumber, String productCode,
                                                       String changeReason, Throwable t) {
        log.warn("Circuit breaker is open. Falling back for productChange: {}, {}", phoneNumber, productCode, t);

        // 1. 폴백 처리를 위한 요청 ID 생성
        String requestId = UUID.randomUUID().toString();

        // 2. 비동기 처리를 위한 상품 변경 결과 저장
        ProductChangeResult result = ProductChangeResult.builder()
                .requestId(requestId)
                .phoneNumber(phoneNumber)
                .productCode(productCode)
                .changeReason(changeReason)
                .status("QUEUED")
                .errorMessage("서비스 일시 지연: " + t.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        resultRepository.save(result);

        log.info("Product change request queued - requestId: {}", requestId);

        // 3. 폴백 응답 생성
        return ProductChangeResponse.builder()
                .success(false)
                .message("요청이 큐에 저장되었습니다. 잠시 후에 처리됩니다.")
                .transactionId(requestId)
                .build();
    }

    /**
     * 전화번호의 유효성을 검사합니다.
     *
     * @param phoneNumber 검사할 전화번호
     * @throws BizException 유효하지 않은 전화번호인 경우
     */
    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty() || !phoneNumber.matches("^01(?:0|1|[6-9])[0-9]{7,8}$")) {
            throw new BizException(ErrorCode.BAD_REQUEST, "유효하지 않은 전화번호 형식입니다");
        }
    }

    /**
     * 상품 코드의 유효성을 검사합니다.
     *
     * @param productCode 검사할 상품 코드
     * @throws BizException 유효하지 않은 상품 코드인 경우
     */
    private void validateProductCode(String productCode) {
        if (productCode == null || productCode.isEmpty()) {
            throw new BizException(ErrorCode.BAD_REQUEST, "상품 코드는 필수 항목입니다");
        }

        // 상품 코드 형식 검증을 완화하거나 현재 사용 중인 형식에 맞게 조정
        // 예: 대문자, 숫자, 언더스코어를 허용하는 패턴
        if (!productCode.matches("^[A-Z0-9_]{3,20}$")) {
            throw new BizException(ErrorCode.BAD_REQUEST, "유효하지 않은 상품 코드 형식입니다");
        }
    }
}