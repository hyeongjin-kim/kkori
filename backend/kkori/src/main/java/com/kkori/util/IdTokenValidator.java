package com.kkori.util;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class IdTokenValidator {

    private static final String ISSUER = "https://kauth.kakao.com";

    /**
     * id_token 안에서 sub (고유 사용자 ID) 추출
     *
     * @param idToken JWT 형식의 id_token 문자열
     * @return sub 값 (사용자 고유 ID)
     * @throws RuntimeException 추출 실패 시 예외 발생
     */
    public static String getSub(String idToken) {
        try {
            return parseClaims(idToken).getSubject();
        } catch (Exception e) {
            throw new RuntimeException("id_token에서 sub를 추출하지 못했습니다", e);
        }
    }

    /**
     * OIDC 필수 클레임(iss, aud, exp, iat, nonce) 완전 검증
     *
     * @param idToken          검증할 JWT id_token
     * @param session          HttpSession (nonce 확인용)
     * @param expectedClientId 클라이언트 ID (aud 검증용)
     * @return 모든 필수 클레임이 정상일 경우 true, 아니면 false
     */
    public static boolean validateIdTokenClaims(String idToken, HttpSession session, String expectedClientId) {
        try {
            JWTClaimsSet claims = parseClaims(idToken);

            if (!isNonceValid(claims, session)) {
                return false;
            }

            if (!isIssuerValid(claims)) {
                return false;
            }

            if (!isAudienceValid(claims, expectedClientId)) {
                return false;
            }

            if (!isExpirationValid(claims)) {
                return false;
            }

            if (!isIssuedAtValid(claims)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static JWTClaimsSet parseClaims(String idToken) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(idToken);
        return signedJWT.getJWTClaimsSet();
    }

    private static boolean isNonceValid(JWTClaimsSet claims, HttpSession session) throws ParseException {
        String nonceInIdToken = claims.getStringClaim("nonce");
        String storedNonce = NonceUtil.getNonce(session);
        return Objects.equals(storedNonce, nonceInIdToken);
    }

    private static boolean isIssuerValid(JWTClaimsSet claims) {
        return ISSUER.equals(claims.getIssuer());
    }

    private static boolean isAudienceValid(JWTClaimsSet claims, String expectedClientId) {
        Object audClaim = claims.getClaim("aud");

        if (audClaim instanceof String) {
            return expectedClientId.equals(audClaim);
        }

        if (audClaim instanceof List<?>) {
            return ((List<?>) audClaim).contains(expectedClientId);
        }

        // aud 클레임 타입 불일치
        return false;
    }

    private static boolean isExpirationValid(JWTClaimsSet claims) {
        Date exp = claims.getExpirationTime();
        return exp != null && new Date().before(exp);
    }

    private static boolean isIssuedAtValid(JWTClaimsSet claims) {
        Date iat = claims.getIssueTime();
        return iat != null && !iat.after(new Date());
    }

}
