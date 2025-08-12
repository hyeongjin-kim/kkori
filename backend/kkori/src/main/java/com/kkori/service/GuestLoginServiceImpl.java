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
import java.util.Optional;
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

        User guestUser = createAndSaveGuestUser();
        Token refreshToken = generateAndStoreRefreshToken(guestUser);

        log.info("게스트 로그인 완료: userId={}, nickname={}", guestUser.getUserId(), guestUser.getNickname());
        return new LoginResponse(refreshToken, guestUser.getNickname());
    }

    @Override
    public Token issueAccessToken(String refreshToken) {
        return issueAccessTokenByValidRefreshToken(refreshToken);
    }

    @Override
    public Token issueAccessTokenByValidRefreshToken(String refreshTokenValue) {
        if (!isValidRefreshToken(refreshTokenValue)) {
            return null;
        }

        return findUserByRefreshToken(refreshTokenValue)
                .map(tokenProvider::generateAccessToken)
                .orElse(null);
    }

    private User createAndSaveGuestUser() {
        User guestUser = User.builder()
                .sub(generateGuestSub())
                .nickname(NicknameGenerator.generate())
                .deleted(false)
                .build();

        User savedUser = userRepository.save(guestUser);
        log.info("게스트 사용자 생성됨: sub={}, nickname={}", savedUser.getSub(), savedUser.getNickname());
        return savedUser;
    }

    private String generateGuestSub() {
        return GUEST_PREFIX + UUID.randomUUID();
    }

    private Token generateAndStoreRefreshToken(User user) {
        Token refreshToken = tokenProvider.generateRefreshToken(user);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken.getToken())
                .expirationDate(LocalDateTime.now().plusMinutes(refreshTokenExpireMinutes))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
        log.debug("RefreshToken 저장 완료: userId={}", user.getUserId());
        return refreshToken;
    }

    private boolean isValidRefreshToken(String tokenValue) {
        return tokenValue != null && !tokenValue.isBlank() && tokenProvider.validateToken(tokenValue);
    }

    private Optional<User> findUserByRefreshToken(String tokenValue) {
        return refreshTokenRepository.findByRefreshToken(tokenValue)
                .filter(rt -> rt.getExpirationDate().isAfter(LocalDateTime.now()))
                .map(RefreshToken::getUser);
    }

}
