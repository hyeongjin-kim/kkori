package com.kkori.oauth2;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = KakaoOAuth2AuthenticationTest.class)
public class KakaoOAuth2AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("카카오 인증 리다이렉트로 전달된 인가 코드를 서버가 쿼리 파라미터에서 추출한다")
    void whenKakaoCallbackWithCode_thenExtractsAuthorizationCode() throws Exception {

        String code = "auth_code";

        mockMvc.perform(get("/oauth2/callback/kakao").param("code", code))
                .andExpect(status().isOk())
                .andExpect(request().attribute("authorizationCode", code));

    }

    @Test
    @DisplayName("카카오 인증 리다이렉트에 인가코드가 포함되어 있지 않은 경우, 400 Bad Request를 반환한다.")
    void whenKakaoCallbackWithoutCode_thenBadRequest() throws Exception {

        mockMvc.perform(get("/oauth2/callback/kakao"))
                .andExpect(status().isBadRequest());
    }

}
