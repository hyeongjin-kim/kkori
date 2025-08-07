package com.kkori.util;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static void addJwtCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofSeconds(maxAge))
                // 도메인 넣을 때 .domain("domain.com")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void addSecureJwtCookie(HttpServletResponse response, String name, String value, int maxAge) {
        addJwtCookie(response, name, value, maxAge);
    }

}