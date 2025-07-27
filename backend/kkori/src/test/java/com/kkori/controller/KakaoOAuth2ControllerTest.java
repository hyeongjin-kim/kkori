package com.kkori.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;
import com.kkori.service.KakaoOAuth2Service;
import jakarta.servlet.http.HttpSession;
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

    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize?client_id=test-client&nonce=testnonce";
    private static final String VALID_CODE = "valid-auth-code";
    private static final String INVALID_CODE = "invalid-auth-code";
    private static final String VALID_KAKAO_CODE = "valid-kakao-code";
    private static final String NICKNAME = "홍길동";
    private static final String ACCESS_TOKEN_VALUE = "jwt-accesstoken-sample";
    private static final String REFRESH_TOKEN_VALUE = "jwt-refreshtoken-sample";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KakaoOAuth2Service kakaoOAuth2Service;

    private final Token accessToken = new Token(ACCESS_TOKEN_VALUE);
    private final Token refreshToken = new Token(REFRESH_TOKEN_VALUE);

    @Test
    @WithMockUser
    @DisplayName("인가 URL 생성 요청 시 200 OK와 인가 URL 반환")
    void createAuthorizationUrl_ShouldReturnOkWithUrl() throws Exception {
        when(kakaoOAuth2Service.createAuthorizationUrl(any(HttpSession.class))).thenReturn(KAKAO_AUTH_URL);

        mockMvc.perform(get("/oauth2/authorization/kakao")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(KAKAO_AUTH_URL));
    }

    @Test
    @WithMockUser
    @DisplayName("인가 URL 생성 중 서비스 예외 발생 시 500 Internal Server Error 반환")
    void createAuthorizationUrl_WhenServiceThrows_ShouldReturnInternalServerError() throws Exception {
        when(kakaoOAuth2Service.createAuthorizationUrl(any(HttpSession.class)))
                .thenThrow(new RuntimeException("내부 서버 오류"));

        mockMvc.perform(get("/oauth2/authorization/kakao")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    @DisplayName("유효한 인가코드로 로그인 요청 시 200 OK와 JWT 토큰, 닉네임 반환")
    void loginWithValidAuthorizationCode_ShouldReturnTokensAndNickname() throws Exception {
        LoginResponse mockResponse = new LoginResponse(accessToken, refreshToken, NICKNAME);
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any())).willReturn(mockResponse);

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", VALID_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken.token").value(ACCESS_TOKEN_VALUE))
                .andExpect(jsonPath("$.refreshToken.token").value(REFRESH_TOKEN_VALUE))
                .andExpect(jsonPath("$.nickname").value(NICKNAME));
    }

    @Test
    @WithMockUser
    @DisplayName("인가코드 없이 로그인 요청 시 400 Bad Request 반환")
    void loginWithoutAuthorizationCode_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("잘못된 인가코드로 로그인 요청 시 400 Bad Request 반환")
    void loginWithInvalidAuthorizationCode_ShouldReturnBadRequest() throws Exception {
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any()))
                .willThrow(new IllegalArgumentException("Invalid authorization code"));

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", INVALID_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("카카오 API 서버 장애로 로그인 실패 시 500 Internal Server Error 반환")
    void loginWhenKakaoApiFails_ShouldReturnInternalServerError() throws Exception {
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any()))
                .willThrow(new RuntimeException("카카오 서버 장애"));

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", VALID_KAKAO_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}