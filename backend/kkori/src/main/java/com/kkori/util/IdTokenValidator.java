package com.kkori.util;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpSession;

public class IdTokenValidator {

    private static final String NONCE_KEY = "oauth2_kakao_nonce";

    /**
     * id_token 안의 nonce와 세션에 저장된 nonce가 일치하는지 검증
     *
     * @param idToken JWT 형식의 id_token 문자열
     * @param session HttpSession (저장된 nonce 조회용)
     * @return nonce가 일치하면 true, 아니면 false
     */
    public static boolean validateNonce(String idToken, HttpSession session) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            String nonceInIdToken = claims.getStringClaim("nonce");
            String storedNonce = NonceUtil.getNonce(session);

            return storedNonce != null && storedNonce.equals(nonceInIdToken);
        } catch (Exception e) {
            // 예외 발생시 false 반환 (필요시 로깅 추가)
            return false;
        }
    }

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

}
