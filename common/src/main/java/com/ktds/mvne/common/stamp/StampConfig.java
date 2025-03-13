package com.ktds.mvne.common.stamp;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 스탬프 관련 빈을 구성하는 설정 클래스입니다.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "stamp.enabled", havingValue = "true", matchIfMissing = true)
public class StampConfig {

    private final StampProperties stampProperties;

    /**
     * StampResolver 빈을 생성합니다.
     *
     * @return HeaderBasedStampResolver 인스턴스
     */
    @Bean
    public StampResolver stampResolver() {
        return new HeaderBasedStampResolver();
    }

    /**
     * StampContextHolder 빈을 생성합니다.
     *
     * @return StampContextHolder 인스턴스
     */
    @Bean
    public StampContextHolder stampContextHolder() {
        return new StampContextHolder();
    }

    /**
     * StampFilter 빈을 생성합니다.
     *
     * @param stampResolver StampResolver 인스턴스
     * @param stampContextHolder StampContextHolder 인스턴스
     * @return StampFilter 인스턴스
     */
    @Bean
    public StampFilter stampFilter(StampResolver stampResolver, StampContextHolder stampContextHolder) {
        return new StampFilter(stampResolver, stampContextHolder);
    }
}
