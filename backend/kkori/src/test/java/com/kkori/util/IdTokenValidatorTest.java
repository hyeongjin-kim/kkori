package com.kkori.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class IdTokenValidatorTest {

    private static final byte[] SHARED_SECRET = "01234567890123456789012345678901".getBytes();

    private HttpSession session;
    private final String expectedClientId = "test-client-id";

    @BeforeEach
    void setUp() {
        session = Mockito.mock(HttpSession.class);
    }

    /**
     * 서명된 유효한 id_token 생성 (HS256 서명 포함)
     */
    private String createValidIdToken(String nonce, String aud, Date exp, Date iat) throws Exception {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .issuer("https://kauth.kakao.com")
                .subject("user-sub-123")
                .claim("nonce", nonce)
                .audience(aud)
                .expirationTime(exp)
                .issueTime(iat);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), builder.build());

        // HS256 비밀키로 서명 수행
        signedJWT.sign(new MACSigner(SHARED_SECRET));

        return signedJWT.serialize();
    }

    @Test
    void validateIdTokenClaims_validToken_returnsTrue() throws Exception {
        String nonce = "testnonce";
        Mockito.when(session.getAttribute("oauth2_kakao_nonce")).thenReturn(nonce);

        Date now = new Date();
        Date exp = new Date(now.getTime() + 60 * 60 * 1000); // 1시간 후 유효
        Date iat = now;

        String idToken = createValidIdToken(nonce, expectedClientId, exp, iat);

        assertTrue(IdTokenValidator.validateIdTokenClaims(idToken, session, expectedClientId));
    }

    @Test
    void validateIdTokenClaims_invalidNonce_returnsFalse() throws Exception {
        Mockito.when(session.getAttribute("oauth2_kakao_nonce")).thenReturn("different-nonce");

        String idToken = createValidIdToken("testnonce", expectedClientId,
                new Date(System.currentTimeMillis() + 60000), new Date());

        assertFalse(IdTokenValidator.validateIdTokenClaims(idToken, session, expectedClientId));
    }

    @Test
    void validateIdTokenClaims_issuerMismatch_returnsFalse() throws Exception {
        Mockito.when(session.getAttribute("oauth2_kakao_nonce")).thenReturn("testnonce");

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("https://wrong-issuer.com")
                .subject("user-sub")
                .claim("nonce", "testnonce")
                .audience(expectedClientId)
                .expirationTime(new Date(System.currentTimeMillis() + 60000))
                .issueTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(new MACSigner(SHARED_SECRET));
        String idToken = signedJWT.serialize();

        assertFalse(IdTokenValidator.validateIdTokenClaims(idToken, session, expectedClientId));
    }

    @Test
    void validateIdTokenClaims_audienceMismatch_returnsFalse() throws Exception {
        Mockito.when(session.getAttribute("oauth2_kakao_nonce")).thenReturn("testnonce");

        String idToken = createValidIdToken("testnonce", "other-client-id",
                new Date(System.currentTimeMillis() + 60000), new Date());

        assertFalse(IdTokenValidator.validateIdTokenClaims(idToken, session, expectedClientId));
    }

    @Test
    void validateIdTokenClaims_expiredToken_returnsFalse() throws Exception {
        Mockito.when(session.getAttribute("oauth2_kakao_nonce")).thenReturn("testnonce");

        Date expired = new Date(System.currentTimeMillis() - 1000);
        String idToken = createValidIdToken("testnonce", expectedClientId, expired,
                new Date(System.currentTimeMillis() - 60000));

        assertFalse(IdTokenValidator.validateIdTokenClaims(idToken, session, expectedClientId));
    }

    @Test
    void validateIdTokenClaims_iatInFuture_returnsFalse() throws Exception {
        Mockito.when(session.getAttribute("oauth2_kakao_nonce")).thenReturn("testnonce");

        Date futureIat = new Date(System.currentTimeMillis() + 60 * 1000); // 1분 미래
        Date futureExp = new Date(System.currentTimeMillis() + 60000 * 2);  // 2분 미래
        String idToken = createValidIdToken("testnonce", expectedClientId, futureExp, futureIat);

        assertFalse(IdTokenValidator.validateIdTokenClaims(idToken, session, expectedClientId));
    }

    @Test
    void getSub_validToken_returnsCorrectSub() throws Exception {
        String nonce = "testnonce";
        Mockito.when(session.getAttribute("oauth2_kakao_nonce")).thenReturn(nonce);

        Date now = new Date();
        Date exp = new Date(now.getTime() + 60000);

        String idToken = createValidIdToken(nonce, expectedClientId, exp, now);
        String sub = IdTokenValidator.getSub(idToken);

        assertEquals("user-sub-123", sub);
    }

    @Test
    void getSub_invalidToken_throwsRuntimeException() {
        String invalidToken = "not-a-valid-token";
        assertThrows(RuntimeException.class, () -> IdTokenValidator.getSub(invalidToken));
    }

}
