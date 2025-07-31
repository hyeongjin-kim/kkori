package com.kkori.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.kkori.jwt.TokenProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocket 인증 인터셉터 테스트")
class WebSocketAuthInterceptorTest {

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebSocketHandler wsHandler;

    @Mock
    private HttpHeaders httpHeaders;

    @InjectMocks
    private WebSocketAuthInterceptor interceptor;

    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
        given(request.getHeaders()).willReturn(httpHeaders);
    }

    @Test
    @DisplayName("유효한 JWT 토큰이 있을 때 인증 성공")
    void authenticateWithValidToken() {
        // given
        String validToken = "valid.jwt.token";
        Long userId = 1L;
        String cookieHeader = "accessToken=" + validToken + "; other=value";

        given(httpHeaders.get("Cookie")).willReturn(List.of(cookieHeader));
        given(tokenProvider.validateToken(validToken)).willReturn(true);
        given(tokenProvider.getUserIdFromToken(validToken)).willReturn(userId);

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isTrue();
        assertThat(attributes.get("userId")).isEqualTo(userId);
        assertThat(attributes.get("connectedAt")).isNotNull();

        verify(tokenProvider).validateToken(validToken);
        verify(tokenProvider).getUserIdFromToken(validToken);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰일 때 인증 실패")
    void authenticateWithInvalidToken() {
        // given
        String invalidToken = "invalid.jwt.token";
        String cookieHeader = "accessToken=" + invalidToken;

        given(httpHeaders.get("Cookie")).willReturn(List.of(cookieHeader));
        given(tokenProvider.validateToken(invalidToken)).willReturn(false);

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isFalse();
        assertThat(attributes.get("userId")).isNull();

        verify(tokenProvider).validateToken(invalidToken);
    }

    @Test
    @DisplayName("JWT 토큰 검증 중 예외 발생 시 인증 실패")
    void authenticateWithTokenValidationException() {
        // given
        String token = "exception.causing.token";
        String cookieHeader = "accessToken=" + token;

        given(httpHeaders.get("Cookie")).willReturn(List.of(cookieHeader));
        given(tokenProvider.validateToken(token)).willThrow(new RuntimeException("Token validation failed"));

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isFalse();
        assertThat(attributes.get("userId")).isNull();
    }

    @Test
    @DisplayName("쿠키가 없을 때 인증 실패")
    void authenticateWithoutCookie() {
        // given
        given(httpHeaders.get("Cookie")).willReturn(null);

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isFalse();
        assertThat(attributes.get("userId")).isNull();
    }

    @Test
    @DisplayName("accessToken 쿠키가 없을 때 인증 실패")
    void authenticateWithoutAccessTokenCookie() {
        // given
        String cookieHeader = "other=value; session=abc123";
        given(httpHeaders.get("Cookie")).willReturn(List.of(cookieHeader));

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isFalse();
        assertThat(attributes.get("userId")).isNull();
    }

    @Test
    @DisplayName("빈 accessToken 쿠키일 때 인증 실패")
    void authenticateWithEmptyAccessToken() {
        // given
        String cookieHeader = "accessToken=; other=value";
        given(httpHeaders.get("Cookie")).willReturn(List.of(cookieHeader));

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isFalse();
        assertThat(attributes.get("userId")).isNull();
    }

    @Test
    @DisplayName("여러 쿠키 중에서 accessToken 정확히 추출")
    void extractAccessTokenFromMultipleCookies() {
        // given
        String validToken = "correct.jwt.token";
        Long userId = 2L;
        String cookieHeader = "session=abc123; accessToken=" + validToken + "; theme=dark";

        given(httpHeaders.get("Cookie")).willReturn(List.of(cookieHeader));
        given(tokenProvider.validateToken(validToken)).willReturn(true);
        given(tokenProvider.getUserIdFromToken(validToken)).willReturn(userId);

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isTrue();
        assertThat(attributes.get("userId")).isEqualTo(userId);

        verify(tokenProvider).validateToken(validToken);
        verify(tokenProvider).getUserIdFromToken(validToken);
    }

    @Test
    @DisplayName("공백이 포함된 쿠키에서도 토큰 정확히 추출")
    void extractTokenFromCookieWithSpaces() {
        // given
        String validToken = "spaced.jwt.token";
        Long userId = 3L;
        String cookieHeader = " accessToken = " + validToken + " ; other = value ";

        given(httpHeaders.get("Cookie")).willReturn(List.of(cookieHeader));
        given(tokenProvider.validateToken(validToken)).willReturn(true);
        given(tokenProvider.getUserIdFromToken(validToken)).willReturn(userId);

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isTrue();
        assertThat(attributes.get("userId")).isEqualTo(userId);

        verify(tokenProvider).validateToken(validToken);
    }

    @Test
    @DisplayName("connectedAt 타임스탬프가 현재 시간과 근사한지 확인")
    void verifyConnectedAtTimestamp() {
        // given
        String validToken = "time.test.token";
        Long userId = 1L;
        String cookieHeader = "accessToken=" + validToken;
        long beforeTime = System.currentTimeMillis();

        given(httpHeaders.get("Cookie")).willReturn(List.of(cookieHeader));
        given(tokenProvider.validateToken(validToken)).willReturn(true);
        given(tokenProvider.getUserIdFromToken(validToken)).willReturn(userId);

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);
        long afterTime = System.currentTimeMillis();

        // then
        assertThat(result).isTrue();
        Long connectedAt = (Long) attributes.get("connectedAt");
        assertThat(connectedAt).isBetween(beforeTime, afterTime);
    }
}