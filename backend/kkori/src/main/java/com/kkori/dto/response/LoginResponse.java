package com.kkori.dto.response;

import com.kkori.jwt.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private Token accessToken;

    private Token refreshToken;

    private String nickname;

    public LoginResponse(Token accessToken, Token refreshToken, String nickname) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.nickname = nickname;
    }

}