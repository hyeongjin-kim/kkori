package com.kkori.controller.interview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.interview.request.CommonRoomRequest;
import com.kkori.dto.interview.request.RoomCreateRequest;
import com.kkori.dto.interview.response.RoomCreateResponse;
import com.kkori.dto.websocket.WebSocketChatMessage;
import com.kkori.entity.User;
import com.kkori.exception.interview.InterviewRoomException;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.service.InterviewSessionService;
import com.kkori.test.helper.WebSocketTestHelper;
import com.kkori.test.helper.WebSocketTestHelper.MessageSubscriber;
import java.util.List;
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
    private final String TEST_ROOM_ID_1 = "REAL_ROOM_456";
    private final String TEST_ROOM_ID_2 = "REAL_ROOM_789";
    private String jwt_user1, jwt_user2;
    private User user1, user2;
    private final String TEST_CHAT_MESSAGE = "테스트 메시지123abc";
    private final String TEST_CHAT_MESSAGE_FAIL = "실패할 테스트 메시지123abc";

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
    @DisplayName("소속된 방에 채팅을 보내면 자신과 상대방이 동일한 내용을 받는다")
    void sendChat() throws Exception {
        // given
        given(interviewSessionService.createPairRoom(1L, TEST_USER_ID_2))
                .willReturn(TEST_ROOM_ID_1);
        doNothing().when(interviewSessionService).canSendChatMessage(any(), any());

        String roomId = createRoomAndGetId(stompSession_user2, personalSubscriber_user2, "PAIR_INTERVIEW", 1l);

        CommonRoomRequest roomJoinRequest = new CommonRoomRequest(roomId);
        stompSession_user2.send("/app/room-join", roomJoinRequest);

        List<WebSocketTestHelper.MessageSubscriber> chatSubscriberList = subscribeChat(
                stompSession_user1, roomId,
                stompSession_user2, roomId
        );
        WebSocketTestHelper.MessageSubscriber chatSubscriber_user1 = chatSubscriberList.get(0);
        WebSocketTestHelper.MessageSubscriber chatSubscriber_user2 = chatSubscriberList.get(1);

        // when
        sendChat(user1, stompSession_user1, roomId, TEST_CHAT_MESSAGE);

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

    @Test
    @DisplayName("소속되지 않은 방에 채팅을 보내면 해당 방의 유저는 아무것도 받지 않고, 자신은 에러 메시지를 받는다")
    void sendChatToWrongRoom() throws Exception {
        // given
        given(interviewSessionService.createPairRoom(1L, TEST_USER_ID_1))
                .willReturn(TEST_ROOM_ID_1);
        given(interviewSessionService.createPairRoom(1L, TEST_USER_ID_2))
                .willReturn(TEST_ROOM_ID_2);
        doThrow(InterviewRoomException.userNotFoundInRoom()).when(interviewSessionService)
                .canSendChatMessage(eq(TEST_ROOM_ID_2), eq(TEST_USER_ID_1));

        String roomId_1 = createRoomAndGetId(stompSession_user1, personalSubscriber_user1, "PAIR_INTERVIEW", 1l);
        String roomId_2 = createRoomAndGetId(stompSession_user2, personalSubscriber_user2, "PAIR_INTERVIEW", 1l);

        List<WebSocketTestHelper.MessageSubscriber> chatSubscriberList = subscribeChat(
                stompSession_user1, roomId_1,
                stompSession_user2, roomId_2
        );
        WebSocketTestHelper.MessageSubscriber chatSubscriber_user1 = chatSubscriberList.get(0);
        WebSocketTestHelper.MessageSubscriber chatSubscriber_user2 = chatSubscriberList.get(1);

        // when
        sendChat(user1, stompSession_user1, roomId_2, TEST_CHAT_MESSAGE_FAIL);
        sendChat(user2, stompSession_user2, roomId_2, TEST_CHAT_MESSAGE);

        // then
        assertThrows(AssertionError.class, () -> chatSubscriber_user1.waitForMessage("chat", 3));
        Map<String, Object> errorResponse = personalSubscriber_user1.waitForMessage("error", 3);
        Map<String, Object> chatResponse_user2 = chatSubscriber_user2.waitForMessage("chat", 3);
        assertThrows(AssertionError.class, () -> chatSubscriber_user2.waitForMessage("chat", 3));
        WebSocketChatMessage chatMessage_user2 = objectMapper.convertValue(chatResponse_user2.get("data"),
                WebSocketChatMessage.class);
        assertThat(chatMessage_user2.content()).isEqualTo(TEST_CHAT_MESSAGE);
    }

    // ==================== 헬퍼 메서드들 ====================
    private List<MessageSubscriber> subscribeChat(StompSession user1, String roomId_1, StompSession user2,
                                                  String roomId_2) throws Exception {
        WebSocketTestHelper.MessageSubscriber chatSubscriber_user1 = testHelper.subscribeToRealRoomTopic(user1,
                roomId_1);
        WebSocketTestHelper.MessageSubscriber chatSubscriber_user2 = testHelper.subscribeToRealRoomTopic(user2,
                roomId_2);
        return List.of(chatSubscriber_user1, chatSubscriber_user2);
    }

    private String createRoomAndGetId(StompSession creatorSession,
                                      WebSocketTestHelper.MessageSubscriber creatorSubscriber, String mode,
                                      Long questionSetId) throws Exception {

        RoomCreateRequest roomCreateRequest = new RoomCreateRequest(mode, questionSetId);
        creatorSession.send("/app/room-create", roomCreateRequest);

        Map<String, Object> response = creatorSubscriber.waitForMessage("room-created", 3);
        Map<String, Object> responsePayload = (Map<String, Object>) response.get("data");
        RoomCreateResponse roomCreateResponse = objectMapper.convertValue(responsePayload, RoomCreateResponse.class);

        return roomCreateResponse.getRoomId();
    }

    private void sendChat(User user, StompSession senderSession, String roomId, String message) {
        WebSocketChatMessage chatMessage = new WebSocketChatMessage(
                roomId, user.getNickname(), message, System.currentTimeMillis()
        );
        senderSession.send("/app/chat", chatMessage);
    }
}
