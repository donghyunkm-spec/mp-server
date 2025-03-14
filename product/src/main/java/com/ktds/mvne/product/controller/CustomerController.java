package com.ktds.mvne.product.controller;

import com.ktds.mvne.common.dto.ApiResponse;
import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.product.dto.CustomerInfoResponseDTO;
import com.ktds.mvne.product.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 고객 정보 조회 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "고객 정보 조회 API", description = "고객 정보 조회 관련 API를 제공합니다.")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @GetMapping("/{phoneNumber}")
    @Operation(summary = "고객 정보 조회", description = "고객의 회선 상태와 현재 사용 중인 상품 정보를 조회합니다.")
    public ResponseEntity<?> getCustomerInfo(
            @Parameter(description = "회선 번호", example = "01012345678")
            @PathVariable("phoneNumber") String phoneNumber) {

        log.info("고객 정보 조회 요청 - 전화번호: {}", phoneNumber);

        try {
            CustomerInfoResponseDTO response = customerService.getCustomerInfo(phoneNumber);

            // 응답 검증 - phoneNumber가 null인 경우 요청 값으로 대체
            if (response.getPhoneNumber() == null || response.getPhoneNumber().isEmpty()) {
                response.setPhoneNumber(phoneNumber);
                log.warn("서비스에서 반환된 전화번호가 null 또는 빈 문자열입니다. 요청 값으로 대체합니다.");
            }

            log.info("고객 정보 조회 완료 - 전화번호: {}, 상태: {}",
                    phoneNumber, response.getStatus());

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (BizException e) {
            // 비즈니스 예외는 별도 처리
            log.warn("고객 정보 조회 비즈니스 예외 - 전화번호: {}, 오류: {}",
                    phoneNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            // 기타 예외는 서버 오류로 처리
            log.error("고객 정보 조회 중 예외 발생 - 전화번호: {}", phoneNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
        }
    }
}