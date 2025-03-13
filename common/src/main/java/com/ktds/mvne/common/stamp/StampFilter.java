package com.ktds.mvne.common.stamp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * HTTP 요청에서 스탬프 ID를 추출하여 스레드 로컬에 설정하는 필터입니다.
 */
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@ConditionalOnProperty(name = "stamp.enabled", havingValue = "true", matchIfMissing = true)
public class StampFilter extends GenericFilterBean {

    private final StampResolver stampResolver;
    private final StampContextHolder stampContextHolder;

    /**
     * 요청이 처리되기 전에 스탬프 ID를 추출하여 스레드 로컬에 설정하고,
     * 요청 처리가 완료된 후 스레드 로컬을 초기화합니다.
     *
     * @param request 서블릿 요청
     * @param response 서블릿 응답
     * @param chain 필터 체인
     * @throws IOException I/O 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String stamp = stampResolver.resolveCurrentStamp();
            log.debug("Setting stamp context: {}", stamp);
            stampContextHolder.setCurrentStamp(stamp);
            chain.doFilter(request, response);
        } finally {
            stampContextHolder.clear();
            log.debug("Cleared stamp context");
        }
    }
}
