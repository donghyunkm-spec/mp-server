package com.ktds.mvne.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 보안 관련 유틸리티 클래스입니다.
 */
public class SecurityUtil {

    /**
     * 현재 인증된 사용자의 username을 반환합니다.
     *
     * @return 인증된 사용자의 username 또는 익명 사용자인 경우 "anonymousUser"
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymousUser";
        }
        return authentication.getName();
    }

    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     *
     * @return 인증된 사용자인 경우 true, 그렇지 않으면 false
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 현재 사용자가 특정 역할을 가지고 있는지 확인합니다.
     *
     * @param role 확인할 역할
     * @return 해당 역할을 가진 경우 true, 그렇지 않으면 false
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
