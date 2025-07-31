package com.kkori.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.kkori.dto.response.KakaoProfileResponse;
import com.kkori.dto.response.KakaoTokenResponse;
import com.kkori.dto.response.LoginResponse;
import com.kkori.dto.response.UserProfileResponse;
import com.kkori.entity.RefreshToken;
import com.kkori.entity.User;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.repository.RefreshTokenRepository;
import com.kkori.repository.UserRepository;
import com.kkori.util.IdTokenValidator;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

class KakaoOAuth2ServiceImplTest {

    private static final String DUMMY_CLIENT_ID = "dummy-client-id";
    private static final String DUMMY_CLIENT_SECRET = "dummy-client-secret";
    private static final String DUMMY_REDIRECT_URI = "dummy-redirect-uri";
    private static final String DUMMY_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    private static final String VALID_ID_TOKEN = "valid-id-token";
    private static final String INVALID_ID_TOKEN = "invalid-id-token";

    private static final String VALID_NONCE = "valid-nonce";
    private static final String INVALID_NONCE = "invalid-nonce";

    private static final String USER_SUB_NEW = "sub123";
    private static final String USER_SUB_EXISTING = "sub123";

    private static final String NICKNAME_NEW_USER = "홍길동";
    private static final String NICKNAME_EXISTING_USER = "기존사용자";
    private static final String NICKNAME_SOFT_DELETED_USER = "탈퇴사용자";

    private static final String TOKEN_ACCESS_SAMPLE = "jwt-accesstoken-sample";
    private static final String TOKEN_REFRESH_SAMPLE = "jwt-refreshtoken-sample";

    private static final String REFRESH_TOKEN_VALID = "jwt-valid-refresh-token";
    private static final String REFRESH_TOKEN_INVALID = "invalid-refresh-token";
    private static final String REFRESH_TOKEN_MISSING = "jwt-missing-token";
    private static final String REFRESH_TOKEN_EXPIRED = "jwt-expired-token";

    private static final Long USER_ID_1 = 1L;
    private static final Long USER_ID_NOT_EXIST = 999L;

    @InjectMocks
    private KakaoOAuth2ServiceImpl kakaoOAuth2Service;

    @Mock
    private WebClient webClient;

    @Mock
    private RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RequestBodySpec requestBodySpec;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RequestHeadersSpec requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private HttpSession httpSession;

    @BeforeEach
    void setUp() {
        openMocks(this);

        setField(kakaoOAuth2Service, "clientId", DUMMY_CLIENT_ID);
        setField(kakaoOAuth2Service, "clientSecret", DUMMY_CLIENT_SECRET);
        setField(kakaoOAuth2Service, "redirectUri", DUMMY_REDIRECT_URI);
        setField(kakaoOAuth2Service, "tokenUrl", DUMMY_TOKEN_URL);

        given(requestBodySpec.body(any(BodyInserter.class))).willAnswer(invocation -> requestBodySpec);
        given(requestHeadersSpec.headers(any())).willAnswer(invocation -> requestHeadersSpec);

        when(tokenProvider.generateAccessToken(any(User.class))).thenReturn(new Token(TOKEN_ACCESS_SAMPLE));
        when(tokenProvider.generateRefreshToken(any(User.class))).thenReturn(new Token(TOKEN_REFRESH_SAMPLE));
    }

    @Test
    @DisplayName("정상 인가코드면 신규 사용자 로그인 후 LoginResponse 반환")
    void loginWithValidAuthorizationCode_shouldReturnLoginResponse() {
        KakaoTokenResponse tokenResponse = createKakaoTokenResponse(VALID_ID_TOKEN);
        KakaoProfileResponse profileResponse = createKakaoProfileResponse(NICKNAME_NEW_USER);
        User savedUser = new User(USER_SUB_NEW, NICKNAME_NEW_USER);

        mockKakaoTokenRequest(tokenResponse);
        mockKakaoProfileRequest(profileResponse);
        given(httpSession.getAttribute("oauth2_kakao_nonce")).willReturn(VALID_NONCE);
        given(userRepository.findBySubAndDeletedFalse(anyString())).willReturn(Optional.empty());
        given(userRepository.save(any())).willReturn(savedUser);

        try (MockedStatic<IdTokenValidator> mockedIdToken = mockStatic(IdTokenValidator.class)) {
            mockedIdToken.when(() -> IdTokenValidator.validateIdTokenClaims(anyString(), any(HttpSession.class),
                    eq(DUMMY_CLIENT_ID))).thenReturn(true);
            mockedIdToken.when(() -> IdTokenValidator.getSub(anyString())).thenReturn(USER_SUB_NEW);

            LoginResponse response = kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(
                    "valid-code", httpSession);

            assertNotNull(response);
            assertEquals(NICKNAME_NEW_USER, response.getNickname());
            assertEquals(TOKEN_REFRESH_SAMPLE, response.getRefreshToken().getToken());
        }
    }

