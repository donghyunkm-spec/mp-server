package com.ktds.mvne.product.service;

import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import com.ktds.mvne.common.util.ValidationUtil;
import com.ktds.mvne.product.adapter.KTAdapter;
import com.ktds.mvne.product.dto.CustomerInfoResponseDTO;
import com.ktds.mvne.product.dto.ProductInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 고객 정보 조회 관련 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final KTAdapter ktAdapter;

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @Override
    public CustomerInfoResponseDTO getCustomerInfo(String phoneNumber) {
        log.info("고객 정보 조회 서비스 호출 - 전화번호: {}", phoneNumber);
        validatePhoneNumber(phoneNumber);

        try {
            CustomerInfoResponseDTO customerInfo = ktAdapter.getCustomerInfo(phoneNumber);
            log.info("KTAdapter에서 반환된 정보: {}", customerInfo);

            // 전화번호가 null인 경우 처리
            if (customerInfo.getPhoneNumber() == null || customerInfo.getPhoneNumber().isEmpty()) {
                log.warn("고객 전화번호가 null로 반환됨. 요청 값으로 설정: {}", phoneNumber);
                customerInfo.setPhoneNumber(phoneNumber);
            }

            // 상품 정보가 null인 경우 기본 객체 생성
            if (customerInfo.getCurrentProduct() == null) {
                log.warn("고객 상품 정보가 null로 반환됨. 빈 객체 생성");
                ProductInfoDTO emptyProduct = new ProductInfoDTO();
                emptyProduct.setProductCode("UNKNOWN");
                emptyProduct.setProductName("Unknown Product");
                emptyProduct.setFee(0);
                customerInfo.setCurrentProduct(emptyProduct);
            }

            log.info("고객 정보 조회 완료 - 전화번호: {}, 상태: {}",
                    phoneNumber, customerInfo.getStatus());

            return customerInfo;

        } catch (Exception e) {
            log.error("고객 정보 조회 실패 - 전화번호: {} - 오류: {}",
                    phoneNumber, e.getMessage(), e);

            // 오류 발생 시 기본 응답 생성
            return createEmptyResponse(phoneNumber);
        }
    }

    /**
     * 전화번호의 유효성을 검사합니다.
     *
     * @param phoneNumber 검사할 전화번호
     * @throws BizException 유효하지 않은 전화번호인 경우
     */
    private void validatePhoneNumber(String phoneNumber) {
        if (!ValidationUtil.validatePhoneNumber(phoneNumber)) {
            log.warn("유효하지 않은 전화번호 형식: {}", phoneNumber);
            throw new BizException(ErrorCode.BAD_REQUEST, "유효하지 않은 전화번호 형식입니다");
        }
    }

    /**
     * 오류 상황에서 반환할 기본 응답을 생성합니다.
     *
     * @param phoneNumber 전화번호
     * @return 기본 고객 정보 응답
     */
    private CustomerInfoResponseDTO createEmptyResponse(String phoneNumber) {
        CustomerInfoResponseDTO response = new CustomerInfoResponseDTO();
        response.setPhoneNumber(phoneNumber);
        response.setStatus("UNKNOWN");

        ProductInfoDTO emptyProduct = new ProductInfoDTO();
        emptyProduct.setProductCode("UNKNOWN");
        emptyProduct.setProductName("Unknown Product");
        emptyProduct.setFee(0);

        response.setCurrentProduct(emptyProduct);

        return response;
    }
}