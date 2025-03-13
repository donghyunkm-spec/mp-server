package com.ktds.mvne.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 상품변경 서비스의 애플리케이션 진입점입니다.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ktds.mvne.product", "com.ktds.mvne.common"})
public class ProductServiceApplication {

    /**
     * 애플리케이션을 실행합니다.
     *
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
