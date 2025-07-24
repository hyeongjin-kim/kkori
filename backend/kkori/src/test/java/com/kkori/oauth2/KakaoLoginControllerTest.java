package com.kkori.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class KakaoLoginControllerTest {
    
    // Spring Web MVC 환경에서 컨트롤러를 테스트할 수 있도록 도와주는 MockMvc 객체
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("Authorization Code를 받는데 성공한다.")
    public void receiveSuccessKakaoAuthorizationCode() throws Exception {
        // given: Authorization Code(쿼리 파라미터)를 받기 위한 요청 준비 조건
        
        // when: GET /oauth/authorize 요청 수행
        
        // then: 응답 코드 302(Found), 상태 검증
        .andExpect(status.isFound());
    }
    
    @Test
    @DisplayName("Authorization Code를 받는데 실패한다.")
    public void receiveFailKakaoAuthorizationCode() throws Exception {
        // given: Authorization Code(쿼리 파라미터)를 받기 위한 요청 준비 조건
        
        // when: GET /oauth/authorize 요청 수행
        
        // then: 응답 코드 400(Bad Request) 에러 메시지 검증
        .andExpect(status().isBadRequest());
    }
}
