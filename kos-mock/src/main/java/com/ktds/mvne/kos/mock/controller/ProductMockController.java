// File: mp-server\kos-mock\src\main\java\com\ktds\mvne\kos\mock\controller\ProductMockController.java
package com.ktds.mvne.kos.mock.controller;

import com.ktds.mvne.kos.mock.util.MockDataGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * KT 영업시스템의 상품 관련 API를 목업으로 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/mock")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "상품 목업 API", description = "KT 영업시스템의 상품 관련 API를 목업으로 제공합니다.")
public class ProductMockController {

    private final MockDataGenerator mockDataGenerator;

    /**
     * 고객 정보를 조회합니다.
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @GetMapping(value = "/customers/{phoneNumber}", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "고객 정보 조회", description = "고객의 회선 상태와 현재 사용 중인 상품 정보를 조회합니다.")
    public ResponseEntity<String> getCustomerInfo(
            @Parameter(description = "회선 번호", example = "01012345678")
            @PathVariable("phoneNumber") String phoneNumber) {
        log.info("Mock 고객 정보 조회 요청 - 전화번호: {}", phoneNumber);

        String response = mockDataGenerator.generateCustomerInfoResponse(phoneNumber);
        log.info("Mock 고객 정보 조회 응답 생성 완료 - 전화번호: {}", phoneNumber);
        log.debug("응답 내용: {}", response);

        return ResponseEntity.ok(response);
    }

    /**
     * 상품 정보를 조회합니다.
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    @GetMapping(value = "/products/{productCode}", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "상품 정보 조회", description = "상품 정보를 조회합니다.")
    public ResponseEntity<String> getProductInfo(
            @Parameter(description = "상품 코드", example = "5GX_STANDARD")
            @PathVariable("productCode") String productCode) {
        log.info("Mock 상품 정보 조회 요청 - 상품코드: {}", productCode);

        // 수정된 부분: 입력 상품코드 그대로 응답으로 반환하도록 수정
        String response = mockDataGenerator.generateProductInfoResponse(productCode, productCode);
        log.info("Mock 상품 정보 조회 응답 생성 완료 - 상품코드: {}", productCode);
        log.debug("응답 내용: {}", response);

        return ResponseEntity.ok(response);
    }

    /**
     * 상품을 변경합니다.
     *
     * @param phoneNumber 회선 번호
     * @param productCode 변경하려는 상품 코드
     * @param changeReason 변경 사유
     * @return 상품 변경 결과
     */
    @PostMapping(value = "/products/change", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "상품 변경", description = "현재 사용 중인 상품을 다른 상품으로 변경합니다.")
    public ResponseEntity<String> changeProduct(
            @Parameter(description = "회선 번호", example = "01012345678")
            @RequestParam("phoneNumber") String phoneNumber,
            @Parameter(description = "변경하려는 상품 코드", example = "5GX_PREMIUM")
            @RequestParam("productCode") String productCode,
            @Parameter(description = "변경 사유", example = "데이터 사용량 증가")
            @RequestParam("changeReason") String changeReason) {
        log.info("Mock 상품 변경 요청 - 전화번호: {}, 상품코드: {}, 변경사유: {}",
                phoneNumber, productCode, changeReason);

        String response = mockDataGenerator.generateProductChangeResponse(phoneNumber, productCode, changeReason);
        log.info("Mock 상품 변경 응답 생성 완료 - 전화번호: {}, 상품코드: {}", phoneNumber, productCode);
        log.debug("응답 내용: {}", response);

        return ResponseEntity.ok(response);
    }

