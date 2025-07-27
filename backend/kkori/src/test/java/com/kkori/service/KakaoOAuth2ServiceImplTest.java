package com.kkori.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.kkori.dto.response.KakaoProfileResponse;
import com.kkori.dto.response.KakaoTokenResponse;
import com.kkori.dto.response.LoginResponse;
import com.kkori.entity.User;
import com.kkori.repository.UserRepository;
import com.kkori.util.IdTokenValidator;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
    private RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private ResponseSpec responseSpec;

    @Mock
    private UserRepository userRepository;

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

    }

    @Test
    @DisplayName("정상 인가코드로 로그인 성공")
    void loginWithValidAuthorizationCode_shouldReturnLoginResponse() {

        KakaoTokenResponse tokenResponse = new KakaoTokenResponse();
        setField(tokenResponse, "tokenType", "Bearer");
        setField(tokenResponse, "accessToken", "access-token");
        setField(tokenResponse, "refreshToken", "refresh-token");
        setField(tokenResponse, "idToken", VALID_ID_TOKEN);
        setField(tokenResponse, "expiresIn", 3600);

        KakaoProfileResponse.Properties props = new KakaoProfileResponse.Properties();
        setField(props, "nickname", "홍길동");
        KakaoProfileResponse profileResponse = new KakaoProfileResponse();
        setField(profileResponse, "properties", props);

        User savedUser = new User("sub123", "홍길동");

        given(webClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.contentType(any())).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoTokenResponse.class)).willReturn(Mono.just(tokenResponse));

        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(KakaoProfileResponse.class)).willReturn(Mono.just(profileResponse));

        given(httpSession.getAttribute("oauth2_kakao_nonce")).willReturn(VALID_NONCE);

        given(userRepository.findBySub(anyString())).willReturn(Optional.empty());
        given(userRepository.save(any())).willReturn(savedUser);

        try (MockedStatic<IdTokenValidator> mocked =
                     mockStatic(com.kkori.util.IdTokenValidator.class)) {
            mocked.when(() -> com.kkori.util.IdTokenValidator.validateNonce(anyString(), any(HttpSession.class)))
                    .thenReturn(true);
            mocked.when(() -> com.kkori.util.IdTokenValidator.getSub(anyString()))
                    .thenReturn("sub123");

            LoginResponse response = kakaoOAuth2Service.loginWithKakao("valid-code", httpSession);

            assertNotNull(response);
            assertEquals("홍길동", response.getNickname());
            assertEquals("jwt-accesstoken-sample", response.getAccessToken().getToken());
            assertEquals("jwt-refreshtoken-sample", response.getRefreshToken().getToken());
        }
    }

    @Test
    @DisplayName("Nonce 검증 실패 시 IllegalArgumentException 예외 발생")
    void loginWithInvalidNonce_shouldThrowIllegalArgumentException() {
        KakaoTokenResponse tokenResponse = new KakaoTokenResponse();
        setField(tokenResponse, "accessToken", "access-token");
        setField(tokenResponse, "refreshToken", "refresh-token");
        setField(tokenResponse, "idToken", INVALID_ID_TOKEN);

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
}
