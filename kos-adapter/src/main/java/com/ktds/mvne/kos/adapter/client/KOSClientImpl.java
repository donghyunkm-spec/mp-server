// File: mp-server\kos-adapter\src\main\java\com\ktds\mvne\kos\adapter\client\KOSClientImpl.java
package com.ktds.mvne.kos.adapter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * KT 영업시스템 클라이언트 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KOSClientImpl implements KOSClient {
    private final WebClient webClient;

    @Value("${kos-mock.base-url}")
    private String mockBaseUrl;

    @Value("${kos-real.base-url}")
    private String realBaseUrl;

    @Value("${kos-real.use-real:false}")
    private boolean useRealKos;

    @Override
    public String sendRequest(String requestXml, String endpoint) {
        String baseUrl = useRealKos ? realBaseUrl : mockBaseUrl;
        String contextPath = useRealKos ? "/real/billings/" : "/mock/billings/";
        String fullEndpoint = contextPath + endpoint;

        log.debug("Sending request to KOS: {}{}", baseUrl, fullEndpoint);
        log.trace("Request XML: {}", requestXml);

        try {
            // XML 요청을 쿼리 파라미터에서 추출할 주요 정보로 변환
            String phoneNumber = extractPhoneNumber(requestXml);
            String billingMonth = extractBillingMonth(requestXml);
            String productCode = extractProductCode(requestXml);
            String changeReason = extractChangeReason(requestXml);

            String response;

            // 엔드포인트에 따라 다른 HTTP 메소드 사용
            if (endpoint.equals("billing-status")) {
                URI uri = UriComponentsBuilder.fromHttpUrl(ensureHttpUrl(baseUrl) + contextPath + "billing-status")
                        .queryParam("phoneNumber", phoneNumber)
                        .build()
                        .toUri();

                log.debug("Sending GET request to: {}", uri);
                response = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> log.error("Error during GET request: {}", e.getMessage(), e))
                        .block();
            } else if (endpoint.equals("info")) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(ensureHttpUrl(baseUrl) + contextPath + "info")
                        .queryParam("phoneNumber", phoneNumber);

                if (billingMonth != null && !billingMonth.isEmpty()) {
                    uriBuilder.queryParam("billingMonth", billingMonth);
                }

                URI uri = uriBuilder.build().toUri();

                log.debug("Sending GET request to: {}", uri);
                response = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> log.error("Error during GET request: {}", e.getMessage(), e))
                        .block();
            } else if (endpoint.equals("customer-info")) {
                URI uri = UriComponentsBuilder.fromHttpUrl(ensureHttpUrl(baseUrl) + contextPath + "customer-info")
                        .queryParam("phoneNumber", phoneNumber)
                        .build()
                        .toUri();

                log.debug("Sending GET request to: {}", uri);
                response = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> log.error("Error during GET request: {}", e.getMessage(), e))
                        .block();
            } else if (endpoint.equals("product-info")) {
                URI uri = UriComponentsBuilder.fromHttpUrl(ensureHttpUrl(baseUrl) + contextPath + "product-info")
                        .queryParam("productCode", productCode)
                        .build()
                        .toUri();

                log.debug("Sending GET request to: {}", uri);
                response = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> log.error("Error during GET request: {}", e.getMessage(), e))
                        .block();
            } else if (endpoint.equals("change")) {
                // 상품 변경 요청의 경우 쿼리 파라미터 대신 Body만 사용
                URI uri = UriComponentsBuilder.fromHttpUrl(ensureHttpUrl(baseUrl) + contextPath + "change")
                        .build()
                        .toUri();

                log.debug("Sending POST request to: {}", uri);
                response = webClient.post()
                        .uri(uri)
                        .contentType(MediaType.APPLICATION_XML)
                        .bodyValue(requestXml)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> log.error("Error during POST request: {}", e.getMessage(), e))
                        .block();
            } else {
                // 다른 엔드포인트는 POST로 처리
                URI uri = UriComponentsBuilder.fromHttpUrl(ensureHttpUrl(baseUrl) + fullEndpoint)
                        .build()
                        .toUri();

                log.debug("Sending POST request to: {}", uri);
                response = webClient.post()
                        .uri(uri)
                        .contentType(MediaType.APPLICATION_XML)
                        .bodyValue(requestXml)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> log.error("Error during POST request: {}", e.getMessage(), e))
                        .block();
            }

            log.debug("Received response from KOS: length={}", response != null ? response.length() : 0);
            log.trace("Response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error sending request to KOS: {}", e.getMessage(), e);
            throw new RuntimeException("KT 영업시스템 요청 실패: " + e.getMessage(), e);
        }
    }

    /**
     * URL이 http:// 또는 https://로 시작하는지 확인하고,
     * 그렇지 않으면 http://를 앞에 추가합니다.
     *
     * @param url 확인할 URL
     * @return 프로토콜을 포함한 URL
     */
    private String ensureHttpUrl(String url) {
        if (url == null || url.isEmpty()) {
            // 기본값 설정
            return "http://localhost:8084";
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }

        return url;
    }

    // XML에서 전화번호 추출 - 정규식 사용
    private String extractPhoneNumber(String requestXml) {
        if (requestXml == null) {
            return "01012345678"; // 기본값
        }

        Pattern pattern = Pattern.compile("<phoneNumber>(\\d+)</phoneNumber>");
        Matcher matcher = pattern.matcher(requestXml);

        if (matcher.find()) {
            return matcher.group(1);
        }

        log.warn("Failed to extract phone number from XML, using default");
        return "01012345678"; // 기본값
    }

    // XML에서 청구 년월 추출 - 정규식 사용
    private String extractBillingMonth(String requestXml) {
        if (requestXml == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("<billingMonth>(\\d+)</billingMonth>");
        Matcher matcher = pattern.matcher(requestXml);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    // XML에서 상품 코드 추출 - 정규식 사용
    private String extractProductCode(String requestXml) {
        if (requestXml == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("<productCode>([^<]+)</productCode>");
        Matcher matcher = pattern.matcher(requestXml);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    // XML에서 변경 사유 추출 - 정규식 사용
    private String extractChangeReason(String requestXml) {
        if (requestXml == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("<changeReason>([^<]+)</changeReason>");
        Matcher matcher = pattern.matcher(requestXml);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}