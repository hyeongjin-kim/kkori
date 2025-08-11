package com.kkori.service;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;

public interface GuestLoginService {

    /**
     * 게스트 사용자를 생성하고 로그인 처리를 수행합니다.
     * 랜덤 닉네임을 생성하고, 게스트 전용 sub 값을 생성하여 사용자를 생성한 후
     * JWT refresh token을 발급합니다.
     * 
     * @return LoginResponse 로그인 응답 (refreshToken, nickname 포함)
     * @throws RuntimeException 사용자 생성 또는 토큰 발급 실패 시
     */
    LoginResponse createGuestUserAndLogin();

    /**
     * 유효한 refresh token을 사용하여 새로운 access token을 발급합니다.
     * 
     * @param refreshToken refresh token 값
     * @return Token access token 또는 실패 시 null
     */
    Token issueAccessToken(String refreshToken);

    /**
     * DB에 저장된 유효한 refresh token으로 access token을 발급합니다.
     * 카카오 로그인의 issueAccessTokenByValidRefreshToken과 동일한 로직입니다.
     * 
     * @param refreshTokenValue refresh token 값
     * @return Token access token 또는 실패 시 null
     */
    Token issueAccessTokenByValidRefreshToken(String refreshTokenValue);

}
