package com.kkori.interceptor;

import com.kkori.jwt.TokenProvider;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            String token = extractTokenFromCookie(request);

            if (token != null && tokenProvider.validateToken(token)) {
                Long userId = tokenProvider.getUserIdFromToken(token);

                if (userId != null) {
                    // 세션에 사용자 정보 저장
                    attributes.put("userId", userId);
                    attributes.put("connectedAt", System.currentTimeMillis());

                    return true; // 연결 허용
                }
            }

        } catch (Exception e) {
            // 인증 실패 시 연결 거부
        }

        return false; // 연결 거부
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractTokenFromCookie(ServerHttpRequest request) {
        List<String> cookies = request.getHeaders().get("Cookie");

        if (cookies != null && !cookies.isEmpty()) {
            // 모든 Cookie 헤더를 확인
            for (String cookieHeader : cookies) {
                String token = parseCookieValue(cookieHeader, "accessToken");
                if (token != null) {
                    return token;
                }
            }
        }

        return null;
    }

    private String parseCookieValue(String cookieHeader, String cookieName) {
        if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
            return null;
        }

        String[] cookies = cookieHeader.split(";");

        for (String cookie : cookies) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2 && cookieName.equals(parts[0].trim())) {
                return parts[1].trim();
            }
        }

        return null;
    }
}