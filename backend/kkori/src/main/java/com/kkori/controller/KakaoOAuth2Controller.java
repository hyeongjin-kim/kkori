package com.kkori.controller;

import com.kkori.service.KakaoOAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/authorization/kakao")
public class KakaoOAuth2Controller {

    private final KakaoOAuth2Service kakaoOAuth2Service;

}
