package com.kkori.service;

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
    private RequestHeadersSpec<?> requestHeadersSpec;

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

        setField(kakaoOAuth2Service, "accessTokenExpireMinutes", 30);
        setField(kakaoOAuth2Service, "refreshTokenExpireMinutes", 10080);

        given(requestBodySpec.body(any(BodyInserter.class))).willAnswer(invocation -> requestBodySpec);
        given(requestHeadersSpec.headers(any())).willAnswer(invocation -> requestHeadersSpec);

        when(tokenProvider.generateToken(any(User.class), eq(30))).thenReturn(new Token("jwt-accesstoken-sample"));
        when(tokenProvider.generateToken(any(User.class), eq(10080))).thenReturn(new Token("jwt-refreshtoken-sample"));

    }

    @Test
    @DisplayName("정상 인가코드면 신규 사용자 로그인 후 LoginResponse 반환")
    void loginWithValidAuthorizationCode_shouldReturnLoginResponse() {
        KakaoTokenResponse tokenResponse = createKakaoTokenResponse(VALID_ID_TOKEN);
        KakaoProfileResponse profileResponse = createKakaoProfileResponse("홍길동");
        User savedUser = new User("sub123", "홍길동");

        mockKakaoTokenRequest(tokenResponse);
        mockKakaoProfileRequest(profileResponse);
        given(httpSession.getAttribute("oauth2_kakao_nonce")).willReturn(VALID_NONCE);
        given(userRepository.findBySub(anyString())).willReturn(Optional.empty());
        given(userRepository.save(any())).willReturn(savedUser);

        try (MockedStatic<IdTokenValidator> mockedIdToken = mockStatic(IdTokenValidator.class)) {
            mockedIdToken.when(() -> IdTokenValidator.validateNonce(anyString(), any(HttpSession.class)))
                    .thenReturn(true);
            mockedIdToken.when(() -> IdTokenValidator.getSub(anyString())).thenReturn("sub123");

            LoginResponse response = kakaoOAuth2Service.loginWithKakao("valid-code", httpSession);

            assertNotNull(response);
            assertEquals("홍길동", response.getNickname());
            assertEquals("jwt-accesstoken-sample", response.getAccessToken().getToken());
            assertEquals("jwt-refreshtoken-sample", response.getRefreshToken().getToken());
        }
    }

    @Test
    @DisplayName("정상 인가코드면 기존 사용자 로그인 후 LoginResponse 반환")
    void loginWithExistingUser_shouldReturnExistingUser() {
        KakaoTokenResponse tokenResponse = createKakaoTokenResponse(VALID_ID_TOKEN);
        KakaoProfileResponse profileResponse = createKakaoProfileResponse("기존사용자");
        User existingUser = new User("sub123", "기존사용자");

        mockKakaoTokenRequest(tokenResponse);
        mockKakaoProfileRequest(profileResponse);
        given(httpSession.getAttribute("oauth2_kakao_nonce")).willReturn(VALID_NONCE);
        given(userRepository.findBySub(anyString())).willReturn(Optional.of(existingUser));

        try (MockedStatic<IdTokenValidator> mockedIdToken = mockStatic(IdTokenValidator.class)) {
            mockedIdToken.when(() -> IdTokenValidator.validateNonce(anyString(), any(HttpSession.class)))
                    .thenReturn(true);
            mockedIdToken.when(() -> IdTokenValidator.getSub(anyString())).thenReturn("sub123");

            LoginResponse response = kakaoOAuth2Service.loginWithKakao("valid-code", httpSession);

            assertNotNull(response);
            assertEquals("기존사용자", response.getNickname());
            assertEquals("jwt-accesstoken-sample", response.getAccessToken().getToken());
            assertEquals("jwt-refreshtoken-sample", response.getRefreshToken().getToken());
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
                () -> kakaoOAuth2Service.loginWithKakao("some-code", httpSession));

        assertEquals("Nonce 검증 실패", exception.getMessage());
    }

    @Test
    @DisplayName("카카오 토큰 요청 실패 시 RuntimeException 발생")
    void requestKakaoToken_failure_shouldThrowRuntimeException() {
        given(webClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.contentType(any())).willReturn(requestBodySpec);
        given(requestBodySpec.body(any(BodyInserter.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoTokenResponse.class)).willReturn(Mono.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> kakaoOAuth2Service.loginWithKakao("code", httpSession));

        assertEquals("카카오 토큰 발급 실패", exception.getMessage());
    }

    @Test
    @DisplayName("카카오 프로필 조회 실패 시 RuntimeException 발생")
    void requestKakaoNickname_failure_shouldThrowRuntimeException() {
        KakaoTokenResponse tokenResponse = createKakaoTokenResponse(VALID_ID_TOKEN);

        mockKakaoTokenRequest(tokenResponse);

        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.headers(any())).willAnswer(invocation -> requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoProfileResponse.class)).willReturn(Mono.empty());

        try (MockedStatic<IdTokenValidator> mockedIdToken = mockStatic(IdTokenValidator.class)) {
            mockedIdToken.when(() -> IdTokenValidator.validateNonce(anyString(), any(HttpSession.class)))
                    .thenReturn(true);
            mockedIdToken.when(() -> IdTokenValidator.getSub(anyString())).thenReturn("sub123");

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> kakaoOAuth2Service.loginWithKakao("valid-code", httpSession));
            assertEquals("카카오 프로필 조회 실패", exception.getMessage());
        }
    }

    @Test
    @DisplayName("카카오 프로필 닉네임 누락 시 RuntimeException 발생")
    void requestKakaoNickname_missingNickname_shouldThrowRuntimeException() {
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
            mockedIdToken.when(() -> IdTokenValidator.validateNonce(anyString(), any(HttpSession.class)))
                    .thenReturn(true);
            mockedIdToken.when(() -> IdTokenValidator.getSub(anyString())).thenReturn("sub123");

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> kakaoOAuth2Service.loginWithKakao("valid-code", httpSession));
            assertEquals("카카오 닉네임 조회 실패", exception.getMessage());
        }
    }

    @Test
    @DisplayName("리프레시 토큰이 유효할 때 새로운 액세스 토큰을 반환")
    void refreshAccessToken_ValidToken_ReturnsToken() {
        String validRefreshToken = "valid-refresh-token";
        User user = new User("sub", "nickname");
        ReflectionTestUtils.setField(user, "userId", 1L);

        RefreshToken refreshToken = new RefreshToken();
        ReflectionTestUtils.setField(refreshToken, "refreshToken", validRefreshToken);
        ReflectionTestUtils.setField(refreshToken, "user", user);
        ReflectionTestUtils.setField(refreshToken, "expirationDate", LocalDateTime.now().plusDays(1));

        when(tokenProvider.validateToken(validRefreshToken)).thenReturn(true);
        when(refreshTokenRepository.findByRefreshToken(validRefreshToken))
                .thenReturn(Optional.of(refreshToken));
        when(tokenProvider.generateToken(user, 30))
                .thenReturn(new Token("new-access-token"));

        Token result = kakaoOAuth2Service.refreshAccessToken(validRefreshToken);

        assertNotNull(result);
        assertEquals("new-access-token", result.getToken());
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
