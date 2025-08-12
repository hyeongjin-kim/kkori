package com.kkori.jwt;

import com.kkori.jwt.Token;
import com.kkori.service.KakaoOAuth2Service;
import com.kkori.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final KakaoOAuth2Service kakaoOAuth2Service;
    
    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int ACCESS_TOKEN_EXPIRE_SECONDS = 60 * 30; // 30분

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractJwtFromRequest(request);

            if (token == null) {
                token = extractJwtFromCookie(request);
            }

            if (token != null) {
                try {
                    // 토큰 유효성 검사
                    if (tokenProvider.validateToken(token)) {
                        Long userId = tokenProvider.getUserIdFromToken(token);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (ExpiredJwtException e) {
                    log.info("액세스 토큰이 만료됨. 자동 갱신 시도...");
                    
                    // 리프레시 토큰으로 자동 갱신 시도
                    if (!tryAutoRefreshToken(request, response)) {
                        log.warn("토큰 자동 갱신 실패. 재로그인 필요");
                        // 갱신 실패 시에는 401 응답하지 않고 그냥 진행 (인증 없는 상태로)
                        return;
                    }
                    
                    log.info("토큰 자동 갱신 성공");
                    // 갱신된 토큰으로 인증 설정
                    String newToken = extractJwtFromCookie(request);
                    if (newToken == null || !tokenProvider.validateToken(newToken)) {
                        return;
                    }
                    
                    Long userId = tokenProvider.getUserIdFromToken(newToken);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("JWT 필터에서 예외 발생: {}", e.getMessage());
            // 예외 발생 시에도 필터 체인은 계속 진행 (인증 없는 상태로)
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("Authorization 헤더: {}", bearerToken);
        
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }
        
        return bearerToken.substring(7);
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        
        for (Cookie cookie : request.getCookies()) {
            log.debug("Cookie: {}={}", cookie.getName(), cookie.getValue());
            if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
    
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        
        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
    
    /**
     * 리프레시 토큰을 사용하여 액세스 토큰 자동 갱신
     */
    private boolean tryAutoRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = extractRefreshTokenFromCookie(request);
            if (refreshToken == null) {
                log.debug("리프레시 토큰이 쿠키에 없음");
                return false;
            }
            
            // 새로운 액세스 토큰 발급
            Token newAccessToken = kakaoOAuth2Service.issueAccessTokenByValidRefreshToken(refreshToken);
            if (newAccessToken == null) {
                log.debug("리프레시 토큰으로 액세스 토큰 발급 실패");
                return false;
            }
            
            // 새로운 액세스 토큰을 쿠키에 설정
            CookieUtil.addAccessTokenJwtCookie(response, ACCESS_TOKEN_COOKIE_NAME, 
                newAccessToken.getToken(), ACCESS_TOKEN_EXPIRE_SECONDS);
            
            log.info("액세스 토큰 자동 갱신 완료");
            return true;
            
        } catch (Exception e) {
            log.error("토큰 자동 갱신 중 오류: {}", e.getMessage());
            return false;
        }
    }

}