    @Test
    @DisplayName("정상 인가코드면 기존 사용자 로그인 후 LoginResponse 반환")
    void loginWithExistingUser_shouldReturnExistingUser() {
        KakaoTokenResponse tokenResponse = createKakaoTokenResponse(VALID_ID_TOKEN);
        KakaoProfileResponse profileResponse = createKakaoProfileResponse(NICKNAME_EXISTING_USER);
        User existingUser = new User(USER_SUB_EXISTING, NICKNAME_EXISTING_USER);

        mockKakaoTokenRequest(tokenResponse);
        mockKakaoProfileRequest(profileResponse);
        given(httpSession.getAttribute("oauth2_kakao_nonce")).willReturn(VALID_NONCE);
        given(userRepository.findBySubAndDeletedFalse(anyString())).willReturn(Optional.of(existingUser));

        try (MockedStatic<IdTokenValidator> mockedIdToken = mockStatic(IdTokenValidator.class)) {
            mockedIdToken.when(() -> IdTokenValidator.validateIdTokenClaims(anyString(), any(HttpSession.class),
                    eq(DUMMY_CLIENT_ID))).thenReturn(true);
            mockedIdToken.when(() -> IdTokenValidator.getSub(anyString())).thenReturn(USER_SUB_EXISTING);

            LoginResponse response = kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(
                    "valid-code", httpSession);

            assertNotNull(response);
            assertEquals(NICKNAME_EXISTING_USER, response.getNickname());
            assertEquals(TOKEN_REFRESH_SAMPLE, response.getRefreshToken().getToken());
        }
    }

