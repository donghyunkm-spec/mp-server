package com.ktds.mvne.product.service;

import com.ktds.mvne.product.adapter.KTAdapter;
import com.ktds.mvne.product.domain.ProductChangeResult;
import com.ktds.mvne.product.dto.ProductChangeResponse;
import com.ktds.mvne.product.repository.ProductChangeResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 상품 변경 큐에 저장된 요청을 비동기적으로 처리하는 워커 클래스입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductWorker {

    private final KTAdapter ktAdapter;
    private final ProductChangeResultRepository resultRepository;

    /**
     * 상품 변경 큐에 저장된 요청을 주기적으로 처리합니다.
     */
    @Scheduled(fixedDelayString = "${worker.retry-interval-ms:60000}")
    @Transactional
    public void processProductChangeRequests() {
        log.debug("Processing queued product change requests");
        
        List<ProductChangeResult> queuedRequests = resultRepository.findByStatus("QUEUED");
        if (queuedRequests.isEmpty()) {
            log.debug("No queued requests found");
            return;
        }
        
        log.info("Found {} queued product change requests", queuedRequests.size());
        
        for (ProductChangeResult request : queuedRequests) {
            try {
                log.info("Processing queued request: {}", request.getRequestId());
                handleRetry(request);
            } catch (Exception e) {
                log.error("Error processing queued request {}: {}", request.getRequestId(), e.getMessage(), e);
                updateFailedResult(request, e.getMessage());
            }
        }
    }

    /**
     * 상품 변경 요청을 재시도합니다.
     *
     * @param request 상품 변경 요청 결과
     */
    private void handleRetry(ProductChangeResult request) {
        log.debug("Retrying product change for phoneNumber: {}, productCode: {}", 
                request.getPhoneNumber(), request.getProductCode());
        
        // 재시도 상태로 업데이트
        request.setStatus("RETRYING");
        request.setTimestamp(LocalDateTime.now());
        resultRepository.save(request);
        
        try {
            // KT 어댑터를 통해 상품 변경 요청
            ProductChangeResponse response = ktAdapter.changeProduct(
                    request.getPhoneNumber(), request.getProductCode(), request.getChangeReason());
            
            // 성공한 경우 결과 업데이트
            if (response.isSuccess()) {
                updateSuccessResult(request, response);
            } else {
                updateFailedResult(request, response.getMessage());
            }
        } catch (Exception e) {
            // 예외 발생 시 결과 업데이트
            log.error("Retry failed for request {}: {}", request.getRequestId(), e.getMessage(), e);
            updateFailedResult(request, e.getMessage());
        }
    }

    /**
     * 성공한 상품 변경 요청 결과를 업데이트합니다.
     *
     * @param request 상품 변경 요청 결과
     * @param response KT 어댑터 응답
     */
    private void updateSuccessResult(ProductChangeResult request, ProductChangeResponse response) {
        request.setStatus("COMPLETED");
        request.setTransactionId(response.getTransactionId());
        request.setErrorMessage(null);
        request.setTimestamp(LocalDateTime.now());
        resultRepository.save(request);
        log.info("Request {} processed successfully", request.getRequestId());
    }

    /**
     * 실패한 상품 변경 요청 결과를 업데이트합니다.
     *
     * @param request 상품 변경 요청 결과
     * @param errorMessage 오류 메시지
     */
    private void updateFailedResult(ProductChangeResult request, String errorMessage) {
        request.setStatus("FAILED");
        request.setErrorMessage(errorMessage);
        request.setTimestamp(LocalDateTime.now());
        resultRepository.save(request);
        log.warn("Request {} processing failed: {}", request.getRequestId(), errorMessage);
    }
}
