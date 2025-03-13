package com.ktds.mvne.common.util;

import java.util.regex.Pattern;

/**
 * 유효성 검사를 위한 유틸리티 클래스입니다.
 */
public class ValidationUtil {

    private static final Pattern PHONE_NUMBER_PATTERN = 
            Pattern.compile("^01[016789][0-9]{7,8}$");
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern PRODUCT_CODE_PATTERN = 
            Pattern.compile("^[A-Z0-9]{5,10}$");

    /**
     * 전화번호의 유효성을 검사합니다.
     *
     * @param phoneNumber 검사할 전화번호
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    /**
     * 이메일 주소의 유효성을 검사합니다.
     *
     * @param email 검사할 이메일 주소
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public static boolean validateEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 상품 코드의 유효성을 검사합니다.
     *
     * @param productCode 검사할 상품 코드
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public static boolean validateProductCode(String productCode) {
        if (productCode == null) {
            return false;
        }
        return PRODUCT_CODE_PATTERN.matcher(productCode).matches();
    }
}
