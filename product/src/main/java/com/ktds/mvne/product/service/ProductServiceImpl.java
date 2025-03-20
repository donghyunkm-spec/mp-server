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
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final Timer productChangeOperationTimer;
    private final Timer circuitBreakerOperationTimer;
    private final Counter productChangeRequestCounter;
    private final Counter productChangeSuccessCounter;
    private final Counter productChangeErrorCounter;
    private final Counter productChangeSyncCounter;
    private final Counter productChangeAsyncCounter;
    private final Counter circuitBreakerStateChangeCounter;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final MeterRegistry meterRegistry;

    public ProductServiceImpl(
            KTAdapter ktAdapter,
            CustomerService customerService,
            ProductChangeResultRepository resultRepository,
            Timer productChangeOperationTimer,
            Timer circuitBreakerOperationTimer,
            Counter productChangeRequestCounter,
            Counter productChangeSuccessCounter,
            Counter productChangeErrorCounter,
            Counter productChangeSyncCounter,
            Counter productChangeAsyncCounter,
            Counter circuitBreakerStateChangeCounter,
            CircuitBreakerRegistry circuitBreakerRegistry,
            MeterRegistry meterRegistry) {
        this.ktAdapter = ktAdapter;
        this.customerService = customerService;
        this.resultRepository = resultRepository;
        this.productChangeOperationTimer = productChangeOperationTimer;
        this.circuitBreakerOperationTimer = circuitBreakerOperationTimer;
        this.productChangeRequestCounter = productChangeRequestCounter;
        this.productChangeSuccessCounter = productChangeSuccessCounter;
        this.productChangeErrorCounter = productChangeErrorCounter;
        this.productChangeSyncCounter = productChangeSyncCounter;
        this.productChangeAsyncCounter = productChangeAsyncCounter;
        this.circuitBreakerStateChangeCounter = circuitBreakerStateChangeCounter;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.meterRegistry = meterRegistry;

        // 서킷 브레이커 상태 이벤트 리스너 등록
        registerCircuitBreakerEventListener();
    }

    /**
     * 서킷 브레이커 상태 변경 이벤트 리스너를 등록합니다.
     */
    private void registerCircuitBreakerEventListener() {
        io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("productChange");

        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> {
                    String stateFrom = event.getStateTransition().getFromState().name();
                    String stateTo = event.getStateTransition().getToState().name();

                    log.info("Circuit breaker state changed from {} to {}", stateFrom, stateTo);

                    // 카운터 갱신 - 태그가 있는 카운터 생성하여 증가
                    meterRegistry.counter("circuit_breaker_state_change",
                                    "from", stateFrom,
                                    "to", stateTo,
                                    "state", stateTo)
                            .increment();

                    // 기본 카운터도 증가
                    circuitBreakerStateChangeCounter.increment();
                });
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
        productChangeRequestCounter.increment();

        log.info("Requesting product change - phoneNumber: {}, productCode: {}, reason: {}",
                phoneNumber, productCode, changeReason);

        // 타이머 시작
        Timer.Sample productChangeSample = Timer.start();

        // 1. 상품 변경 가능 여부 확인
        ProductCheckResponse checkResponse = checkProductChangeAvailability(phoneNumber, productCode);
        if (!checkResponse.isAvailable()) {
            log.warn("Product change not available: {}", checkResponse.getMessage());
            productChangeErrorCounter.increment();
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
            Timer.Sample circuitBreakerSample = Timer.start();
            ProductChangeResponse response = ktAdapter.changeProduct(phoneNumber, productCode, changeReason);
            circuitBreakerSample.stop(circuitBreakerOperationTimer);

            // 4. 변경 결과 저장
            result.setStatus(response.isSuccess() ? "COMPLETED" : "FAILED");
            result.setTransactionId(response.getTransactionId());
            result.setErrorMessage(response.isSuccess() ? null : response.getMessage());
            resultRepository.save(result);

            log.info("Product change request processed - status: {}, transactionId: {}",
                    result.getStatus(), result.getTransactionId());

            // 타이머 종료 및 카운터 증가
            productChangeSample.stop(productChangeOperationTimer);
            productChangeSuccessCounter.increment();
            productChangeSyncCounter.increment();

            return response;
        } catch (Exception e) {
            // 예외 발생 시 실패 상태로 저장
            log.error("Exception occurred during product change: {}", e.getMessage(), e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
            resultRepository.save(result);

            // 타이머 종료 및 카운터 증가
            productChangeSample.stop(productChangeOperationTimer);
            productChangeErrorCounter.increment();

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

        // 비동기 처리를 위한 카운터 증가
        productChangeAsyncCounter.increment();

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