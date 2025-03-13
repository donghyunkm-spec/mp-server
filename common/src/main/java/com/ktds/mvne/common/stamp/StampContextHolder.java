package com.ktds.mvne.common.stamp;

import org.springframework.stereotype.Component;

/**
 * 현재 요청의 스탬프 컨텍스트를 관리하는 클래스입니다.
 */
@Component
public class StampContextHolder {
    private final ThreadLocal<String> stampContext = new ThreadLocal<>();

    /**
     * 현재 스레드에 설정된 스탬프 ID를 반환합니다.
     *
     * @return 스탬프 ID
     */
    public String getCurrentStamp() {
        return stampContext.get();
    }

    /**
     * 현재 스레드에 스탬프 ID를 설정합니다.
     *
     * @param stamp 스탬프 ID
     */
    public void setCurrentStamp(String stamp) {
        stampContext.set(stamp);
    }

    /**
     * 현재 스레드의 스탬프 컨텍스트를 초기화합니다.
     */
    public void clear() {
        stampContext.remove();
    }
}
