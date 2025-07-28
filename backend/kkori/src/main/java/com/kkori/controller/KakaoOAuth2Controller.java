package com.kkori.controller;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;
import com.kkori.service.KakaoOAuth2Service;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
            @RequestParam("code") String code, HttpSession session) {

        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("인가코드가 필요합니다");
        }

        LoginResponse response = kakaoOAuth2Service.loginWithKakao(code, session);
        return ResponseEntity.ok(response);
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

}
