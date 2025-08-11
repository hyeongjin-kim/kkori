package com.kkori.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.kkori.dto.response.LoginResponse;
import com.kkori.entity.RefreshToken;
import com.kkori.entity.User;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.repository.RefreshTokenRepository;
import com.kkori.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuestLoginServiceTest {

    private static final String GUEST_PREFIX = "guest_";
    private static final String GUEST_NICKNAME = "신비한코알라";
    private static final String ACCESS_TOKEN_VALUE = "guest-jwt-access-token";
    private static final String REFRESH_TOKEN_VALUE = "guest-jwt-refresh-token";
    private static final String INVALID_TOKEN_VALUE = "invalid-refresh-token";
    private static final Long GUEST_USER_ID = 1L;

    @InjectMocks
    private GuestLoginServiceImpl guestLoginService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private final Token accessToken = new Token(ACCESS_TOKEN_VALUE);
    private final Token refreshToken = new Token(REFRESH_TOKEN_VALUE);

    private User createGuestUser(String sub, String nickname) {
        return User.builder()
                .userId(GuestLoginServiceTest.GUEST_USER_ID)
                .sub(sub)
                .nickname(nickname)
                .deleted(false)
                .build();
    }

    private RefreshToken createRefreshToken(User user, Token token) {
        return RefreshToken.builder()
                .user(user)
                .refreshToken(token.getToken())
                .expirationDate(LocalDateTime.now().plusDays(7))
                .build();
    }

    @Test
    @DisplayName("게스트 사용자 생성 및 로그인 성공")
    void createGuestUserAndLogin_Success() {
        // Given
        User savedGuestUser = createGuestUser(GUEST_PREFIX + "uuid-123", GUEST_NICKNAME);
        given(userRepository.save(any(User.class))).willReturn(savedGuestUser);
        given(tokenProvider.generateRefreshToken(any(User.class))).willReturn(refreshToken);
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willReturn(createRefreshToken(savedGuestUser, refreshToken));

        // When
        LoginResponse result = guestLoginService.createGuestUserAndLogin();

        // Then
        assertNotNull(result);
        assertEquals(REFRESH_TOKEN_VALUE, result.getRefreshToken().getToken());
        assertEquals(GUEST_NICKNAME, result.getNickname());

        // 사용자 저장 검증
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getSub()).startsWith(GUEST_PREFIX);
        assertThat(capturedUser.getNickname()).isNotEmpty();
        assertThat(capturedUser.isDeleted()).isFalse();

        // 토큰 생성 및 저장 검증
        verify(tokenProvider, times(1)).generateRefreshToken(any(User.class));
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("게스트 닉네임이 랜덤으로 생성되는지 검증")
    void createGuestUserAndLogin_GeneratesRandomNickname() {
        // Given
        String randomNickname = "활기찬펭귄";
        User savedGuestUser = createGuestUser(GUEST_PREFIX + "uuid-456", randomNickname);
        given(userRepository.save(any(User.class))).willReturn(savedGuestUser);
        given(tokenProvider.generateRefreshToken(any(User.class))).willReturn(refreshToken);
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willReturn(createRefreshToken(savedGuestUser, refreshToken));

        // When
        LoginResponse result = guestLoginService.createGuestUserAndLogin();

        // Then
        assertThat(result.getNickname()).isEqualTo(randomNickname);
        assertThat(result.getNickname()).isNotEmpty();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getNickname()).isNotEmpty();
    }

    @Test
    @DisplayName("게스트 사용자 sub 필드가 guest_ prefix로 시작하는지 검증")
    void createGuestUserAndLogin_SubStartsWithGuestPrefix() {
        // Given
        User savedGuestUser = createGuestUser(GUEST_PREFIX + "uuid-789", GUEST_NICKNAME);
        given(userRepository.save(any(User.class))).willReturn(savedGuestUser);
        given(tokenProvider.generateRefreshToken(any(User.class))).willReturn(refreshToken);
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willReturn(createRefreshToken(savedGuestUser, refreshToken));

        // When
        guestLoginService.createGuestUserAndLogin();

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getSub()).startsWith(GUEST_PREFIX);
        assertThat(capturedUser.getSub()).hasSize(42); // "guest_" + UUID(36자) = 42자
    }

    @Test
    @DisplayName("사용자 저장 실패 시 예외 발생")
    void createGuestUserAndLogin_WhenUserSaveFails_ThrowsException() {
        // Given
        given(userRepository.save(any(User.class)))
                .willThrow(new RuntimeException("데이터베이스 연결 실패"));

        // When & Then
        assertThatThrownBy(() -> guestLoginService.createGuestUserAndLogin())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 실패");

        verify(tokenProvider, times(0)).generateRefreshToken(any(User.class));
        verify(refreshTokenRepository, times(0)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("JWT 토큰 생성 실패 시 예외 발생")
    void createGuestUserAndLogin_WhenTokenGenerationFails_ThrowsException() {
        // Given
        User savedGuestUser = createGuestUser(GUEST_PREFIX + "uuid-error", GUEST_NICKNAME);
        given(userRepository.save(any(User.class))).willReturn(savedGuestUser);
        given(tokenProvider.generateRefreshToken(any(User.class)))
                .willThrow(new IllegalStateException("JWT 토큰 생성 실패"));

        // When & Then
        assertThatThrownBy(() -> guestLoginService.createGuestUserAndLogin())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT 토큰 생성 실패");

        verify(refreshTokenRepository, times(0)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("RefreshToken 저장 실패 시 예외 발생")
    void createGuestUserAndLogin_WhenRefreshTokenSaveFails_ThrowsException() {
        // Given
        User savedGuestUser = createGuestUser(GUEST_PREFIX + "uuid-token-error", GUEST_NICKNAME);
        given(userRepository.save(any(User.class))).willReturn(savedGuestUser);
        given(tokenProvider.generateRefreshToken(any(User.class))).willReturn(refreshToken);
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willThrow(new RuntimeException("RefreshToken 저장 실패"));

        // When & Then
        assertThatThrownBy(() -> guestLoginService.createGuestUserAndLogin())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("RefreshToken 저장 실패");
    }

    @Test
    @DisplayName("유효한 Refresh Token으로 Access Token 발급 성공")
    void issueAccessToken_WithValidRefreshToken_Success() {
        // Given
        given(tokenProvider.validateToken(REFRESH_TOKEN_VALUE)).willReturn(true);
        
        User guestUser = createGuestUser(GUEST_PREFIX + "uuid-access", GUEST_NICKNAME);
        RefreshToken refreshTokenEntity = createRefreshToken(guestUser, refreshToken);
        
        given(refreshTokenRepository.findByRefreshToken(REFRESH_TOKEN_VALUE))
                .willReturn(Optional.of(refreshTokenEntity));
        given(tokenProvider.generateAccessToken(guestUser)).willReturn(accessToken);

        // When
        Token result = guestLoginService.issueAccessToken(REFRESH_TOKEN_VALUE);

        // Then
        assertNotNull(result);
        assertEquals(ACCESS_TOKEN_VALUE, result.getToken());
        
        verify(tokenProvider, times(1)).validateToken(REFRESH_TOKEN_VALUE);
        verify(refreshTokenRepository, times(1)).findByRefreshToken(REFRESH_TOKEN_VALUE);
        verify(tokenProvider, times(1)).generateAccessToken(guestUser);
    }

    @Test
    @DisplayName("유효하지 않은 Refresh Token으로 Access Token 발급 실패")
    void issueAccessToken_WithInvalidRefreshToken_ReturnsNull() {
        // Given
        given(tokenProvider.validateToken(INVALID_TOKEN_VALUE)).willReturn(false);

        // When
        Token result = guestLoginService.issueAccessToken(INVALID_TOKEN_VALUE);

        // Then
        assertNull(result);
        
        verify(tokenProvider, times(1)).validateToken(INVALID_TOKEN_VALUE);
        verify(tokenProvider, times(0)).getUserIdFromToken(anyString());
        verify(userRepository, times(0)).findById(any());
        verify(tokenProvider, times(0)).generateAccessToken(any(User.class));
    }

    @Test
    @DisplayName("null Refresh Token으로 Access Token 발급 실패")
    void issueAccessToken_WithNullRefreshToken_ReturnsNull() {
        // When
        Token result = guestLoginService.issueAccessToken(null);

        // Then
        assertNull(result);
        
        verify(tokenProvider, times(0)).validateToken(anyString());
    }

    @Test
    @DisplayName("빈 Refresh Token으로 Access Token 발급 실패")
    void issueAccessToken_WithEmptyRefreshToken_ReturnsNull() {
        // When
        Token result = guestLoginService.issueAccessToken("");

        // Then
        assertNull(result);
        
        verify(tokenProvider, times(0)).validateToken(anyString());
    }

    @Test
    @DisplayName("RefreshToken이 DB에 존재하지 않을 때 Access Token 발급 실패")
    void issueAccessToken_WhenRefreshTokenNotExists_ReturnsNull() {
        // Given
        given(tokenProvider.validateToken(REFRESH_TOKEN_VALUE)).willReturn(true);
        given(refreshTokenRepository.findByRefreshToken(REFRESH_TOKEN_VALUE))
                .willReturn(Optional.empty());

        // When
        Token result = guestLoginService.issueAccessToken(REFRESH_TOKEN_VALUE);

        // Then
        assertNull(result);
        
        verify(tokenProvider, times(1)).validateToken(REFRESH_TOKEN_VALUE);
        verify(refreshTokenRepository, times(1)).findByRefreshToken(REFRESH_TOKEN_VALUE);
        verify(tokenProvider, times(0)).generateAccessToken(any(User.class));
    }

}