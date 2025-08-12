package com.kkori.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kkori.dto.response.LoginResponse;
import com.kkori.jwt.Token;
import com.kkori.service.GuestLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(GuestLoginController.class)
class GuestLoginControllerTest {

    private static final String GUEST_NICKNAME = "신비한코알라";
    private static final String ACCESS_TOKEN_VALUE = "guest-jwt-access-token";
    private static final String REFRESH_TOKEN_VALUE = "guest-jwt-refresh-token";
    private static final String API_ENDPOINT = "/api/login/guest";
    private static final int ACCESS_TOKEN_MAX_AGE = 900;       // 15분
    private static final int REFRESH_TOKEN_MAX_AGE = 604800;   // 7일

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GuestLoginService guestLoginService;

    private final Token accessToken = new Token(ACCESS_TOKEN_VALUE);
    private final Token refreshToken = new Token(REFRESH_TOKEN_VALUE);

    private ResultActions performGuestLogin() throws Exception {
        return mockMvc.perform(post(API_ENDPOINT)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private LoginResponse createGuestLoginResponse(String nickname) {
        return new LoginResponse(refreshToken, nickname);
    }

    private void mockGuestLoginSuccess(String nickname) {
        given(guestLoginService.createGuestUserAndLogin())
                .willReturn(createGuestLoginResponse(nickname));
        given(guestLoginService.issueAccessToken(refreshToken.getToken()))
                .willReturn(accessToken);
    }

    private void mockGuestLoginThrowing(Exception exception) {
        given(guestLoginService.createGuestUserAndLogin())
                .willThrow(exception);
    }

    @Test
    @WithMockUser
    @DisplayName("게스트 로그인 성공 시 200 OK와 로그인 응답 반환")
    void guestLogin_Success_ShouldReturnOkWithLoginResponse() throws Exception {
        mockGuestLoginSuccess(GUEST_NICKNAME);

        performGuestLogin()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken.token").value(REFRESH_TOKEN_VALUE))
                .andExpect(jsonPath("$.nickname").value(GUEST_NICKNAME));
    }

    @Test
    @WithMockUser
    @DisplayName("게스트 로그인 성공 시 쿠키 설정 확인")
    void guestLogin_Success_ShouldSetCookies() throws Exception {
        mockGuestLoginSuccess(GUEST_NICKNAME);

        performGuestLogin()
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("accessToken", ACCESS_TOKEN_VALUE))
                .andExpect(cookie().value("refreshToken", REFRESH_TOKEN_VALUE))
                .andExpect(cookie().maxAge("accessToken", ACCESS_TOKEN_MAX_AGE))
                .andExpect(cookie().maxAge("refreshToken", REFRESH_TOKEN_MAX_AGE));
    }

    @Test
    @WithMockUser
    @DisplayName("게스트 로그인 시 랜덤 닉네임 생성 확인")
    void guestLogin_ShouldGenerateRandomNickname() throws Exception {
        String randomNickname = "활기찬펭귄";
        mockGuestLoginSuccess(randomNickname);

        performGuestLogin()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(randomNickname))
                .andExpect(jsonPath("$.nickname").isNotEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("게스트 로그인 중 서비스 예외 발생 시 500 반환")
    void guestLogin_WhenServiceThrows_ShouldReturnInternalServerError() throws Exception {
        mockGuestLoginThrowing(new RuntimeException("게스트 사용자 생성 실패"));
        performGuestLogin().andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    @DisplayName("Access Token 발급 실패 시 401 반환")
    void guestLogin_WhenAccessTokenIssueFails_ShouldReturnUnauthorized() throws Exception {
        given(guestLoginService.createGuestUserAndLogin())
                .willReturn(createGuestLoginResponse(GUEST_NICKNAME));
        given(guestLoginService.issueAccessToken(refreshToken.getToken()))
                .willReturn(null);

        performGuestLogin().andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("데이터베이스 오류 발생 시 500 반환")
    void guestLogin_WhenDatabaseError_ShouldReturnInternalServerError() throws Exception {
        mockGuestLoginThrowing(new RuntimeException("데이터베이스 연결 실패"));
        performGuestLogin().andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    @DisplayName("JWT 토큰 생성 실패 시 500 반환")
    void guestLogin_WhenJwtCreationFails_ShouldReturnInternalServerError() throws Exception {
        mockGuestLoginThrowing(new IllegalStateException("JWT 토큰 생성 실패"));
        performGuestLogin().andExpect(status().isInternalServerError());
    }

}