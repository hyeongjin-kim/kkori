package com.kkori.controller;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;
import com.kkori.service.GuestLoginService;
import com.kkori.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class GuestLoginController {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final int ACCESS_TOKEN_EXPIRE_SECONDS = 60 * 15;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int REFRESH_TOKEN_EXPIRE_SECONDS = 60 * 60 * 24 * 7;

    private final GuestLoginService guestLoginService;

    @PostMapping("/guest")
    public ResponseEntity<LoginResponse> guestLogin(HttpServletResponse response) {
        log.info("게스트 로그인 요청");

        try {
            // 게스트 사용자 생성 및 refresh token 발급
            LoginResponse loginResponse = guestLoginService.createGuestUserAndLogin();

            // access token 발급
            Token accessToken = guestLoginService.issueAccessToken(loginResponse.getRefreshToken().getToken());

            if (accessToken == null) {
                log.error("AccessToken 발급 실패, refreshToken: {}", loginResponse.getRefreshToken().getToken());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 기존 CookieUtil 재사용하여 쿠키 설정
            CookieUtil.addSecureJwtCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken.getToken(),
                    ACCESS_TOKEN_EXPIRE_SECONDS);

            CookieUtil.addJwtCookie(response, REFRESH_TOKEN_COOKIE_NAME, loginResponse.getRefreshToken().getToken(),
                    REFRESH_TOKEN_EXPIRE_SECONDS);

            log.info("게스트 로그인 성공: nickname={}", loginResponse.getNickname());
            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            log.error("게스트 로그인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/guest/token/refresh")
    public ResponseEntity<Token> refreshGuestAccessToken(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshTokenValue) {

        Token newAccessToken = guestLoginService.issueAccessTokenByValidRefreshToken(refreshTokenValue);
        
        if (newAccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(newAccessToken);
    }
}
