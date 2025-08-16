package com.kkori.controller.interview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.component.interview.InterviewRoom;
import com.kkori.component.interview.InterviewStatus;
import com.kkori.component.interview.UserLastEventStore;
import com.kkori.config.validator.WebSocketSecurityValidator;
import com.kkori.dto.interview.request.CommonRoomRequest;
import com.kkori.dto.interview.response.LastStatusResponse;
import com.kkori.dto.interview.response.RoomReconnectionResponse;
import com.kkori.entity.User;
import com.kkori.exception.ExceptionCode;
import com.kkori.exception.interview.InterviewRoomException;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.service.InterviewSessionService;
import com.kkori.service.UserService;
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
@DisplayName("WebSocket 재연결 로직 테스트")
class InterviewWebSocketReconnectionTest {

    @LocalServerPort
    private int port;

    @MockBean
    private InterviewSessionService interviewSessionService;

    @MockBean
    private UserService userService;

    @MockBean
    private WebSocketSecurityValidator webSocketSecurityValidator;

    @MockBean
    private UserLastEventStore userLastEventStore;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private WebSocketTestHelper testHelper;

    private StompSession stompSession;
    private ObjectMapper objectMapper;
    private WebSocketTestHelper.MessageSubscriber personalSubscriber;

    private final Long TEST_USER_ID = 456L;
    private final String TEST_ROOM_ID = "RECONNECT_TEST_ROOM_789";
    private String jwtToken;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();

        // 테스트용 User 객체 생성
        testUser = User.builder()
                .userId(TEST_USER_ID)
                .sub("test-kakao-reconnect-456")
                .nickname("재연결테스트사용자")
                .deleted(false)
                .build();

        // WebSocketSecurityValidator 모킹 - 모든 구독을 허용
        given(webSocketSecurityValidator.isUserInRoom(any(), any())).willReturn(true);

        // User 객체로 JWT 토큰 생성
        Token tokenObject = tokenProvider.generateAccessToken(testUser);
        jwtToken = tokenObject.getToken();

        // WebSocket 연결
        stompSession = testHelper.createRealTestSession(port, jwtToken, TEST_USER_ID);
        personalSubscriber = testHelper.subscribeToRealPersonalQueue(stompSession, TEST_USER_ID);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (personalSubscriber != null) {
            personalSubscriber.unsubscribe();
        }

        Thread.sleep(1000);

        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T extractData(Map<String, Object> response, Class<T> clazz) {
        Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
        return objectMapper.convertValue(dataMap, clazz);
    }

    @Test
    @DisplayName("새로운 탭으로 접근 시 중복 접속 처리 테스트")
    void duplicateTabAccessTest() throws Exception {
        // given - 사용자가 이미 방에 접속해 있는 상황 시뮬레이션
        given(interviewSessionService.isReconnection(TEST_ROOM_ID, TEST_USER_ID)).willReturn(true);
        given(interviewSessionService.getRoomIdByUserId(TEST_USER_ID)).willReturn(TEST_ROOM_ID);
        given(userLastEventStore.getLastInterviewStatus(TEST_USER_ID))
                .willReturn(InterviewStatus.QUESTION_PRESENTED);

        // when - 새 탭에서 방 접속 시도 (roomId가 있는 경우)
        CommonRoomRequest request = new CommonRoomRequest(TEST_ROOM_ID);
        stompSession.send("/app/room-join", request);

        // then - 기존 탭에 disconnect 메시지 전송 확인
        Map<String, Object> disconnectResponse = personalSubscriber.waitForMessage("disconnect", 10);
        assertThat(disconnectResponse).isNotNull();
        assertThat(disconnectResponse.get("type")).isEqualTo("disconnect");
        assertThat(disconnectResponse.get("data")).isEqualTo("다른 곳에서 접속하여 연결을 해제합니다.");

        // and - 재연결 응답 확인
        Map<String, Object> reconnectResponse = personalSubscriber.waitForMessage("room-reconnected", 10);
        assertThat(reconnectResponse).isNotNull();
        assertThat(reconnectResponse.get("type")).isEqualTo("room-reconnected");

        RoomReconnectionResponse roomReconnectionResponse = extractData(reconnectResponse, RoomReconnectionResponse.class);
        assertThat(roomReconnectionResponse.getRoomId()).isEqualTo(TEST_ROOM_ID);

        // and - 마지막 상태 전송 확인
        Map<String, Object> lastStatusResponse = personalSubscriber.waitForMessage("last-status", 10);
        assertThat(lastStatusResponse).isNotNull();
        assertThat(lastStatusResponse.get("type")).isEqualTo("last-status");

        LastStatusResponse statusResponse = extractData(lastStatusResponse, LastStatusResponse.class);
        assertThat(statusResponse.getStatus()).isEqualTo("questionPresented");
    }

