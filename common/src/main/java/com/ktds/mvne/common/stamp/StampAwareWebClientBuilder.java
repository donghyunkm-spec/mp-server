package com.ktds.mvne.common.stamp;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 현재 스탬프 컨텍스트를 HTTP 요청 헤더에 포함시키는 WebClient 빌더입니다.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "stamp.enabled", havingValue = "true", matchIfMissing = true)
public class StampAwareWebClientBuilder {

    private final StampContextHolder stampContextHolder;
    private final WebClient.Builder webClientBuilder;

    /**
     * 현재 스탬프 컨텍스트를 헤더에 포함한 WebClient를 생성합니다.
     *
     * @return 스탬프 인식 WebClient 인스턴스
     */
    public WebClient build() {
        return webClientBuilder
                .filter((request, next) -> {
                    String currentStamp = stampContextHolder.getCurrentStamp();
                    if (currentStamp != null) {
                        request.headers().add("X-MVNO-Stamp", currentStamp);
                    }
                    return next.exchange(request);
                })
                .build();
    }
}
