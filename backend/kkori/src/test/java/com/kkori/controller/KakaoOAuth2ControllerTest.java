package com.kkori.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.kkori.dto.response.UserProfileResponse;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.repository.UserRepository;
import com.kkori.service.KakaoOAuth2Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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
    private static final String NEW_USER_NICKNAME = "새사용자";
    private static final String EXISTING_USER_NICKNAME = "기존사용자";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KakaoOAuth2Service kakaoOAuth2Service;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TokenProvider tokenProvider;

    private final Token accessToken = new Token(ACCESS_TOKEN_VALUE);
    private final Token refreshToken = new Token(REFRESH_TOKEN_VALUE);

    private ResultActions performLoginCallbackWithCode(String code) throws Exception {
        return mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                .param("code", code)
                .accept(MediaType.APPLICATION_JSON));
    }

    private LoginResponse createLoginResponse(String nickname) {
        return new LoginResponse(refreshToken, nickname);
    }

    @Test
    @WithMockUser
    @DisplayName("인가 URL 생성 요청 시 302 Found와 Location 헤더로 리다이렉트")
    void createAuthorizationUrl_ShouldReturnRedirectWithUrl() throws Exception {
        when(kakaoOAuth2Service.buildKakaoAuthorizeUrlAndSaveNonceInSession(any(HttpSession.class))).thenReturn(
                KAKAO_AUTH_URL);

        mockMvc.perform(get("/oauth2/authorization/kakao")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", KAKAO_AUTH_URL));
    }

    @Test
    @WithMockUser
    @DisplayName("인가 URL 생성 중 서비스 예외 발생 시 500 Internal Server Error 반환")
    void createAuthorizationUrl_WhenServiceThrows_ShouldReturnInternalServerError() throws Exception {
        when(kakaoOAuth2Service.buildKakaoAuthorizeUrlAndSaveNonceInSession(any(HttpSession.class)))
                .thenThrow(new RuntimeException("내부 서버 오류"));

        mockMvc.perform(get("/oauth2/authorization/kakao")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    @DisplayName("유효한 인가코드로 로그인 요청 시 302와 JWT 토큰, 닉네임 반환 및 쿠키 설정")
    void loginWithValidAuthorizationCode_ShouldReturnTokensAndNickname() throws Exception {
        given(kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(anyString(), any()))
                .willReturn(createLoginResponse(NICKNAME));

        when(kakaoOAuth2Service.issueAccessToken(anyString()))
                .thenReturn(new Token(ACCESS_TOKEN_VALUE));

        performLoginCallbackWithCode(VALID_CODE)
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Set-Cookie",
                        hasItem(Matchers.containsString("accessToken="))))
                .andExpect(header().stringValues("Set-Cookie",
                        hasItem(Matchers.containsString("refreshToken="))));
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
        given(kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(anyString(), any()))
                .willThrow(new IllegalArgumentException("Invalid authorization code"));

        performLoginCallbackWithCode(INVALID_CODE)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("카카오 API 서버 장애로 로그인 실패 시 500 Internal Server Error 반환")
    void loginWhenKakaoApiFails_ShouldReturnInternalServerError() throws Exception {
        given(kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(anyString(), any()))
                .willThrow(new RuntimeException("카카오 서버 장애"));

        performLoginCallbackWithCode(VALID_KAKAO_CODE)
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    @DisplayName("신규 사용자면 DB 저장 이후 로그인 응답 성공")
    void loginCallback_NewUser_Success() throws Exception {
        given(kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(anyString(),
                any(HttpSession.class))).willReturn(
                createLoginResponse(NEW_USER_NICKNAME));

        when(kakaoOAuth2Service.issueAccessToken(anyString()))
                .thenReturn(new Token(ACCESS_TOKEN_VALUE));

        performLoginCallbackWithCode("new-user-code")
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Set-Cookie",
                        hasItem(Matchers.containsString("refreshToken="))))
                .andExpect(header().stringValues("Set-Cookie",
                        hasItem(Matchers.containsString("accessToken="))));
    }

    @Test
    @WithMockUser
    @DisplayName("기존 사용자면 즉시 로그인 응답 성공")
    void loginCallback_ExistingUser_Success() throws Exception {
        LoginResponse loginResponse = createLoginResponse(EXISTING_USER_NICKNAME);

        given(kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(anyString(),
                any(HttpSession.class)))
                .willReturn(loginResponse);

        when(kakaoOAuth2Service.issueAccessToken(anyString()))
                .thenReturn(new Token(ACCESS_TOKEN_VALUE));

        performLoginCallbackWithCode("existing-user-code")
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Set-Cookie",
                        hasItem(Matchers.containsString("accessToken="))))
                .andExpect(header().stringValues("Set-Cookie",
                        hasItem(Matchers.containsString("refreshToken="))));
    }

    @Test
    @WithMockUser
    @DisplayName("DB 저장 실패 시 500 에러 반환")
    void loginCallback_SaveFailure_ShouldReturnInternalServerError() throws Exception {
        String errMsg = "DB 저장 실패";
        given(kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(anyString(),
                any(HttpSession.class))).willThrow(
                new RuntimeException(errMsg));

        performLoginCallbackWithCode("fail-code")
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(Matchers.containsString(errMsg)));
    }

    @Test
    @WithMockUser
    @DisplayName("리프레시 토큰 유효 시 액세스 토큰 재발급 성공")
    void refreshAccessToken_ValidRefreshToken_Success() throws Exception {
        String validRefreshToken = "valid-refresh-token";
        Token newAccessToken = new Token("new-access-token");
        when(kakaoOAuth2Service.issueAccessTokenByValidRefreshToken(validRefreshToken)).thenReturn(newAccessToken);

        mockMvc.perform(post("/oauth2/authorization/kakao/token/refresh")
                        .cookie(new Cookie("refreshToken", validRefreshToken))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(newAccessToken.getToken()));
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
        LoginResponse mockResponse = createLoginResponse(NICKNAME);
        given(kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(anyString(), any())).willReturn(
                mockResponse);
        when(kakaoOAuth2Service.issueAccessToken(anyString()))
                .thenReturn(new Token(ACCESS_TOKEN_VALUE));

        performLoginCallbackWithCode(VALID_CODE)
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Set-Cookie",
                        hasItem(Matchers.containsString("refreshToken="))))
                .andExpect(header().stringValues("Set-Cookie",
                        hasItem(Matchers.containsString("accessToken="))));
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("로그아웃(탈퇴) 요청 시 서비스 호출 및 204 반환")
    void withdrawUser_ShouldCallServiceAndReturnNoContent() throws Exception {
        Long mockUserId = 1L;
        doNothing().when(kakaoOAuth2Service).softDeleteUserAndRemoveAllRefreshTokens(mockUserId);

        mockMvc.perform(delete("/oauth2/authorization/kakao/users/withdraw").with(csrf()))
                .andExpect(status().isNoContent());

        verify(kakaoOAuth2Service, times(1)).softDeleteUserAndRemoveAllRefreshTokens(mockUserId);
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("인증된 사용자는 사용자 정보 조회에 성공")
    void getUserInfo_Success() throws Exception {
        UserProfileResponse userProfileResponse = new UserProfileResponse(1L, "홍길동");

        given(kakaoOAuth2Service.getUserProfile(1L)).willReturn(userProfileResponse);

        mockMvc.perform(get("/oauth2/authorization/kakao/userinfo").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.nickname").value("홍길동"));
    }

    @Test
    @DisplayName("인증되지 않은 사용자 정보 조회 시 401 Unauthorized 반환")
    void getUserInfo_Unauthorized() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/kakao/userinfo").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("지원하지 않는 principal 타입으로 예외 발생")
    void getUserInfo_UnsupportedPrincipalType_ShouldReturnServerError() throws Exception {

        given(kakaoOAuth2Service.getUserProfile(anyLong())).willThrow(
                new IllegalArgumentException("지원하지 않는 principal 타입: java.lang.Integer"));

        mockMvc.perform(get("/oauth2/authorization/kakao/userinfo").with(request -> {
                            request.setUserPrincipal(() -> "user");
                            return request;
                        })
                        .accept(MediaType.APPLICATION_JSON)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(
                                "1")))
                .andExpect(status().isBadRequest());
    }

}