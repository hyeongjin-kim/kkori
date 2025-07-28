package com.kkori.service;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;
import jakarta.servlet.http.HttpSession;

public interface KakaoOAuth2Service {

    String createAuthorizationUrl(HttpSession session);

    LoginResponse loginWithKakao(String authorizationCode, HttpSession session);

    Token refreshAccessToken(String refreshTokenValue);
}