    @Test
    @DisplayName("nonce 검증 실패 시 IllegalArgumentException 발생")
    void loginWithInvalidNonce_shouldThrowIllegalArgumentException() {
        KakaoTokenResponse tokenResponse = createKakaoTokenResponse(INVALID_ID_TOKEN);
        given(webClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.contentType(any())).willReturn(requestBodySpec);
        given(requestBodySpec.body(any(BodyInserter.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoTokenResponse.class)).willReturn(Mono.just(tokenResponse));

        given(httpSession.getAttribute("oauth2_kakao_nonce")).willReturn(INVALID_NONCE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded("some-code",
                        httpSession));

        assertEquals("id_token 검증 실패", exception.getMessage());
    }

    @Test
    @DisplayName("카카오 토큰 요청 실패 시 RuntimeException 발생")
    void fetchKakaoToken_ByAuthorizationCode_failure_shouldThrowRuntimeException() {
        given(webClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.contentType(any())).willReturn(requestBodySpec);
        given(requestBodySpec.body(any(BodyInserter.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoTokenResponse.class)).willReturn(Mono.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded("code", httpSession));

        assertEquals("카카오 토큰 발급 실패", exception.getMessage());
    }

    @Test
    @DisplayName("카카오 프로필 조회 실패 시 RuntimeException 발생")
    void fetchNickname_FromKakaoProfile_failure_shouldThrowRuntimeException() {
        KakaoTokenResponse tokenResponse = createKakaoTokenResponse(VALID_ID_TOKEN);

        mockKakaoTokenRequest(tokenResponse);

        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.headers(any())).willAnswer(invocation -> requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoProfileResponse.class)).willReturn(Mono.empty());

        try (MockedStatic<IdTokenValidator> mockedIdToken = mockStatic(IdTokenValidator.class)) {
            mockedIdToken.when(() -> IdTokenValidator.validateIdTokenClaims(anyString(), any(HttpSession.class),
                    eq(DUMMY_CLIENT_ID))).thenReturn(true);
            mockedIdToken.when(() -> IdTokenValidator.getSub(anyString())).thenReturn(USER_SUB_NEW);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded("valid-code",
                            httpSession));

            assertEquals("카카오 프로필 조회 실패", exception.getMessage());
        }
    }

    @Test
    @DisplayName("카카오 프로필 닉네임 누락 시 RuntimeException 발생")
    void fetchNickname_missingNickname_FromKakaoProfile_shouldThrowRuntimeException() {
        KakaoTokenResponse tokenResponse = createKakaoTokenResponse(VALID_ID_TOKEN);

        KakaoProfileResponse profileResponse = new KakaoProfileResponse();
        setField(profileResponse, "properties", null);

        mockKakaoTokenRequest(tokenResponse);

        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.headers(any())).willAnswer(invocation -> requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoProfileResponse.class)).willReturn(Mono.just(profileResponse));

        try (MockedStatic<IdTokenValidator> mockedIdToken = mockStatic(IdTokenValidator.class)) {
            mockedIdToken.when(() -> IdTokenValidator.validateIdTokenClaims(anyString(), any(HttpSession.class),
                    eq(DUMMY_CLIENT_ID))).thenReturn(true);
            mockedIdToken.when(() -> IdTokenValidator.getSub(anyString())).thenReturn(USER_SUB_NEW);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded("valid-code",
                            httpSession));

            assertEquals("카카오 닉네임 조회 실패", exception.getMessage());
        }
    }

    @Test
    @DisplayName("refreshToken이 유효할 때 새로운 액세스 토큰을 반환")
    void issueAccessToken_ValidToken_ReturnsTokenByValidRefreshToken() {
        User user = new User("sub", "nickname");
        ReflectionTestUtils.setField(user, "userId", USER_ID_1);

        RefreshToken refreshTokenEntity = new RefreshToken();
        ReflectionTestUtils.setField(refreshTokenEntity, "refreshToken", REFRESH_TOKEN_VALID);
        ReflectionTestUtils.setField(refreshTokenEntity, "user", user);
        ReflectionTestUtils.setField(refreshTokenEntity, "expirationDate", LocalDateTime.now().plusDays(1));

        when(tokenProvider.validateToken(REFRESH_TOKEN_VALID)).thenReturn(true);
        when(refreshTokenRepository.findByRefreshToken(REFRESH_TOKEN_VALID)).thenReturn(
                Optional.of(refreshTokenEntity));
        when(tokenProvider.generateAccessToken(user)).thenReturn(new Token(TOKEN_ACCESS_SAMPLE));

        Token result = kakaoOAuth2Service.issueAccessTokenByValidRefreshToken(REFRESH_TOKEN_VALID);

        assertNotNull(result);
        assertEquals(TOKEN_ACCESS_SAMPLE, result.getToken());
    }

    @Test
    @DisplayName("refreshToken이 유효하지 않으면 IllegalArgumentException 예외 발생")
    void issueAccessToken_InvalidToken_ThrowsException() {
        when(tokenProvider.validateToken(REFRESH_TOKEN_INVALID)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            kakaoOAuth2Service.issueAccessToken(REFRESH_TOKEN_INVALID);
        });
        assertEquals("Refresh token이 유효하지 않습니다.", ex.getMessage());
    }

    @Test
    @DisplayName("refreshToken이 DB에 없으면 RuntimeException 예외 발생")
    void issueAccessToken_TokenNotFound_ThrowsException() {
        when(tokenProvider.validateToken(REFRESH_TOKEN_MISSING)).thenReturn(true);
        when(refreshTokenRepository.findByRefreshToken(REFRESH_TOKEN_MISSING)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            kakaoOAuth2Service.issueAccessToken(REFRESH_TOKEN_MISSING);
        });
        assertEquals("Refresh token이 존재하지 않습니다.", ex.getMessage());
    }

    @Test
    @DisplayName("refreshToken이 만료되었으면 RuntimeException 예외 발생")
    void issueAccessToken_ExpiredToken_ThrowsException() {
        User user = new User("expired", "유저");
        ReflectionTestUtils.setField(user, "userId", 2L);

        RefreshToken refreshTokenEntity = new RefreshToken();
        ReflectionTestUtils.setField(refreshTokenEntity, "refreshToken", REFRESH_TOKEN_EXPIRED);
        ReflectionTestUtils.setField(refreshTokenEntity, "user", user);
        ReflectionTestUtils.setField(refreshTokenEntity, "expirationDate", LocalDateTime.now().minusMinutes(1));

        when(tokenProvider.validateToken(REFRESH_TOKEN_EXPIRED)).thenReturn(true);
        when(refreshTokenRepository.findByRefreshToken(REFRESH_TOKEN_EXPIRED)).thenReturn(
                Optional.of(refreshTokenEntity));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            kakaoOAuth2Service.issueAccessToken(REFRESH_TOKEN_EXPIRED);
        });
        assertEquals("Refresh token이 만료되었습니다.", ex.getMessage());
    }

    @Test
    @DisplayName("soft deleted 상태 사용자 로그인 시 IllegalStateException 발생")
    void loginWithSoftDeletedUser_shouldThrowIllegalStateException() {
        User softDeletedUser = new User("sub123", NICKNAME_SOFT_DELETED_USER);
        softDeletedUser.softDelete();

        KakaoTokenResponse tokenResponse = createKakaoTokenResponse(VALID_ID_TOKEN);
        KakaoProfileResponse profileResponse = createKakaoProfileResponse(NICKNAME_SOFT_DELETED_USER);

        mockKakaoTokenRequest(tokenResponse);
        mockKakaoProfileRequest(profileResponse);

        given(userRepository.findBySubAndDeletedFalse(anyString())).willReturn(Optional.of(softDeletedUser));
        given(httpSession.getAttribute(anyString())).willReturn(VALID_NONCE);

        try (MockedStatic<IdTokenValidator> mockedIdToken = mockStatic(IdTokenValidator.class)) {
            mockedIdToken.when(() -> IdTokenValidator.validateIdTokenClaims(anyString(), any(HttpSession.class),
                    eq(DUMMY_CLIENT_ID))).thenReturn(true);
            mockedIdToken.when(() -> IdTokenValidator.getSub(anyString())).thenReturn(USER_SUB_NEW);

            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded("code",
                            httpSession));
            assertEquals("탈퇴한 사용자입니다.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("유저 아이디로 사용자 닉네임 정상 조회")
    void getUserProfile_ShouldReturnNickname_WhenUserExists() {
        User user = new User("sub123", NICKNAME_NEW_USER);
        ReflectionTestUtils.setField(user, "userId", USER_ID_1);

        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(user));

        UserProfileResponse response = kakaoOAuth2Service.getUserProfile(USER_ID_1);

        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo(NICKNAME_NEW_USER);
    }

    @Test
    @DisplayName("유저 없을 경우 RuntimeException 발생")
    void getUserProfile_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(USER_ID_NOT_EXIST)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kakaoOAuth2Service.getUserProfile(USER_ID_NOT_EXIST);
        });

        assertThat(exception.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("null ID 입력 시 NullPointerException 발생")
    void getUserProfile_ShouldThrowNullPointerException_WhenUserIdIsNull() {
        assertThrows(NullPointerException.class, () -> kakaoOAuth2Service.getUserProfile(null));
    }

    @Test
    @DisplayName("음수 ID 입력 시 RuntimeException 발생 및 메시지 확인")
    void getUserProfile_ShouldThrowRuntimeException_WhenUserNotFound() {
        when(userRepository.findById(-1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> kakaoOAuth2Service.getUserProfile(-1L));
        assertThat(ex.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
    }

    private KakaoTokenResponse createKakaoTokenResponse(String idToken) {
        KakaoTokenResponse tokenResponse = new KakaoTokenResponse();
        setField(tokenResponse, "tokenType", "Bearer");
        setField(tokenResponse, "accessToken", "access-token");
        setField(tokenResponse, "refreshToken", "refresh-token");
        setField(tokenResponse, "idToken", idToken);
        setField(tokenResponse, "expiresIn", 3600);
        return tokenResponse;
    }

    private KakaoProfileResponse createKakaoProfileResponse(String nickname) {
        KakaoProfileResponse profileResponse = new KakaoProfileResponse();
        KakaoProfileResponse.Properties props = new KakaoProfileResponse.Properties();
        setField(props, "nickname", nickname);
        setField(profileResponse, "properties", props);
        return profileResponse;
    }

    private void mockKakaoTokenRequest(KakaoTokenResponse tokenResponse) {
        given(webClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.contentType(any())).willReturn(requestBodySpec);
        given(requestBodySpec.body(any(BodyInserter.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoTokenResponse.class)).willReturn(Mono.just(tokenResponse));
    }

    private void mockKakaoProfileRequest(KakaoProfileResponse profileResponse) {
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.headers(any())).willAnswer(invocation -> requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoProfileResponse.class)).willReturn(Mono.just(profileResponse));
    }

}
