package com.kkori.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoOAuth2Controller {

    @GetMapping("/oauth2/callback/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code,
                                           HttpServletRequest request) {
        if (code == null) {
            return ResponseEntity.badRequest().build();
        }

        request.setAttribute("authorizationCode", code);
        return ResponseEntity.ok().build();
    }

}
