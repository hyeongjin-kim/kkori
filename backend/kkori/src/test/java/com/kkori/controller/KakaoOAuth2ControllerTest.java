package com.kkori.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;
import com.kkori.service.KakaoOAuth2Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(KakaoOAuth2Controller.class)
class KakaoOAuth2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KakaoOAuth2Service kakaoOAuth2Service;

    Token accessToken = new Token("jwt-accesstoken-sample");
    Token refreshToken = new Token("jwt-refreshtoken-sample");

    @Test
    @WithMockUser
    @DisplayName("카카오 인증 서버에서 받은 인가코드로 로그인 성공 시 200 OK와 JWT 토큰, 닉네임 반환")
    void shouldReturnAuthResponse_whenValidAuthorizationCodeProvided() throws Exception {
        String validCode = "valid-auth-code";
        LoginResponse mockResponse = new LoginResponse(accessToken, refreshToken, "홍길동");
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any())).willReturn(mockResponse);

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", validCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken.token").value("jwt-accesstoken-sample"))
                .andExpect(jsonPath("$.refreshToken.token").value("jwt-refreshtoken-sample"))
                .andExpect(jsonPath("$.nickname").value("홍길동"));
    }

    @Test
    @WithMockUser
    @DisplayName("인가코드 없이 요청하면 400 Bad Request 반환")
    void shouldReturnBadRequest_whenAuthorizationCodeIsMissing() throws Exception {

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("잘못된 인가코드로 요청 시 400 Bad Request 반환")
    void shouldReturnBadRequest_whenInvalidAuthorizationCode() throws Exception {

        String invalidCode = "invalid-auth-code";
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any()))
                .willThrow(new IllegalArgumentException("Invalid authorization code"));

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", invalidCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("카카오 API 서버 오류 발생 시 500 Internal Server Error가 반환된다")
    void shouldReturnInternalServerError_whenKakaoApiFails() throws Exception {

        String code = "valid-kakao-code";
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any()))
                .willThrow(new RuntimeException("카카오 서버 장애"));

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", code)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}