    @Test
    @DisplayName("새로고침으로 인한 재접속 테스트 (roomId가 null인 경우)")
    void pageRefreshReconnectionTest() throws Exception {
        // given - 새로고침으로 roomId가 없는 상황
        given(interviewSessionService.getRoomIdByUserId(TEST_USER_ID)).willReturn(TEST_ROOM_ID);
        given(userLastEventStore.getLastInterviewStatus(TEST_USER_ID))
                .willReturn(InterviewStatus.NEXT_QUESTION_PRESENTED);

        // when - roomId 없이 방 접속 시도 (새로고침 상황)
        CommonRoomRequest request = new CommonRoomRequest(null);
        stompSession.send("/app/room-join", request);

        // then - 재연결 응답 확인
        Map<String, Object> reconnectResponse = personalSubscriber.waitForMessage("room-reconnected", 10);
        assertThat(reconnectResponse).isNotNull();
        assertThat(reconnectResponse.get("type")).isEqualTo("room-reconnected");

        RoomReconnectionResponse roomReconnectionResponse = extractData(reconnectResponse, RoomReconnectionResponse.class);
        assertThat(roomReconnectionResponse.getRoomId()).isEqualTo(TEST_ROOM_ID);

        // and - 마지막 상태 전송 확인
        Map<String, Object> lastStatusResponse = personalSubscriber.waitForMessage("last-status", 10);
        assertThat(lastStatusResponse).isNotNull();
        assertThat(lastStatusResponse.get("type")).isEqualTo("last-status");

        LastStatusResponse statusResponse = extractData(lastStatusResponse, LastStatusResponse.class);
        assertThat(statusResponse.getStatus()).isEqualTo("nextQuestionPresented");
    }

    @Test
    @DisplayName("네트워크 장애 후 재접속 테스트 - 마지막 이벤트 복구")
    void networkFailureReconnectionTest() throws Exception {
        // given - 네트워크 장애 후 재접속 상황
        Object lastEvent = Map.of(
                "type", "interview-started",
                "data", Map.of("firstQuestion", Map.of("questionText", "자기소개를 해주세요."))
        );
        
        given(interviewSessionService.getRoomIdByUserId(TEST_USER_ID)).willReturn(TEST_ROOM_ID);
        given(userLastEventStore.getLastEvent(TEST_USER_ID)).willReturn(lastEvent);
        given(userLastEventStore.getLastInterviewStatus(TEST_USER_ID))
                .willReturn(InterviewStatus.QUESTION_PRESENTED);

        // when - 재접속 시도
        CommonRoomRequest request = new CommonRoomRequest(null);
        stompSession.send("/app/room-join", request);

        // then - 재연결 응답 확인
        Map<String, Object> reconnectResponse = personalSubscriber.waitForMessage("room-reconnected", 10);
        assertThat(reconnectResponse).isNotNull();

        // and - 마지막 이벤트 재전송 확인
        Map<String, Object> lastEventResponse = personalSubscriber.waitForMessage("interview-started", 10);
        assertThat(lastEventResponse).isNotNull();
        assertThat(lastEventResponse.get("type")).isEqualTo("interview-started");

        // and - 마지막 상태 복구 확인
        Map<String, Object> lastStatusResponse = personalSubscriber.waitForMessage("last-status", 10);
        assertThat(lastStatusResponse).isNotNull();
        
        verify(userLastEventStore).getLastEvent(TEST_USER_ID);
        verify(userLastEventStore).getLastInterviewStatus(TEST_USER_ID);
    }

