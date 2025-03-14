package com.ktds.mvne.kos.adapter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
        String fullEndpoint = "/mock/billings/" + endpoint;

        if (useRealKos) {
            fullEndpoint = "/real/billings/" + endpoint;
        }

        log.debug("Sending request to KOS: {}{}", baseUrl, fullEndpoint);

        try {
            // XML 요청을 쿼리 파라미터에서 추출할 주요 정보로 변환
            String phoneNumber = extractPhoneNumber(requestXml);
            String billingMonth = extractBillingMonth(requestXml);

            String response;

            // 엔드포인트에 따라 다른 HTTP 메소드 사용
            if (endpoint.equals("billing-status")) {
                response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("http")
                                .host(baseUrl.replace("http://", "").replace("https://", ""))
                                .path("/mock/billings/billing-status")
                                .queryParam("phoneNumber", phoneNumber)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } else if (endpoint.equals("info")) {
                WebClient.RequestHeadersUriSpec<?> requestSpec = webClient.get();
                response = requestSpec
                        .uri(uriBuilder -> {
                            uriBuilder
                                    .scheme("http")
                                    .host(baseUrl.replace("http://", "").replace("https://", ""))
                                    .path("/mock/billings/info")
                                    .queryParam("phoneNumber", phoneNumber);

                            if (billingMonth != null && !billingMonth.isEmpty()) {
                                uriBuilder.queryParam("billingMonth", billingMonth);
                            }

                            return uriBuilder.build();
                        })
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } else {
                // 다른 엔드포인트는 POST로 처리 (change 등)
                response = webClient.post()
                        .uri(baseUrl + fullEndpoint)
                        .contentType(MediaType.APPLICATION_XML)
                        .bodyValue(requestXml)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            }

            log.debug("Received response from KOS: length={}", response != null ? response.length() : 0);
            return response;
        } catch (Exception e) {
            log.error("Error sending request to KOS: {}", e.getMessage(), e);
            throw new RuntimeException("KT 영업시스템 요청 실패: " + e.getMessage(), e);
        }
    }

    // XML에서 전화번호 추출
    private String extractPhoneNumber(String requestXml) {
        // 간단한 구현 - 실제로는 XML 파싱이 필요할 수 있음
        if (requestXml.contains("<phoneNumber>")) {
            int start = requestXml.indexOf("<phoneNumber>") + "<phoneNumber>".length();
            int end = requestXml.indexOf("</phoneNumber>");
            return requestXml.substring(start, end);
        }
        return "01012345678"; // 기본값 (실제 구현 시 제거)
    }

    // XML에서 청구 년월 추출
    private String extractBillingMonth(String requestXml) {
        // 간단한 구현 - 실제로는 XML 파싱이 필요할 수 있음
        if (requestXml.contains("<billingMonth>")) {
            int start = requestXml.indexOf("<billingMonth>") + "<billingMonth>".length();
            int end = requestXml.indexOf("</billingMonth>");
            return requestXml.substring(start, end);
        }
        return null;
    }
}