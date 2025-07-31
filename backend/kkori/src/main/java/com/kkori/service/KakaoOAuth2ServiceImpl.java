package com.kkori.service;

import static com.kkori.util.NonceUtil.generateNonce;
import static com.kkori.util.NonceUtil.saveNonce;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

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
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuth2ServiceImpl implements KakaoOAuth2Service {

    private static final String KAKAO_AUTHORIZE_BASE_URL = "https://kauth.kakao.com/oauth/authorize";
    private static final String KAKAO_USER_PROFILE_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String RESPONSE_TYPE_CODE = "code";
    private static final String QUERY_PARAM_GRANT_TYPE = "authorization_code";
    private static final String ERROR_USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    private static final String ERROR_DELETED_USER = "탈퇴한 사용자입니다.";
    private static final String ERROR_TOKEN_ISSUE_FAIL = "카카오 토큰 발급 실패";
    private static final String ERROR_PROFILE_FAIL = "카카오 프로필 조회 실패";
    private static final String ERROR_NICKNAME_FAIL = "카카오 닉네임 조회 실패";
    private static final String ERROR_IDTOKEN_INVALID = "id_token 검증 실패";

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-url}")
    private String redirectUri;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String buildKakaoAuthorizeUrlAndSaveNonceInSession(HttpSession session) {
        String nonce = generateNonce();
        saveNonce(session, nonce);

        return fromUriString(KAKAO_AUTHORIZE_BASE_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", RESPONSE_TYPE_CODE)
                .queryParam("nonce", nonce)
                .build()
                .toUriString();
    }

    @Override
    public LoginResponse exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(String authorizationCode,
                                                                                HttpSession session) {
        log.info("id_token 검증 시작");
        KakaoTokenResponse kakaoTokenResponse = fetchKakaoTokenByAuthorizationCode(authorizationCode);
        log.info("id_token 추출됨: {}", kakaoTokenResponse.getIdToken());

        boolean valid = IdTokenValidator.validateIdTokenClaims(kakaoTokenResponse.getIdToken(), session, clientId);
        log.info("id_token 검증 결과: {}", valid);
        if (!valid) {
            throw new IllegalArgumentException(ERROR_IDTOKEN_INVALID);
        }

        String kakaoSub = IdTokenValidator.getSub(kakaoTokenResponse.getIdToken());
        String accessToken = kakaoTokenResponse.getAccessToken();

        String nickname = fetchNicknameFromKakaoProfile(accessToken);

        User user = findOrCreateUserByKakaoSub(kakaoSub, nickname);

        Token refreshJwtToken = tokenProvider.generateRefreshToken(user);

        saveRefreshTokenForUser(user, refreshJwtToken);

        return new LoginResponse(refreshJwtToken, user.getNickname());
    }

    @Override
    public void softDeleteUserAndRemoveAllRefreshTokens(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ERROR_USER_NOT_FOUND));
        user.softDelete();
        userRepository.save(user);
        deleteAllRefreshTokensByUser(user);
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {

        if (userId == null) {
            throw new NullPointerException("userId가 null입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return new UserProfileResponse(user.getNickname());
    }

    @Override
    public Token issueAccessTokenByValidRefreshToken(String refreshTokenValue) {
        if (!isValidRefreshTokenInput(refreshTokenValue)) {
            return null;
        }

        return refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .filter(rt -> rt.getExpirationDate().isAfter(LocalDateTime.now()))
                .filter(rt -> rt.getUser() != null)
                .map(RefreshToken::getUser)
                .map(tokenProvider::generateAccessToken)
                .orElse(null);
    }

    private User findOrCreateUserByKakaoSub(String kakaoSub, String nickname) {
        return userRepository.findBySubAndDeletedFalse(kakaoSub)
                .map(user -> {
                    if (user.isDeleted()) {
                        throw new IllegalStateException(ERROR_DELETED_USER);
                    }
                    return user;
                })
                .orElseGet(() -> userRepository.save(new User(kakaoSub, nickname)));
    }

    private void saveRefreshTokenForUser(User user, Token refreshToken) {
        int expireMinutes = tokenProvider.getRefreshTokenExpireMinutes();
        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(refreshToken.getToken())
                .user(user)
                .expirationDate(LocalDateTime.now().plusMinutes(expireMinutes))
                .build());
    }

    private boolean isValidRefreshTokenInput(String refreshTokenValue) {
        return refreshTokenValue != null && !refreshTokenValue.isBlank() && tokenProvider.validateToken(
                refreshTokenValue);
    }

    private void deleteAllRefreshTokensByUser(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        refreshTokenRepository.deleteAll(tokens);
    }

    KakaoTokenResponse fetchKakaoTokenByAuthorizationCode(String code) {
        log.info("토큰 요청 파라미터: grant_type=authorization_code, client_id={}, redirect_uri={}, code={}",
                clientId, redirectUri, code);

        KakaoTokenResponse response = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", QUERY_PARAM_GRANT_TYPE)
                        .with("client_id", clientId)
                        .with("redirect_uri", redirectUri)
                        .with("code", code)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        log.info("토큰 응답: {}", response);
        if (response == null) {
            throw new RuntimeException(ERROR_TOKEN_ISSUE_FAIL);
        }
        return response;
    }

    private String fetchNicknameFromKakaoProfile(String accessToken) {
        KakaoProfileResponse profileResponse = webClient.get()
                .uri(KAKAO_USER_PROFILE_URL)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KakaoProfileResponse.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException(ERROR_PROFILE_FAIL));

        if (profileResponse.getProperties() == null || profileResponse.getProperties().getNickname() == null) {
            throw new RuntimeException(ERROR_NICKNAME_FAIL);
        }
        return profileResponse.getProperties().getNickname();
    }

}
