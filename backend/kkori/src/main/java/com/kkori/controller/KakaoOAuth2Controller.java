package com.kkori.controller;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;
import com.kkori.service.KakaoOAuth2Service;
import com.kkori.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/authorization/kakao")
public class KakaoOAuth2Controller {

    private final KakaoOAuth2Service kakaoOAuth2Service;

    @GetMapping
    public ResponseEntity<String> getKakaoAuthorizationUrl(HttpSession session) {
        String authorizationUrl = kakaoOAuth2Service.createAuthorizationUrl(session);
        return ResponseEntity.ok(authorizationUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<LoginResponse> handleKakaoCallback(
            @RequestParam("code") String code, HttpSession session, HttpServletResponse response) {

        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("인가코드가 필요합니다");
        }
        LoginResponse loginResponse = kakaoOAuth2Service.loginWithKakao(code, session);

        CookieUtil.addJwtCookie(response, "accessToken", loginResponse.getAccessToken().getToken(), 60 * 60);
        CookieUtil.addJwtCookie(response, "refreshToken", loginResponse.getRefreshToken().getToken(), 60 * 60 * 24 * 7);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<Token> refreshAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenValue) {

        Token newAccessToken = kakaoOAuth2Service.refreshAccessToken(refreshTokenValue);
        if (newAccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(newAccessToken);
    }

    @DeleteMapping("/users/withdraw")
    public ResponseEntity<Void> withdrawUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        long userId = switch (principal) {
            case User userDetails -> Long.parseLong(userDetails.getUsername());
            case String str -> Long.parseLong(str);
            case Long id -> id;
            default -> throw new IllegalArgumentException("지원하지 않는 principal 타입: " + principal.getClass());
        };

        kakaoOAuth2Service.withdrawUser(userId);
        return ResponseEntity.noContent().build();
    }

}
