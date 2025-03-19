// kosmock/src/main/java/com/ktds/mvne/kosmock/util/XmlUtility.java
package com.ktds.mvne.kos.mock.util;

import org.springframework.stereotype.Component;

/**
 * XML 처리를 위한 유틸리티 클래스
 */
@Component
public class XmlUtility {

    /**
     * XML 내용을 SOAP 엔벨로프로 감싸기
     * @param content SOAP 본문 내용
     * @return SOAP 엔벨로프로 감싼 XML
     */
    public String wrapInSoapEnvelope(String content) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soapenv:Header/>\n" +
                "  <soapenv:Body>\n" +
                "    " + content + "\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }
}