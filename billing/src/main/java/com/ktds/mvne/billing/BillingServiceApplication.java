package com.ktds.mvne.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 요금조회 서비스의 애플리케이션 진입점입니다.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ktds.mvne.billing", "com.ktds.mvne.common"})
public class BillingServiceApplication {

    /**
     * 애플리케이션을 실행합니다.
     *
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }
}
