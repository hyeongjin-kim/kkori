package com.kkori.controller.interview;

import static org.assertj.core.api.Assertions.assertThat;

import com.kkori.dto.websocket.SignalingMessage;
import com.kkori.entity.User;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.service.InterviewSessionService;
import com.kkori.test.helper.WebSocketTestHelper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
@DisplayName("WebRTC 시그널링 테스트")
public class WebRTCControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private InterviewSessionService interviewSessionService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private WebSocketTestHelper testHelper;

    private StompSession stompSession1, stompSession2;
    private WebSocketTestHelper.MessageSubscriber personalSubscriber1, personalSubscriber2;

    private final Long USER_ID_1 = 123L;
    private final Long USER_ID_2 = 456L;
    private final String ROOM_ID = "TEST_ROOM_123";
    private String jwtToken1, jwtToken2;

    @BeforeEach
    void setUp() throws Exception {
        User user1 = User.builder()
                .userId(USER_ID_1)
                .sub("test-kakao-123456")
                .nickname("사용자1")
                .deleted(false)
                .build();

        User user2 = User.builder()
                .userId(USER_ID_2)
                .sub("test-kakao-789101")
                .nickname("사용자2")
                .deleted(false)
                .build();

        Token token1 = tokenProvider.generateAccessToken(user1);
        Token token2 = tokenProvider.generateAccessToken(user2);
        jwtToken1 = token1.getToken();
        jwtToken2 = token2.getToken();

        stompSession1 = testHelper.createRealTestSession(port, jwtToken1, USER_ID_1);
        personalSubscriber1 = testHelper.subscribeToRealPersonalQueue(stompSession1, USER_ID_1);

        stompSession2 = testHelper.createRealTestSession(port, jwtToken2, USER_ID_2);
        personalSubscriber2 = testHelper.subscribeToRealPersonalQueue(stompSession2, USER_ID_2);
    }

    @AfterEach
    void tearDown() throws Exception {
        cleanupWebRTCTest();
    }

    @Test
    @DisplayName("WebRTC Offer 전송 테스트")
    void sendOffer() throws Exception {
        SignalingMessage offer = new SignalingMessage(
                "offer", ROOM_ID, USER_ID_2, USER_ID_1, "v=0\r\no=- 123 456 IN IP4 127.0.0.1\r\n..."
        );

        stompSession2.send("/app/create-offer", offer);

        Map<String, Object> received = personalSubscriber1.waitForMessage("received-offer", 10);
        assertThat(received).isNotNull();
        assertThat(received.get("type")).isEqualTo("received-offer");
        assertThat(((Number) received.get("senderId")).longValue()).isEqualTo(USER_ID_2);
        assertThat(((Number) received.get("receiverId")).longValue()).isEqualTo(USER_ID_1);
    }

    @Test
    @DisplayName("WebRTC Answer 전송 테스트")
    void sendAnswer() throws Exception {
        SignalingMessage answer = new SignalingMessage(
                "answer", ROOM_ID, USER_ID_1, USER_ID_2, "v=0\r\no=- 789 012 IN IP4 127.0.0.1\r\n..."
        );

        stompSession1.send("/app/create-answer", answer);

        Map<String, Object> received = personalSubscriber2.waitForMessage("received-answer", 10);
        assertThat(received).isNotNull();
        assertThat(received.get("type")).isEqualTo("received-answer");
        assertThat(((Number) received.get("senderId")).longValue()).isEqualTo(USER_ID_1);
        assertThat(((Number) received.get("receiverId")).longValue()).isEqualTo(USER_ID_2);
    }

    // ==================== 헬퍼 메서드들 ====================

    private List<WebSocketTestHelper.MessageSubscriber> subscribeUsersToRoom(List<StompSession> sessions,
                                                                             String roomId) {
        return sessions.stream()
                .map(session -> {
                    try {
                        return testHelper.subscribeToRealRoomTopic(session, roomId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private void unsubscribeSafe(WebSocketTestHelper.MessageSubscriber subscriber) {
        if (subscriber != null) {
            subscriber.unsubscribe();
        }
    }

    private void disconnectSafe(StompSession session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    private void cleanupWebRTCTest() throws Exception {
        // 구독 해제
        unsubscribeSafe(personalSubscriber1);
        unsubscribeSafe(personalSubscriber2);

        Thread.sleep(500);

        // 세션 종료
        disconnectSafe(stompSession1);
        disconnectSafe(stompSession2);
    }
}
