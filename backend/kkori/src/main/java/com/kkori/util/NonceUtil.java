package com.kkori.util;

import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public class NonceUtil {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    private static final String NONCE_KEY = "oauth2_kakao_nonce";

    // nonce 생성
    public static String generateNonce() {
        byte[] nonceBytes = new byte[24];
        secureRandom.nextBytes(nonceBytes);
        return base64Encoder.encodeToString(nonceBytes);
    }

    // 세션에 nonce 저장
    public static void saveNonce(HttpSession session, String nonce) {
        session.setAttribute(NONCE_KEY, nonce);
    }

    // 세션에 저장된 nonce 조회
    public static String getNonce(HttpSession session) {
        Object nonce = session.getAttribute(NONCE_KEY);
        return nonce != null ? nonce.toString() : null;
    }

}
