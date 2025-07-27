package com.kkori.service;

import com.kkori.dto.response.LoginResponse;
import jakarta.servlet.http.HttpSession;

public interface KakaoOAuth2Service {

    LoginResponse loginWithKakao(String authorizationCode, HttpSession session);

}
