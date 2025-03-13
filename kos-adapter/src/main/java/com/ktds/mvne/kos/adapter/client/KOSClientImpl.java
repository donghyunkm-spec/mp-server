package com.ktds.mvne.kos.adapter.client;

import com.ktds.mvne.common.exception.ExternalSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * KT 영업시스템과의 통신을 담당하는 클라이언트 구현체입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KOSClientImpl implements KOSClient {

    private final WebClient webClient;

    @Value("${kos-mock.base-url}")
    private String kosMockBaseUrl;

    @Value("${kos-real.base-url}")
    private String kosRealBaseUrl;

    @Value("${kos-real.use-real:false}")
    private boolean useRealKos;

    /**
     * KT 영업시스템에 SOAP 요청을 보내고 응답을 받습니다.
     *
     * @param requestXml SOAP 요청 XML
     * @param endpoint 호출할 엔드포인트
     * @return SOAP 응답 XML
     */
    @Override
    public String sendRequest(String requestXml, String endpoint) {
        String baseUrl = getBaseUrl();
        String url = baseUrl + "/" + endpoint;
        
        log.debug("Sending request to KOS: {}", url);
        log.trace("Request XML: {}", requestXml);
        
        try {
            String response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.TEXT_XML)
                    .bodyValue(requestXml)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.trace("Response XML: {}", response);
            return response;
        } catch (WebClientResponseException e) {
            log.error("KOS request failed with status {}: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new ExternalSystemException("KT 영업시스템 요청 실패: " + e.getMessage(), 
                    e.getStatusCode().value(), "KOS");
        } catch (Exception e) {
            log.error("KOS request failed: {}", e.getMessage(), e);
            throw new ExternalSystemException("KT 영업시스템 요청 실패: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "KOS");
        }
    }

    /**
     * 현재 설정에 따라 사용할 기본 URL을 결정합니다.
     *
     * @return 사용할 기본 URL
     */
    private String getBaseUrl() {
        if (useRealKos) {
            log.debug("Using real KOS system");
            return kosRealBaseUrl;
        } else {
            log.debug("Using mock KOS system");
            return kosMockBaseUrl;
        }
    }
}
