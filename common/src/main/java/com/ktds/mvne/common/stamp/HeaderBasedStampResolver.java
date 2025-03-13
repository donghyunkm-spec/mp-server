package com.ktds.mvne.common.stamp;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * HTTP 헤더에 기반하여 스탬프 ID를 결정하는 구현체입니다.
 */
@Component
@RequiredArgsConstructor
public class HeaderBasedStampResolver implements StampResolver {

    @Value("${stamp.header-name:X-MVNO-Stamp}")
    private String headerName;

    @Value("${stamp.default-stamp:default-stamp}")
    private String defaultStamp;

    /**
     * HTTP 요청 헤더에서 스탬프 ID를 추출합니다.
     * 헤더가 없는 경우 기본 스탬프 ID를 반환합니다.
     *
     * @return 스탬프 ID
     */
    @Override
    public String resolveCurrentStamp() {
        ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return defaultStamp;
        }

        HttpServletRequest request = attributes.getRequest();
        String stampId = request.getHeader(headerName);
        return stampId != null ? stampId : defaultStamp;
    }
}
