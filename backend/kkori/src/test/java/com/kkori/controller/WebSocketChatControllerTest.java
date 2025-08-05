package com.kkori.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.interview.request.RoomCreateRequest;
import com.kkori.dto.interview.response.RoomCreateResponse;
import com.kkori.dto.websocket.WebSocketChatMessage;
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
public class WebSocketChatControllerTest {

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
    private final String TEST_CHAT_MESSAGE = "테스트 메시지123abc";

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

        // 실제 JWT 쿠키와 함께 WebSocket 연결
        stompSession_user1 = testHelper.createRealTestSession(port, jwt_user1, TEST_USER_ID_1);
        personalSubscriber_user1 = testHelper.subscribeToRealPersonalQueue(stompSession_user1, TEST_USER_ID_1);

        stompSession_user2 = testHelper.createRealTestSession(port, jwt_user2, TEST_USER_ID_2);
        personalSubscriber_user2 = testHelper.subscribeToRealPersonalQueue(stompSession_user2, TEST_USER_ID_2);
    }

    @AfterEach
    void tearDown() throws Exception {
        System.out.println("🔧 테스트 정리 시작...");

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
    @DisplayName("채팅을 보내면 자신과 상대방이 동일한 내용을 받는다")
    void sendChat() throws Exception {
        // given

        given(interviewSessionService.createPairRoom(1L, TEST_USER_ID_2))
                .willReturn(TEST_ROOM_ID);

        RoomCreateRequest roomCreateRequest = new RoomCreateRequest("PAIR_INTERVIEW", 1l);
        stompSession_user2.send("/app/room-create", roomCreateRequest);

        Map<String, Object> response = personalSubscriber_user2.waitForMessage("room-created", 3);

        Map<String, Object> responsePayload = (Map<String, Object>) response.get("data");
        RoomCreateResponse roomCreateResponse = objectMapper.convertValue(responsePayload, RoomCreateResponse.class);
        String roomId = roomCreateResponse.getRoomId();

        WebSocketTestHelper.MessageSubscriber chatSubscriber_user1 = testHelper.subscribeToRealRoomTopic(
                stompSession_user1, roomId);
        WebSocketTestHelper.MessageSubscriber chatSubscriber_user2 = testHelper.subscribeToRealRoomTopic(
                stompSession_user2, roomId);

        // when
        WebSocketChatMessage chatMessageSend = new WebSocketChatMessage(
                roomId, user1.getNickname(), TEST_CHAT_MESSAGE, System.currentTimeMillis()
        );
        stompSession_user1.send("/app/chat", chatMessageSend);

        // then
        Map<String, Object> chatResponse_user1 = chatSubscriber_user1.waitForMessage("chat", 3);
        Map<String, Object> chatResponse_user2 = chatSubscriber_user2.waitForMessage("chat", 3);
        WebSocketChatMessage chatMessage_user1 = objectMapper.convertValue(chatResponse_user1.get("data"),
                WebSocketChatMessage.class);
        WebSocketChatMessage chatMessage_user2 = objectMapper.convertValue(chatResponse_user2.get("data"),
                WebSocketChatMessage.class);
        assertThat(chatMessage_user1.content()).isEqualTo(TEST_CHAT_MESSAGE);
        assertThat(chatMessage_user2.content()).isEqualTo(TEST_CHAT_MESSAGE);
    }
}
