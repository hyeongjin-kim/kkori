package com.kkori.service;

import com.kkori.dto.response.LoginResponse;

public interface KakaoOAuth2Service {

    LoginResponse loginWithKakao(String authorizationCode);

}
