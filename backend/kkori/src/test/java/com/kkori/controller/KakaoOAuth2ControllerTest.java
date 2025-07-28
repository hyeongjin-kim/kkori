package com.kkori.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.repository.RefreshTokenRepository;
import com.kkori.repository.UserRepository;
import com.kkori.service.KakaoOAuth2Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
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

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

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

    @Test
    @WithMockUser
    @DisplayName("신규 사용자면 DB 저장 이후 로그인 응답 성공")
    void loginCallback_NewUser_Success() throws Exception {
        LoginResponse response = new LoginResponse(accessToken, refreshToken, "새사용자");
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any(HttpSession.class))).willReturn(response);

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", "new-user-code")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("새사용자"));
    }

    @Test
    @WithMockUser
    @DisplayName("기존 사용자면 즉시 로그인 응답 성공")
    void loginCallback_ExistingUser_Success() throws Exception {
        LoginResponse response = new LoginResponse(accessToken, refreshToken, "기존사용자");
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any(HttpSession.class))).willReturn(response);

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", "existing-user-code")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("기존사용자"));
    }

    @Test
    @WithMockUser
    @DisplayName("DB 저장 실패 시 500 에러 반환")
    void loginCallback_SaveFailure_ShouldReturnInternalServerError() throws Exception {
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any(HttpSession.class)))
                .willThrow(new RuntimeException("DB 저장 실패"));

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", "fail-code")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("DB 저장 실패")));
    }

    @Test
    @WithMockUser
    @DisplayName("리프레시 토큰 유효 시 액세스 토큰 재발급 성공")
    public void refreshAccessToken_ValidRefreshToken_Success() throws Exception {
        String validRefreshToken = "valid-refresh-token";
        when(kakaoOAuth2Service.refreshAccessToken(validRefreshToken))
                .thenReturn(new Token("new-access-token"));

        mockMvc.perform(post("/oauth2/authorization/kakao/token/refresh")
                        .cookie(new Cookie("refreshToken", validRefreshToken))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-access-token"));
    }

    @Test
    @WithMockUser
    @DisplayName("리프레시 토큰 없을 때 401 반환")
    void refreshAccessToken_WithoutRefreshToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/oauth2/authorization/kakao/token/refresh")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("리프레시 토큰 위변조/만료 시 401 반환")
    void refreshAccessToken_InvalidRefreshToken_ShouldReturnUnauthorized() throws Exception {
        given(tokenProvider.validateToken(anyString())).willReturn(false);

        mockMvc.perform(post("/oauth2/authorization/kakao/token/refresh")
                        .cookie(new Cookie("refreshToken", "invalid-refresh-token"))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("리프레시 토큰 유효하지만 DB에 유저 없을 시 401 반환")
    void refreshAccessToken_UserNotFound_ShouldReturnUnauthorized() throws Exception {
        given(tokenProvider.validateToken(anyString())).willReturn(true);
        given(tokenProvider.getUserIdFromToken(anyString())).willReturn(123L);
        given(userRepository.findById(123L)).willReturn(Optional.empty());

        mockMvc.perform(post("/oauth2/authorization/kakao/token/refresh")
                        .cookie(new Cookie("refreshToken", "valid-refresh-token"))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("유효한 인가코드로 로그인 요청 시 쿠키에 JWT 토큰이 포함되어 반환")
    void loginWithValidCode_ShouldSetCookies() throws Exception {
        LoginResponse mockResponse = new LoginResponse(accessToken, refreshToken, NICKNAME);
        given(kakaoOAuth2Service.loginWithKakao(anyString(), any()))
                .willReturn(mockResponse);

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", VALID_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(NICKNAME))
                .andExpect(header().stringValues("Set-Cookie",
                        org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("accessToken="))))
                .andExpect(header().stringValues("Set-Cookie",
                        org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("refreshToken="))));
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("로그아웃(탈퇴) 요청 시 서비스 호출 및 204 반환")
    void withdrawUser_ShouldCallServiceAndReturnNoContent() throws Exception {
        Long mockUserId = 1L;
        doNothing().when(kakaoOAuth2Service).withdrawUser(mockUserId);

        mockMvc.perform(delete("/oauth2/authorization/kakao/users/withdraw")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(kakaoOAuth2Service, times(1)).withdrawUser(mockUserId);
    }

}