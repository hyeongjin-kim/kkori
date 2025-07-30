package com.kkori.util;


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
                attributes.put("userId", userId);
                return true; // 연결 허용
            }
        } catch (Exception e) {
            // 토큰 검증 실패
        }
        return false; // 연결 거부
        //return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 필요시 추가 처리
    }

    private String extractTokenFromCookie(ServerHttpRequest request) {
        // Cookie 헤더에서 직접 파싱
        List<String> cookies = request.getHeaders().get("Cookie");

        if (cookies != null && !cookies.isEmpty()) {
            String cookieHeader = cookies.get(0);
            return parseCookieValue(cookieHeader, "accessToken");
        }

        return null;
    }

    /**
     * Cookie 헤더 문자열에서 특정 쿠키 값 추출 예: "accessToken=abc123; refreshToken=def456; theme=dark"
     */
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