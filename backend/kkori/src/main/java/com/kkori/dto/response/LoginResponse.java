package com.kkori.dto.response;

import com.kkori.jwt.Token;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Token accessToken;

    private Token refreshToken;

    private String nickname;

}