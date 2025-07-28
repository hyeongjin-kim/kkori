package com.kkori.util;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

public class IdTokenValidator {

    private static final String NONCE_KEY = "oauth2_kakao_nonce";

    /**
     * id_token 안에서 sub (고유 사용자 ID) 추출
     *
     * @param idToken JWT 형식의 id_token 문자열
     * @return sub 값 (사용자 고유 ID)
     * @throws RuntimeException 추출 실패 시 예외 발생
     */
    public static String getSub(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("id_token에서 sub를 추출하지 못했습니다", e);
        }
    }

    /**
     * OIDC 필수 클레임(iss, aud, exp, iat, nonce) 완전 검증 메서드 추가
     *
     * @param idToken          검증할 JWT id_token
     * @param session          HttpSession (nonce 확인용)
     * @param expectedClientId 클라이언트 ID (aud 검증용)
     * @return 모든 필수 클레임이 정상일 경우 true, 아니면 false
     */
    public static boolean validateIdTokenClaims(String idToken, HttpSession session, String expectedClientId) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // nonce 검증
            String nonceInIdToken = claims.getStringClaim("nonce");
            String storedNonce = NonceUtil.getNonce(session);
            if (storedNonce == null || !storedNonce.equals(nonceInIdToken)) {
                return false;
            }

            // iss(issuer) 검증
            String issuer = claims.getIssuer();
            if (!"https://kauth.kakao.com".equals(issuer)) {
                return false;
            }

            // aud(audience) 검증
            Object audClaim = claims.getClaim("aud");
            if (audClaim instanceof String) {
                if (!expectedClientId.equals(audClaim)) {
                    return false;
                }
            } else if (audClaim instanceof List<?>) {
                if (!((List<?>) audClaim).contains(expectedClientId)) {
                    return false;
                }
            } else {
                return false; // aud 클레임 타입 불일치
            }

            // exp(expiration time) 검증
            Date exp = claims.getExpirationTime();
            if (exp == null || new Date().after(exp)) {
                return false;
            }

            // iat(issued at) 검증 (optional, 이보다 미래일 경우 false 처리)
            Date iat = claims.getIssueTime();
            return iat != null && !iat.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

}
