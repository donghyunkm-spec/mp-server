package com.ktds.mvne.common.stamp;

/**
 * 현재 스탬프 ID를 결정하는 인터페이스입니다.
 */
public interface StampResolver {

    /**
     * 현재 컨텍스트에 적용될 스탬프 ID를 반환합니다.
     *
     * @return 스탬프 ID
     */
    String resolveCurrentStamp();
}