    /**
     * KOS 어댑터가 요청하는 경로에 맞추기 위한 추가 엔드포인트
     *
     * @param phoneNumber 회선 번호
     * @return 고객 정보
     */
    @GetMapping(value = "/kos/customers/{phoneNumber}", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "고객 정보 조회 (KOS 어댑터용)", description = "KOS 어댑터 호환용 고객 정보 조회 API")
    public ResponseEntity<String> getCustomerInfoForKosAdapter(
            @Parameter(description = "회선 번호", example = "01012345678")
            @PathVariable("phoneNumber") String phoneNumber) {
        log.info("Mock 고객 정보 조회(KOS 어댑터용) 요청 - 전화번호: {}", phoneNumber);

        String response = mockDataGenerator.generateCustomerInfoResponse(phoneNumber);
        log.info("Mock 고객 정보 조회(KOS 어댑터용) 응답 생성 완료 - 전화번호: {}", phoneNumber);
        log.debug("응답 내용: {}", response);

        return ResponseEntity.ok(response);
    }

    /**
     * KOS 어댑터가 요청하는 경로에 맞추기 위한 추가 엔드포인트
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    @GetMapping(value = "/kos/products/{productCode}", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "상품 정보 조회 (KOS 어댑터용)", description = "KOS 어댑터 호환용 상품 정보 조회 API")
    public ResponseEntity<String> getProductInfoForKosAdapter(
            @Parameter(description = "상품 코드", example = "5GX_STANDARD")
            @PathVariable("productCode") String productCode) {
        log.info("Mock 상품 정보 조회(KOS 어댑터용) 요청 - 상품코드: {}", productCode);

        // 수정된 부분: 입력 상품코드 그대로 응답으로 반환하도록 수정
        String response = mockDataGenerator.generateProductInfoResponse(productCode, productCode);
        log.info("Mock 상품 정보 조회(KOS 어댑터용) 응답 생성 완료 - 상품코드: {}", productCode);
        log.debug("응답 내용: {}", response);

        return ResponseEntity.ok(response);
    }

    /**
     * 상품 정보를 조회합니다. (billings 경로 대응용)
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    @PostMapping(value = "/billings/product-info", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "상품 정보 조회 (billings 경로 대응용)", description = "상품 정보를 조회합니다.")
    public ResponseEntity<String> getProductInfoForBillings(
            @RequestBody(required = false) Map<String, String> requestBody) {

        // 요청 본문에서 상품 코드 추출
        String productCode = (requestBody != null && requestBody.containsKey("productCode"))
                ? requestBody.get("productCode")
                : "5GX_STANDARD";  // 기본값 설정

        log.info("Mock 상품 정보 조회(billings 경로) 요청 - 상품코드: {}", productCode);

        // 수정된 부분: 입력 상품코드 그대로 응답으로 반환하도록 수정
        String response = mockDataGenerator.generateProductInfoResponse(productCode, productCode);
        log.info("Mock 상품 정보 조회(billings 경로) 응답 생성 완료 - 상품코드: {}", productCode);
        log.debug("응답 내용: {}", response);

        return ResponseEntity.ok(response);
    }

    /**
     * 상품 정보를 조회합니다. (billings 경로 대응용 GET 메서드)
     *
     * @param productCode 상품 코드
     * @return 상품 정보
     */
    @GetMapping(value = "/billings/product-info", produces = MediaType.TEXT_XML_VALUE)
    @Operation(summary = "상품 정보 조회 (billings 경로 대응용 GET)", description = "상품 정보를 조회합니다.")
    public ResponseEntity<String> getProductInfoForBillingsGet(
            @RequestParam(value = "productCode", required = false) String productCode) {

        // 상품 코드가 없는 경우 기본값 설정
        if (productCode == null || productCode.isEmpty()) {
            productCode = "5GX_STANDARD";
        }

        log.info("Mock 상품 정보 조회(billings 경로 GET) 요청 - 상품코드: {}", productCode);

        // 수정된 부분: 입력 상품코드 그대로 응답으로 반환하도록 수정
        String response = mockDataGenerator.generateProductInfoResponse(productCode, productCode);
        log.info("Mock 상품 정보 조회(billings 경로 GET) 응답 생성 완료 - 상품코드: {}", productCode);
        log.debug("응답 내용: {}", response);

        return ResponseEntity.ok(response);
    }
}