    @Test
    @DisplayName("재연결 실패 시 에러 응답 테스트")
    void reconnectionFailureTest() throws Exception {
        // given - 방을 찾을 수 없는 상황
        given(interviewSessionService.getRoomIdByUserId(TEST_USER_ID))
                .willThrow(new InterviewRoomException(ExceptionCode.ROOM_NOT_FOUND));

        // when - 재접속 시도
        CommonRoomRequest request = new CommonRoomRequest(null);
        stompSession.send("/app/room-join", request);

        // then - 에러 응답 확인
        Map<String, Object> errorResponse = personalSubscriber.waitForMessage("error", 10);
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.get("type")).isEqualTo("error");

        @SuppressWarnings("unchecked")
        Map<String, Object> errorData = (Map<String, Object>) errorResponse.get("data");
        assertThat(errorData.get("error")).isEqualTo("면접 방을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자 상태 업데이트 테스트 - 중요한 상태만 저장")
    void userStatusUpdateTest() throws Exception {
        // given - UserLastEventStore 모킹
        doNothing().when(userLastEventStore).updateUserStatus(anyLong(), anyString());

        // when - 중요한 상태 업데이트 (저장되어야 함)
        stompSession.send("/app/interview-status", "questionPresented");
        Thread.sleep(500);

        // then - 중요한 상태가 저장되었는지 확인
        verify(userLastEventStore).updateUserStatus(TEST_USER_ID, "questionPresented");

        // when - 임시 상태 업데이트 (저장되지 않아야 함)
        stompSession.send("/app/interview-status", "answerStart");
        Thread.sleep(500);

        // then - 임시 상태는 필터링되어 저장되지 않음을 확인
        verify(userLastEventStore).updateUserStatus(TEST_USER_ID, "answerStart");
    }

    @Test
    @DisplayName("정상적인 방 입장 시 재연결 로직이 실행되지 않는 테스트")
    void normalRoomJoinTest() throws Exception {
        // given - 새로운 방 입장 상황 (재연결이 아님)
        given(interviewSessionService.isReconnection(TEST_ROOM_ID, TEST_USER_ID)).willReturn(false);
        given(interviewSessionService.getRoom(TEST_ROOM_ID))
                .willReturn(InterviewRoom.createSoloRoom(TEST_ROOM_ID, 1L, TEST_USER_ID, null));
        given(userService.findById(TEST_USER_ID)).willReturn(testUser);

        // when - 정상적인 방 입장
        CommonRoomRequest request = new CommonRoomRequest(TEST_ROOM_ID);
        stompSession.send("/app/room-join", request);

        // then - 재연결 관련 메시지가 전송되지 않음을 확인
        Thread.sleep(2000); // 메시지가 오지 않는지 확인하기 위한 대기

        // 큐에 메시지가 없거나, 있다면 재연결 관련 메시지가 아니어야 함
        Map<String, Object> anyMessage = personalSubscriber.getMessages().poll();
        if (anyMessage != null) {
            String messageType = (String) anyMessage.get("type");
            assertThat(messageType).isNotIn("room-reconnected", "disconnect", "last-status");
        }

        // and - 정상적인 방 입장 로직이 실행되었는지 확인
        verify(interviewSessionService).joinRoom(TEST_ROOM_ID, TEST_USER_ID);
        verify(interviewSessionService).getRoom(TEST_ROOM_ID);
    }
}