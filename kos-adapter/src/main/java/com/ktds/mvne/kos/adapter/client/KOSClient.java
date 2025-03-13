package com.ktds.mvne.kos.adapter.client;

/**
 * KT 영업시스템과의 통신을 담당하는 클라이언트 인터페이스입니다.
 */
public interface KOSClient {

    /**
     * KT 영업시스템에 SOAP 요청을 보내고 응답을 받습니다.
     *
     * @param requestXml SOAP 요청 XML
     * @param endpoint 호출할 엔드포인트
     * @return SOAP 응답 XML
     */
    String sendRequest(String requestXml, String endpoint);
}
