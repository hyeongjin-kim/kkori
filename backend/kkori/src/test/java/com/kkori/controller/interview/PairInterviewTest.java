package com.kkori.controller.interview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.component.interview.InterviewRoom;
import com.kkori.dto.interview.request.CommonRoomRequest;
import com.kkori.dto.interview.request.RoomCreateRequest;
import com.kkori.dto.interview.response.RoomCreateResponse;
import com.kkori.entity.User;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.service.InterviewSessionService;
import com.kkori.service.UserService;
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
@DisplayName("같이 연습하기 테스트")
class PairInterviewTest {

    @LocalServerPort
    private int port;

    @MockBean
    private InterviewSessionService interviewSessionService;

    @MockBean
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private WebSocketTestHelper testHelper;

    private StompSession stompSession;
    private ObjectMapper objectMapper;
    private WebSocketTestHelper.MessageSubscriber personalSubscriber;

    private final Long TEST_USER_ID = 123L;
    private String jwtToken;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();

        // 테스트용 User 객체 생성
        testUser = User.builder()
                .userId(TEST_USER_ID)
                .sub("test-kakao-123456")
                .nickname("테스트사용자")
                .deleted(false)
                .build();

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

    @Test
    @DisplayName("(JWT 쿠키 기반) 2명의 사용자 방 입장 시 각각 올바른 메시지를 받을 수 있다.")
    void realJWTTwoUsersRoomTest() throws Exception {
        // given
        TwoUserTestSetup testSetup = setupTwoUserTest();

        try {
            // when - 방 생성 및 입장
            createPairRoomAndJoin(testSetup);

            // then - 개인 메시지 검증
            verifyPersonalJoinMessages(testSetup);

        } finally {
            // 정리
            cleanupTwoUserTest(testSetup);
        }
    }

    // ==================== 테스트 데이터 클래스 ====================

    /**
     * 2명 사용자 테스트 설정
     */
    private static class TwoUserTestSetup {
        final Long user2Id = 456L;
        final User testUser2;
        final StompSession stompSession2;
        final WebSocketTestHelper.MessageSubscriber personalSubscriber2;

        TwoUserTestSetup(User testUser2, StompSession stompSession2,
                         WebSocketTestHelper.MessageSubscriber personalSubscriber2) {
            this.testUser2 = testUser2;
            this.stompSession2 = stompSession2;
            this.personalSubscriber2 = personalSubscriber2;
        }
    }

    // ==================== 테스트 헬퍼 메서드들 ====================

    private TwoUserTestSetup setupTwoUserTest() throws Exception {
        final Long TEST_USER2_ID = 456L;

        // 두 번째 사용자 생성 및 JWT 토큰 생성
        User testUser2 = createTestUser(TEST_USER2_ID, "test-kakao-456789", "테스트사용자2");
        Token tokenObject2 = tokenProvider.generateAccessToken(testUser2);
        String jwtToken2 = tokenObject2.getToken();

        // WebSocket 연결 및 구독
        StompSession stompSession2 = testHelper.createRealTestSession(port, jwtToken2, TEST_USER2_ID);
        WebSocketTestHelper.MessageSubscriber personalSubscriber2 =
                testHelper.subscribeToRealPersonalQueue(stompSession2, TEST_USER2_ID);

        // 서비스 모킹 설정
        setupServiceMocks(testUser, testUser2);

        return new TwoUserTestSetup(testUser2, stompSession2, personalSubscriber2);
    }

    private User createTestUser(Long userId, String sub, String nickname) {
        return User.builder()
                .userId(userId)
                .sub(sub)
                .nickname(nickname)
                .deleted(false)
                .build();
    }

    private void setupServiceMocks(User user1, User user2) {
        // 방 생성 모킹
        String testRoomId = "TWO_USER_ROOM_123";
        given(interviewSessionService.createPairRoom(1L, TEST_USER_ID))
                .willReturn(testRoomId);

        // UserService 모킹
        given(userService.findById(TEST_USER_ID)).willReturn(user1);
        given(userService.findById(user2.getUserId())).willReturn(user2);
    }

    private void createPairRoomAndJoin(TwoUserTestSetup testSetup) throws Exception {
        // 1. 방 생성
        RoomCreateRequest createRequest = new RoomCreateRequest("PAIR_INTERVIEW", 1L);
        stompSession.send("/app/room-create", createRequest);

        // 방 생성 응답 확인
        Map<String, Object> createResponse = personalSubscriber.waitForMessage("room-created", 10);
        String createdRoomId = extractRoomIdFromResponse(createResponse);

        // 2. 방 입장 관련 모킹 설정
        setupRoomJoinMocks(createdRoomId, testSetup.user2Id);

        // 3. 두 번째 사용자 방 입장
        CommonRoomRequest joinRequest = new CommonRoomRequest(createdRoomId);
        testSetup.stompSession2.send("/app/room-join", joinRequest);
    }

    private String extractRoomIdFromResponse(Map<String, Object> response) {
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
        RoomCreateResponse roomCreateResponse = objectMapper.convertValue(dataMap, RoomCreateResponse.class);
        return roomCreateResponse.getRoomId();
    }

    private void setupRoomJoinMocks(String roomId, Long user2Id) {
        InterviewRoom mockRoom = InterviewRoom.createPairRoom(roomId, 1L, TEST_USER_ID, null);
        mockRoom.addUser(user2Id);
        given(interviewSessionService.getRoom(roomId)).willReturn(mockRoom);
    }

    private void verifyPersonalJoinMessages(TwoUserTestSetup testSetup) throws Exception {
        // 개인 메시지들 확인
        Map<String, Object> creatorNotification = personalSubscriber.waitForMessage("joined-user", 10);
        Map<String, Object> participantNotification = testSetup.personalSubscriber2.waitForMessage("existing-user", 10);

        // 방 생성자가 받는 "joined-user" 메시지 검증
        verifyJoinedUserMessage(creatorNotification, testSetup.testUser2.getNickname());

        // 새 참여자가 받는 "existing-user" 메시지 검증
        verifyExistingUserMessage(participantNotification, testUser.getNickname(), TEST_USER_ID);
    }

    private void verifyJoinedUserMessage(Map<String, Object> message, String expectedNickname) {
        assertThat(message.get("type")).isEqualTo("joined-user");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) message.get("data");
        assertThat(data.get("nickName")).isEqualTo(expectedNickname);
    }

    private void verifyExistingUserMessage(Map<String, Object> message, String expectedNickname, Long expectedUserId) {
        assertThat(message.get("type")).isEqualTo("existing-user");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) message.get("data");
        assertThat(data.get("nickName")).isEqualTo(expectedNickname);
    }

    private void cleanupTwoUserTest(TwoUserTestSetup testSetup) {
        unsubscribeSafe(testSetup.personalSubscriber2);
        disconnectSafe(testSetup.stompSession2);
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
}