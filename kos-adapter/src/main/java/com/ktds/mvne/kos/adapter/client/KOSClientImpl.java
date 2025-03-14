package com.ktds.mvne.kos.adapter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

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

        try {
            // XML 요청을 쿼리 파라미터에서 추출할 주요 정보로 변환
            String phoneNumber = extractPhoneNumber(requestXml);
            String billingMonth = extractBillingMonth(requestXml);

            String response;

            // 엔드포인트에 따라 다른 HTTP 메소드 사용
            if (endpoint.equals("billing-status")) {
                URI uri = UriComponentsBuilder.fromHttpUrl(ensureHttpUrl(baseUrl) + contextPath + "billing-status")
                        .queryParam("phoneNumber", phoneNumber)
                        .build()
                        .toUri();

                response = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } else if (endpoint.equals("info")) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(ensureHttpUrl(baseUrl) + contextPath + "info")
                        .queryParam("phoneNumber", phoneNumber);

                if (billingMonth != null && !billingMonth.isEmpty()) {
                    uriBuilder.queryParam("billingMonth", billingMonth);
                }

                URI uri = uriBuilder.build().toUri();

                response = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } else {
                // 다른 엔드포인트는 POST로 처리 (change 등)
                URI uri = UriComponentsBuilder.fromHttpUrl(ensureHttpUrl(baseUrl) + fullEndpoint)
                        .build()
                        .toUri();

                response = webClient.post()
                        .uri(uri)
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

    // XML에서 전화번호 추출
    private String extractPhoneNumber(String requestXml) {
        // 간단한 구현 - 실제로는 XML 파싱이 필요할 수 있음
        if (requestXml != null && requestXml.contains("<phoneNumber>")) {
            int start = requestXml.indexOf("<phoneNumber>") + "<phoneNumber>".length();
            int end = requestXml.indexOf("</phoneNumber>");
            return requestXml.substring(start, end);
        }
        return "01012345678"; // 기본값 (실제 구현 시 제거)
    }

    // XML에서 청구 년월 추출
    private String extractBillingMonth(String requestXml) {
        // 간단한 구현 - 실제로는 XML 파싱이 필요할 수 있음
        if (requestXml != null && requestXml.contains("<billingMonth>")) {
            int start = requestXml.indexOf("<billingMonth>") + "<billingMonth>".length();
            int end = requestXml.indexOf("</billingMonth>");
            return requestXml.substring(start, end);
        }
        return null;
    }
}