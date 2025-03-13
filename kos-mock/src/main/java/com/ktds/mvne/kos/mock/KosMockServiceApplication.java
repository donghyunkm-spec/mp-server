package com.ktds.mvne.kos.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * KOS 목업 서비스의 애플리케이션 진입점입니다.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ktds.mvne.kos.mock", "com.ktds.mvne.common"})
public class KosMockServiceApplication {

    /**
     * 애플리케이션을 실행합니다.
     *
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(KosMockServiceApplication.class, args);
    }
}
