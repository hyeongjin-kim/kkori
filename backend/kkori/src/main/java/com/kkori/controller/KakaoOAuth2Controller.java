package com.kkori.controller;

import com.kkori.dto.response.LoginResponse;
import com.kkori.service.KakaoOAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/authorization/kakao")
public class KakaoOAuth2Controller {

    private final KakaoOAuth2Service kakaoOAuth2Service;

    @GetMapping("/callback")
    public ResponseEntity<LoginResponse> handleKakaoCallback(@RequestParam("code") String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("인가코드가 필요합니다");
        }

        LoginResponse authResponse = kakaoOAuth2Service.loginWithKakao(code);
        return ResponseEntity.ok(authResponse);
    }

}
