package com.kkori.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String COOKIE_NAME = "accessToken";

    private static final String VALID_HEADER_TOKEN = "valid.jwt.token";
    private static final String INVALID_HEADER_TOKEN = "invalid.jwt.token";
    private static final String VALID_COOKIE_TOKEN = "valid.cookie.jwt.token";
    private static final String INVALID_COOKIE_TOKEN = "invalid.cookie.jwt.token";
    private static final String HEADER_TOKEN = "header.jwt.token";
    private static final String COOKIE_TOKEN = "cookie.jwt.token";

    private TokenProvider tokenProvider;
    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        tokenProvider = mock(TokenProvider.class);
        filter = new JwtAuthenticationFilter(tokenProvider);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void givenValidTokenInHeader_whenDoFilter_thenAuthenticationSet() throws ServletException, IOException {
        addAuthorizationHeader(VALID_HEADER_TOKEN);
        mockValidToken(VALID_HEADER_TOKEN, 1L);

        filter.doFilterInternal(request, response, filterChain);

        assertAuthenticationPrincipalEquals(1L);
        verifyFilterChainCalled();
    }

    @Test
    void givenInvalidTokenInHeader_whenDoFilter_thenUnauthorizedResponse() throws ServletException, IOException {
        addAuthorizationHeader(INVALID_HEADER_TOKEN);
        mockInvalidToken(INVALID_HEADER_TOKEN);

        filter.doFilterInternal(request, response, filterChain);

        assertUnauthorizedResponse();
        verifyFilterChainNeverCalled();
    }

    @Test
    void givenNoToken_whenDoFilter_thenProceedWithoutAuthentication() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);

        assertNull(getContext().getAuthentication());
        verifyFilterChainCalled();
    }

    @Test
    void givenTokensInHeaderAndCookie_whenDoFilter_thenHeaderTokenUsed() throws ServletException, IOException {
        addAuthorizationHeader(HEADER_TOKEN);
        addCookieToken(COOKIE_TOKEN);

        mockValidToken(HEADER_TOKEN, 10L);

        filter.doFilterInternal(request, response, filterChain);

        assertAuthenticationPrincipalEquals(10L);
        verifyFilterChainCalled();
    }

    @Test
    void givenInvalidTokenInCookie_whenDoFilter_thenUnauthorizedResponse() throws ServletException, IOException {
        addCookieToken(INVALID_COOKIE_TOKEN);
        mockInvalidToken(INVALID_COOKIE_TOKEN);

        filter.doFilterInternal(request, response, filterChain);

        assertUnauthorizedResponse();
        verifyFilterChainNeverCalled();
    }

    @Test
    void givenValidTokenInCookie_whenDoFilter_thenAuthenticationSet() throws ServletException, IOException {
        addCookieToken(VALID_COOKIE_TOKEN);
        mockValidToken(VALID_COOKIE_TOKEN, 2L);

        filter.doFilterInternal(request, response, filterChain);

        assertAuthenticationPrincipalEquals(2L);
        verifyFilterChainCalled();
    }

    private void addAuthorizationHeader(String token) {
        request.addHeader(AUTH_HEADER, BEARER_PREFIX + token);
    }

    private void addCookieToken(String token) {
        request.setCookies(new Cookie(COOKIE_NAME, token));
    }

    private void mockValidToken(String token, Long userId) {
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUserIdFromToken(token)).thenReturn(userId);
    }

    private void mockInvalidToken(String token) {
        when(tokenProvider.validateToken(token)).thenThrow(new RuntimeException("Invalid token"));
    }

    private void assertAuthenticationPrincipalEquals(Long expectedUserId) {
        assertNotNull(getContext().getAuthentication(), "Authentication should be set");
        assertEquals(expectedUserId, getContext().getAuthentication().getPrincipal());
    }

    private void assertUnauthorizedResponse() throws UnsupportedEncodingException {
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Unauthorized"));
    }

    private void verifyFilterChainCalled() throws ServletException, IOException {
        verify(filterChain).doFilter(request, response);
    }

    private void verifyFilterChainNeverCalled() throws ServletException, IOException {
        verify(filterChain, never()).doFilter(request, response);
    }

}
