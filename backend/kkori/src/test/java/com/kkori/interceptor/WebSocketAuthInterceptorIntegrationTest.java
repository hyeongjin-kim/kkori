package com.kkori.interceptor;

import com.kkori.jwt.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret-key=test-secret-key-for-integration-test",
        "jwt.ACCESS_TOKEN_MINUTE_TIME=60",
        "jwt.REFRESH_TOKEN_MINUTE_TIME=10080"
})
@DisplayName("WebSocket 인증 인터셉터 통합 테스트")
class WebSocketAuthInterceptorIntegrationTest {

    @Autowired
    private WebSocketAuthInterceptor interceptor;

    @MockBean
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("Spring Context에서 인터셉터가 정상적으로 주입됨")
    void contextLoads() {
        assertThat(interceptor).isNotNull();
    }
}