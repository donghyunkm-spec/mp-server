package com.ktds.mvne.common.stamp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 스탬프 관련 설정 속성 클래스입니다.
 */
@Component
@ConfigurationProperties(prefix = "stamp")
@Getter
@Setter
public class StampProperties {
    private boolean enabled = true;
    private String headerName = "X-MVNO-Stamp";
    private String defaultStamp = "default-stamp";
}
