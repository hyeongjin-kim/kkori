package com.kkori.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.SignalingMessage;
import com.kkori.entity.User;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.service.InterviewSessionService;
import com.kkori.test.helper.WebSocketTestHelper;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Signaling 테스트")
public class WebRTCControllerTest {
    @LocalServerPort
    private int port;

    @MockBean
    private InterviewSessionService interviewSessionService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private WebSocketTestHelper testHelper;

    private StompSession stompSession_user1, stompSession_user2;
    private ObjectMapper objectMapper;
    private WebSocketTestHelper.MessageSubscriber personalSubscriber_user1, personalSubscriber_user2;

    private final Long TEST_USER_ID_1 = 123L;
    private final Long TEST_USER_ID_2 = 456L;
    private final String TEST_ROOM_ID = "REAL_ROOM_456";
    private String jwt_user1, jwt_user2;
    private User user1, user2;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();

        // 테스트용 User 객체 생성
        user1 = User.builder()
                .userId(TEST_USER_ID_1)
                .sub("test-kakao-123456")
                .nickname("테스트사용자1")
                .deleted(false)
                .build();

        user2 = User.builder()
                .userId(TEST_USER_ID_2)
                .sub("test-kakao-789101")
                .nickname("테스트사용자2")
                .deleted(false)
                .build();

        // User 객체로 JWT 토큰 생성
        Token user1_token = tokenProvider.generateAccessToken(user1);
        Token user2_token = tokenProvider.generateAccessToken(user2);
        jwt_user1 = user1_token.getToken();
        jwt_user2 = user2_token.getToken();
        System.out.println("🔑 생성된 JWT 토큰: " + jwt_user1.substring(0, Math.min(30, jwt_user1.length())) + "...");
        System.out.println("🔍 토큰에서 사용자 ID 확인: " + tokenProvider.getUserIdFromToken(jwt_user1));
        System.out.println("🔍 토큰 유효성 확인: " + tokenProvider.validateToken(jwt_user1));

        // 실제 JWT 쿠키와 함께 WebSocket 연결
        stompSession_user1 = testHelper.createRealTestSession(port, jwt_user1, TEST_USER_ID_1);
        personalSubscriber_user1 = testHelper.subscribeToRealPersonalQueue(stompSession_user1, TEST_USER_ID_1);

        stompSession_user2 = testHelper.createRealTestSession(port, jwt_user2, TEST_USER_ID_2);
        personalSubscriber_user2 = testHelper.subscribeToRealPersonalQueue(stompSession_user2, TEST_USER_ID_2);

        System.out.println("=== 실제 JWT 기반 테스트 준비 완료 ===");
    }

    @AfterEach
    void tearDown() throws Exception {
        System.out.println("🔧 실제 테스트 정리 시작...");

        if (personalSubscriber_user1 != null) {
            personalSubscriber_user1.unsubscribe();
        }
        if (personalSubscriber_user2 != null) {
            personalSubscriber_user2.unsubscribe();
        }

        Thread.sleep(1000);

        if (stompSession_user1 != null && stompSession_user1.isConnected()) {
            stompSession_user1.disconnect();
            System.out.println("🔌 user1 WebSocket 연결 해제됨");
        }
        if (stompSession_user2 != null && stompSession_user2.isConnected()) {
            stompSession_user2.disconnect();
            System.out.println("🔌 user2 WebSocket 연결 해제됨");
        }

        System.out.println("✅ 실제 테스트 정리 완료");
    }

    @Test
    void sendOffer() throws Exception {
        // given
        SignalingMessage offer = new SignalingMessage(
                "offer", TEST_ROOM_ID, TEST_USER_ID_2, TEST_USER_ID_1, "v=0\r\no=- 123 456 IN IP4 127.0.0.1\r\n...",
                null
        );

        // when
        stompSession_user2.send("/app/offer", offer);

        // then
        Map<String, Object> received = personalSubscriber_user1.waitForMessage("offer", 10);
        assertThat(received).isNotNull();

        assertThat(received.get("type")).isEqualTo("offer");
        assertThat(((Number) received.get("senderId")).longValue()).isEqualTo(TEST_USER_ID_2);
        assertThat(((Number) received.get("receiverId")).longValue()).isEqualTo(TEST_USER_ID_1);
    }

    @Test
    void sendAnswer() throws Exception {
        // given
        SignalingMessage answer = new SignalingMessage(
                "answer", TEST_ROOM_ID, TEST_USER_ID_1, TEST_USER_ID_2, "v=0\r\no=- 789 012 IN IP4 127.0.0.1\r\n...",
                null
        );

        // when
        stompSession_user1.send("/app/answer", answer);

        // then
        Map<String, Object> received = personalSubscriber_user2.waitForMessage("answer", 10);
        assertThat(received).isNotNull();

        assertThat(received.get("type")).isEqualTo("answer");
        assertThat(((Number) received.get("senderId")).longValue()).isEqualTo(TEST_USER_ID_1);
        assertThat(((Number) received.get("receiverId")).longValue()).isEqualTo(TEST_USER_ID_2);
    }
}
