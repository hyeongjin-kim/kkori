package com.kkori.controller;

import com.kkori.dto.response.LoginResponse;
import com.kkori.dto.response.UserProfileResponse;
import com.kkori.exception.user.UnsupportedPrincipalException;
import com.kkori.jwt.Token;
import com.kkori.service.KakaoOAuth2Service;
import com.kkori.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/authorization/kakao")
public class KakaoOAuth2Controller {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int ACCESS_TOKEN_EXPIRE_SECONDS = 60 * 60;
    private static final int REFRESH_TOKEN_EXPIRE_SECONDS = 60 * 60 * 24 * 7;
    private static final String AUTHORIZATION_CODE_PARAM = "code";
    private static final String ERROR_MISSING_CODE = "인가코드가 필요합니다";
    private static final String ERROR_UNSUPPORTED_PRINCIPAL_TYPE = "지원하지 않는 principal 타입: ";

    private final KakaoOAuth2Service kakaoOAuth2Service;

    @GetMapping
    public ResponseEntity<String> getKakaoAuthorizationUrl(HttpSession session) {
        String authorizationUrl = kakaoOAuth2Service.buildKakaoAuthorizeUrlAndSaveNonceInSession(session);
        return ResponseEntity.ok(authorizationUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<LoginResponse> handleKakaoCallback(
            @RequestParam(AUTHORIZATION_CODE_PARAM) String code, HttpSession session, HttpServletResponse response) {

        log.info("Kakao OAuth2 callback received. authorization code: {}", code);

        if (code == null || code.trim().isEmpty()) {
            log.warn("인가코드가 빈 값으로 들어옴");
            throw new IllegalArgumentException(ERROR_MISSING_CODE);
        }

        LoginResponse loginResponse = kakaoOAuth2Service.exchangeAuthorizationCodeForLoginAndCreateUserIfNeeded(code,
                session);

        log.info("로그인 응답: accessToken={}, refreshToken={}, nickname={}",
                loginResponse.getAccessToken().getToken(),
                loginResponse.getRefreshToken().getToken(),
                loginResponse.getNickname());

        CookieUtil.addJwtCookie(response, ACCESS_TOKEN_COOKIE_NAME, loginResponse.getAccessToken().getToken(),
                ACCESS_TOKEN_EXPIRE_SECONDS);
        CookieUtil.addJwtCookie(response, REFRESH_TOKEN_COOKIE_NAME, loginResponse.getRefreshToken().getToken(),
                REFRESH_TOKEN_EXPIRE_SECONDS);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<Token> refreshAccessToken(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshTokenValue) {

        Token newAccessToken = kakaoOAuth2Service.issueAccessTokenByValidRefreshToken(refreshTokenValue);
        if (newAccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(newAccessToken);
    }

    @DeleteMapping("/users/withdraw")
    public ResponseEntity<Void> withdrawUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        long userId = extractUserIdFromAuthentication(authentication);

        kakaoOAuth2Service.softDeleteUserAndRemoveAllRefreshTokens(userId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/userinfo")
    public ResponseEntity<UserProfileResponse> getUserInfo(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        long userId = extractUserIdFromAuthentication(authentication);

        UserProfileResponse response = kakaoOAuth2Service.getUserProfile(userId);

        return ResponseEntity.ok(response);
    }

    private long extractUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        log.info("Principal class: {}, value: {}", principal.getClass(), principal);

        if (principal instanceof User userDetails) {
            return Long.parseLong(userDetails.getUsername());
        } else if (principal instanceof String str) {
            return Long.parseLong(str);
        } else if (principal instanceof Long id) {
            return id;
        } else {
            log.error("Unsupported principal type: {}", principal.getClass());
            throw new UnsupportedPrincipalException(ERROR_UNSUPPORTED_PRINCIPAL_TYPE + principal.getClass());
        }
    }

}
