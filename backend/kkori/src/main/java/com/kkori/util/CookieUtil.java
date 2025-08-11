package com.kkori.util;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static void addRefreshTokenJwtCookie(HttpServletResponse response, String name, String value, int maxAge) {
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

    public static void addAccessTokenJwtCookie(HttpServletResponse response, String name, String value, int maxAge) {
        addRefreshTokenJwtCookie(response, name, value, maxAge);
    }

}