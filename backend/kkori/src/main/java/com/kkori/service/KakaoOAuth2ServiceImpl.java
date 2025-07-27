package com.kkori.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2ServiceImpl implements KakaoOAuth2Service {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-url}")
    private String redirectUri;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    private static final String NONCE_KEY = "oauth2_kakao_nonce";

}
