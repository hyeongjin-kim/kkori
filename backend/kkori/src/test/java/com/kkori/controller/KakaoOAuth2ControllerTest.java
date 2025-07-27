package com.kkori.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kkori.service.KakaoOAuth2Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(KakaoOAuth2Controller.class)
class KakaoOAuth2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KakaoOAuth2Service kakaoOAuth2Service;

    @Test
    @DisplayName("카카오 API 서버 오류 발생 시 500 Internal Server Error가 반환된다")
    void shouldReturnInternalServerError_whenKakaoApiFails() throws Exception {

        String code = "valid-kakao-code";
        given(kakaoOAuth2Service.loginWithKakao(code))
                .willThrow(new RuntimeException("카카오 서버 장애"));

        mockMvc.perform(get("/oauth2/authorization/kakao/callback")
                        .param("code", code)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }


}