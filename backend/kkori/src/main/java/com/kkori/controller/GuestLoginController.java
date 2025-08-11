package com.kkori.controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;
import com.kkori.service.GuestLoginService;
import com.kkori.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final GuestLoginService guestLoginService;

    private static final class TokenCookie {
        static final String ACCESS_TOKEN = "accessToken";
        static final int ACCESS_EXPIRE_SECONDS = 60 * 15;
        static final String REFRESH_TOKEN = "refreshToken";
        static final int REFRESH_EXPIRE_SECONDS = 60 * 60 * 24 * 7;
    }

    @PostMapping("/guest")
    public ResponseEntity<LoginResponse> guestLogin(HttpServletResponse response) {
        log.info("게스트 로그인 요청");

        LoginResponse loginResponse = guestLoginService.createGuestUserAndLogin();
        if (loginResponse == null) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }

        Token accessToken = guestLoginService.issueAccessToken(
                loginResponse.getRefreshToken().getToken()
        );

        if (accessToken == null) {
            log.error("AccessToken 발급 실패, refreshToken: {}", loginResponse.getRefreshToken().getToken());
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        addLoginCookies(response, accessToken, loginResponse.getRefreshToken());
        log.info("게스트 로그인 성공: nickname={}", loginResponse.getNickname());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/guest/token/refresh")
    public ResponseEntity<Token> refreshGuestAccessToken(
            @CookieValue(value = TokenCookie.REFRESH_TOKEN, required = false) String refreshTokenValue,
            HttpServletResponse response
    ) {
        Token newAccessToken = guestLoginService.issueAccessTokenByValidRefreshToken(refreshTokenValue);

        if (newAccessToken == null) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        CookieUtil.addSecureJwtCookie(response, TokenCookie.ACCESS_TOKEN, newAccessToken.getToken(),
                TokenCookie.ACCESS_EXPIRE_SECONDS);

        return ResponseEntity.ok(newAccessToken);
    }

    private void addLoginCookies(HttpServletResponse response, Token accessToken, Token refreshToken) {
        CookieUtil.addSecureJwtCookie(response, TokenCookie.ACCESS_TOKEN,
                accessToken.getToken(), TokenCookie.ACCESS_EXPIRE_SECONDS);

        CookieUtil.addJwtCookie(response, TokenCookie.REFRESH_TOKEN,
                refreshToken.getToken(), TokenCookie.REFRESH_EXPIRE_SECONDS);
    }

}
