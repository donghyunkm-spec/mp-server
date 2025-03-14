package com.ktds.mvne.kos.adapter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * KT 영업시스템 클라이언트 인터페이스
 */
public interface KOSClient {
    /**
     * KT 영업시스템에 SOAP 요청을 전송합니다.
     *
     * @param requestXml SOAP XML 요청 문자열
     * @param endpoint 요청할 엔드포인트
     * @return SOAP XML 응답 문자열
     */
    String sendRequest(String requestXml, String endpoint);
}

