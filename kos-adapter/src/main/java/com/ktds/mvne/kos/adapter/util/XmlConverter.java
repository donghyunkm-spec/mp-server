package com.ktds.mvne.kos.adapter.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ktds.mvne.common.exception.BizException;
import com.ktds.mvne.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * JSON과 SOAP XML 간의 변환을 담당하는 유틸리티 클래스입니다.
 */
@Component
@Slf4j
public class XmlConverter {

    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    /**
     * XmlConverter 생성자입니다.
     */
    public XmlConverter() {
        this.jsonMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.xmlMapper = (XmlMapper) new XmlMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * JSON 객체를 SOAP XML로 변환합니다.
     *
     * @param jsonRequest JSON 요청 객체
     * @return SOAP XML 문자열
     */
    public String convertToSoapXml(Object jsonRequest) {
        try {
            // 1. JSON 객체를 XML로 변환
            String xmlBody = xmlMapper.writeValueAsString(jsonRequest);

            // 2. SOAP 엔벨로프로 감싸기
            return createSoapEnvelope(xmlBody);
        } catch (Exception e) {
            log.error("Failed to convert JSON to SOAP XML: {}", e.getMessage(), e);
            throw new BizException(ErrorCode.INTERNAL_SERVER_ERROR, "XML 변환 실패: " + e.getMessage());
        }
    }

    /**
     * SOAP XML을 JSON 객체로 변환합니다.
     *
     * @param soapXml SOAP XML 문자열
     * @param responseType 응답 객체 타입
     * @param <T> 응답 객체 타입
     * @return JSON 객체
     */
    public <T> T convertToJson(String soapXml, Class<T> responseType) {
        try {
            // 1. SOAP 엔벨로프에서 바디 추출
            String xmlBody = extractSoapBody(soapXml);
            log.debug("Extracted XML body: {}", xmlBody);

            // 2. XML을 JSON 객체로 변환
            T result = xmlMapper.readValue(xmlBody, responseType);
            log.debug("Converted object: {}", result);

            return result;
        } catch (Exception e) {
            log.error("Failed to convert SOAP XML to JSON: {}", e.getMessage(), e);
            throw new BizException(ErrorCode.INTERNAL_SERVER_ERROR, "XML 파싱 실패: " + e.getMessage());
        }
    }

    /**
     * XML 바디를 SOAP 엔벨로프로 감쌉니다.
     *
     * @param bodyContent XML 바디 내용
     * @return SOAP 엔벨로프가 포함된 XML 문자열
     */
    private String createSoapEnvelope(String bodyContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // 루트 엘리먼트 생성
        Element envelope = document.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soap:Envelope");
        document.appendChild(envelope);

        // 네임스페이스 속성 추가
        envelope.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

        // 바디 엘리먼트 생성
        Element body = document.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soap:Body");
        envelope.appendChild(body);

        // 바디 내용 파싱 및 추가
        Document bodyDoc = builder.parse(new InputSource(new StringReader("<root>" + bodyContent + "</root>")));
        NodeList children = bodyDoc.getDocumentElement().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node importedNode = document.importNode(children.item(i), true);
            body.appendChild(importedNode);
        }

        // XML을 문자열로 변환
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * SOAP 엔벨로프에서 바디 내용을 추출합니다.
     *
     * @param soapXml SOAP XML 문자열
     * @return 바디 내용
     */
    private String extractSoapBody(String soapXml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(soapXml)));

        // SOAP 바디 엘리먼트 찾기
        NodeList bodyList = document.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
        if (bodyList.getLength() == 0) {
            bodyList = document.getElementsByTagName("Body");
        }

        if (bodyList.getLength() == 0) {
            throw new BizException(ErrorCode.INTERNAL_SERVER_ERROR, "SOAP Body를 찾을 수 없습니다.");
        }

        // 바디의 첫 번째 자식 엘리먼트 추출 (응답 객체)
        Element body = (Element) bodyList.item(0);
        NodeList children = body.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                // 응답 엘리먼트를 XML로 직렬화
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(child), new StreamResult(writer));
                return writer.toString();
            }
        }

        throw new BizException(ErrorCode.INTERNAL_SERVER_ERROR, "SOAP Body의 응답 엘리먼트를 찾을 수 없습니다.");
    }
}