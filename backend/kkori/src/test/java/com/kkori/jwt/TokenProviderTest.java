package com.kkori.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kkori.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TokenProviderTest {

    private static final String JWT_SECRET = "test-jwt-secret-key-test-jwt-secret-key";
    private static final String SUB = "sub";
    private static final String NICKNAME = "nickname";
    private static final String MALFORMED_TOKEN = "thisIsNotAJwt";
    private static final String FAKE_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.KqwwyicjgR53bZj4CDV80lZoZdADoWDd6E9a-2o7ZOg";

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecretKey", JWT_SECRET);
    }

    private User createUser(Long id) {
        User user = new User(SUB, NICKNAME);
        ReflectionTestUtils.setField(user, "userId", id);
        return user;
    }

    @Test
    @DisplayName("JWT 생성 및 파싱 성공")
    void testGenerateAndParseToken() {
        User user = createUser(42L);

        Token token = tokenProvider.generateToken(user, 60);

        assertNotNull(token);
        assertNotNull(token.getToken());

        assertTrue(tokenProvider.validateToken(token.getToken()));

        Long userId = tokenProvider.getUserIdFromToken(token.getToken());
        assertEquals(42L, userId);
    }

    @Test
    @DisplayName("만료된 JWT 검증 시 예외 발생")
    void testExpiredTokenThrowsException() throws InterruptedException {
        User user = createUser(1L);

        Token token = tokenProvider.generateToken(user, 0); // 0분 → 즉시 만료
        Thread.sleep(1000);
        assertThrows(Exception.class, () -> tokenProvider.validateToken(token.getToken()));
    }

    @Test
    @DisplayName("위변조된 JWT 검증시 SignatureException 발생")
    void testInvalidSignature() {
        assertThrows(RuntimeException.class, () -> tokenProvider.validateToken(FAKE_TOKEN));
    }

    @Test
    @DisplayName("Malformed JWT 검증시 예외 발생")
    void testMalformedToken() {
        assertThrows(RuntimeException.class, () -> tokenProvider.validateToken(MALFORMED_TOKEN));
    }

}
