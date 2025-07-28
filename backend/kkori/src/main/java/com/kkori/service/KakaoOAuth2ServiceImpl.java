package com.kkori.service;

import static com.kkori.util.NonceUtil.generateNonce;
import static com.kkori.util.NonceUtil.saveNonce;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2ServiceImpl implements KakaoOAuth2Service {

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
    public String createAuthorizationUrl(HttpSession session) {
        String nonce = generateNonce();

        saveNonce(session, nonce);

        return fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("nonce", nonce)
                .build()
                .toUriString();
    }

    @Override
    public LoginResponse loginWithKakao(String authorizationCode, HttpSession session) {
        KakaoTokenResponse tokenResponse = requestKakaoToken(authorizationCode);

        String idToken = tokenResponse.getIdToken();

        if (idToken == null || !IdTokenValidator.validateNonce(idToken, session)) {
            throw new IllegalArgumentException("Nonce 검증 실패");
        }

        String sub = IdTokenValidator.getSub(idToken);

        String nickname = requestKakaoNickname(tokenResponse.getAccessToken());

        User user = userRepository.findBySub(sub)
                .orElseGet(() -> userRepository.save(new User(sub, nickname)));

        Token accessToken = new Token("jwt-accesstoken-sample");
        Token refreshToken = new Token("jwt-refreshtoken-sample");

        return new LoginResponse(accessToken, refreshToken, user.getNickname());
    }

    public Token refreshAccessToken(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return null;
        }
        if (!tokenProvider.validateToken(refreshTokenValue)) {
            return null;
        }
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .orElse(null);
        if (refreshToken == null || refreshToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            return null;
        }
        User user = refreshToken.getUser();
        if (user == null) {
            return null;
        }
        return tokenProvider.generateToken(user, 30);
    }

    private KakaoTokenResponse requestKakaoToken(String code) {
        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", redirectUri)
                        .with("code", code))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("카카오 토큰 발급 실패"));
    }

    private String requestKakaoNickname(String accessToken) {
        KakaoProfileResponse profileResponse = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KakaoProfileResponse.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("카카오 프로필 조회 실패"));

        if (profileResponse.getProperties() == null || profileResponse.getProperties().getNickname() == null) {
            throw new RuntimeException("카카오 닉네임 조회 실패");
        }

        return profileResponse.getProperties().getNickname();
    }

}
