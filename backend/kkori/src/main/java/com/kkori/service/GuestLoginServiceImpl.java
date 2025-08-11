package com.kkori.service;

import com.kkori.dto.response.LoginResponse;
import com.kkori.entity.RefreshToken;
import com.kkori.entity.User;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.repository.RefreshTokenRepository;
import com.kkori.repository.UserRepository;
import com.kkori.util.NicknameGenerator;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GuestLoginServiceImpl implements GuestLoginService {

    private static final String GUEST_PREFIX = "guest_";

    @Value("${jwt.ACCESS_TOKEN_MINUTE_TIME}")
    private int accessTokenExpireMinutes;

    @Value("${jwt.REFRESH_TOKEN_MINUTE_TIME}")
    private int refreshTokenExpireMinutes;

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public LoginResponse createGuestUserAndLogin() {
        log.info("게스트 사용자 로그인 시작");

        // 게스트 사용자 생성
        User guestUser = createGuestUser();
        log.info("게스트 사용자 생성됨: sub={}, nickname={}", guestUser.getSub(), guestUser.getNickname());

        // 사용자 저장
        User savedUser = userRepository.save(guestUser);

        // JWT Refresh Token 생성
        Token refreshToken = tokenProvider.generateRefreshToken(savedUser);

        // RefreshToken 엔티티 저장
        saveRefreshTokenForUser(savedUser, refreshToken);

        log.info("게스트 로그인 완료: userId={}, nickname={}", savedUser.getUserId(), savedUser.getNickname());
        return new LoginResponse(refreshToken, savedUser.getNickname());
    }

    @Override
    public Token issueAccessToken(String refreshToken) {
        return issueAccessTokenByValidRefreshToken(refreshToken);
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

    private User createGuestUser() {
        String guestSub = GUEST_PREFIX + UUID.randomUUID().toString();
        String randomNickname = NicknameGenerator.generate();

        return User.builder()
                .sub(guestSub)
                .nickname(randomNickname)
                .deleted(false)
                .build();
    }

    private void saveRefreshTokenForUser(User user, Token refreshToken) {
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken.getToken())
                .expirationDate(LocalDateTime.now().plusMinutes(refreshTokenExpireMinutes))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
        log.debug("RefreshToken 저장 완료: userId={}", user.getUserId());
    }


    private boolean isValidRefreshTokenInput(String refreshTokenValue) {
        return refreshTokenValue != null && !refreshTokenValue.isBlank() 
               && tokenProvider.validateToken(refreshTokenValue);
    }

}
