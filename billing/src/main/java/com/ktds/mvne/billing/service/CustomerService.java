package com.ktds.mvne.billing.service;

import com.ktds.mvne.billing.dto.CustomerInfoResponseDTO;

/**
 * 고객 정보 조회 관련 서비스 인터페이스입니다.
 */
public interface CustomerService {

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    CustomerInfoResponseDTO getCustomerInfo(String phoneNumber);
}
