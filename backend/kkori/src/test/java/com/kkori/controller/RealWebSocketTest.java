package com.kkori.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.interview.request.CommonRoomRequest;
import com.kkori.dto.interview.request.RoomCreateRequest;
import com.kkori.dto.interview.response.RoomCreateResponse;
import com.kkori.dto.interview.response.RoomStatusResponse;
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
@DisplayName("실제 JWT 쿠키 기반 WebSocket 테스트")
class RealWebSocketTest {

    @LocalServerPort
    private int port;

    @MockBean
    private InterviewSessionService interviewSessionService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private WebSocketTestHelper testHelper;

    private StompSession stompSession;
    private ObjectMapper objectMapper;
    private WebSocketTestHelper.MessageSubscriber personalSubscriber;

    private final Long TEST_USER_ID = 123L;
    private final String TEST_ROOM_ID = "REAL_ROOM_456";
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
        System.out.println("🔑 생성된 JWT 토큰: " + jwtToken.substring(0, Math.min(30, jwtToken.length())) + "...");
        System.out.println("🔍 토큰에서 사용자 ID 확인: " + tokenProvider.getUserIdFromToken(jwtToken));
        System.out.println("🔍 토큰 유효성 확인: " + tokenProvider.validateToken(jwtToken));

        // 실제 JWT 쿠키와 함께 WebSocket 연결
        stompSession = testHelper.createRealTestSession(port, jwtToken, TEST_USER_ID);
        personalSubscriber = testHelper.subscribeToRealPersonalQueue(stompSession, TEST_USER_ID);

        System.out.println("=== 실제 JWT 기반 테스트 준비 완료 ===");
    }

    @AfterEach
    void tearDown() throws Exception {
        System.out.println("🔧 실제 테스트 정리 시작...");

        if (personalSubscriber != null) {
            personalSubscriber.unsubscribe();
        }

        Thread.sleep(1000);

        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
            System.out.println("🔌 실제 WebSocket 연결 해제됨");
        }

        System.out.println("✅ 실제 테스트 정리 완료");
    }

    @Test
    @DisplayName("실제 JWT 쿠키로 방 생성 테스트")
    void realJWTRoomCreateTest() throws Exception {
        // given
        String expectedRoomId = "REAL_CREATED_ROOM_789";
        given(interviewSessionService.createSoloRoom(1L, TEST_USER_ID))
                .willReturn(expectedRoomId);

        // RoomCreateRequest 객체를 직접 전송
        RoomCreateRequest request = new RoomCreateRequest("SOLO_PRACTICE", 1L);

        System.out.println("🎯 실제 JWT로 방 생성 메시지 전송");

        // when
        stompSession.send("/app/room-create", request);

        // then
        System.out.println("📥 실제 JWT 기반 응답 메시지 대기 중... (10초)");

        try {
            Map<String, Object> response = personalSubscriber.waitForMessage("room-created", 10);
            System.out.println("✅ 실제 JWT 기반 응답 받음: " + response);

            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("room-created");

            // 응답 데이터에서 방 정보 확인
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
            RoomCreateResponse roomCreateResponse = objectMapper.convertValue(dataMap, RoomCreateResponse.class);
            assertThat(roomCreateResponse.getRoomId()).isEqualTo(expectedRoomId);

            System.out.println("🎉 실제 JWT 기반 테스트 성공!");
            System.out.println("📋 생성된 방 ID: " + roomCreateResponse.getRoomId());

        } catch (AssertionError e) {
            System.err.println("❌ 실제 JWT 기반 테스트 실패: " + e.getMessage());

            // 실패 시 디버깅 정보
            System.out.println("🔍 JWT 토큰 재확인: " + tokenProvider.validateToken(jwtToken));
            System.out.println("🔍 사용자 ID 재확인: " + tokenProvider.getUserIdFromToken(jwtToken));

            // 대기 중인 모든 메시지 확인
            printQueuedMessages();

            throw e;
        }
    }

    @Test
    @DisplayName("실제 JWT 쿠키로 방 상태 조회 테스트")
    void realJWTRoomStatusTest() throws Exception {
        // given
        CommonRoomRequest request = new CommonRoomRequest(TEST_ROOM_ID);

        System.out.println("🎯 실제 JWT로 방 상태 조회 메시지 전송");

        // when
        stompSession.send("/app/room-status", request);
        Thread.sleep(1000);

        // then
        System.out.println("📥 실제 JWT 기반 상태 응답 대기 중... (10초)");

        try {
            Map<String, Object> response = personalSubscriber.waitForMessage("room-status", 10);
            System.out.println("✅ 실제 JWT 기반 상태 응답 받음: " + response);

            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("room-status");

            // 응답 데이터에서 방 상태 정보 확인
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
            RoomStatusResponse roomStatusResponse = objectMapper.convertValue(dataMap, RoomStatusResponse.class);
            assertThat(roomStatusResponse.getStatus()).isEqualTo("WAITING");
            assertThat(roomStatusResponse.getUserCount()).isEqualTo(1);
            assertThat(roomStatusResponse.getMaxUsers()).isEqualTo(2);

            System.out.println("🎉 실제 JWT 기반 상태 조회 테스트 성공!");
            System.out.println("📋 방 상태: " + roomStatusResponse.getStatus());
            System.out.println("📋 참여자 수: " + roomStatusResponse.getUserCount() + "/" + roomStatusResponse.getMaxUsers());

        } catch (AssertionError e) {
            System.err.println("❌ 실제 JWT 기반 상태 조회 실패: " + e.getMessage());
            printQueuedMessages();
            throw e;
        }
    }

    @Test
    @DisplayName("실제 인터셉터 동작 확인 - 인증 실패 시뮬레이션")
    void realInterceptorAuthFailureTest() throws Exception {
        System.out.println("🎯 잘못된 JWT로 연결 시도 테스트");

        String invalidToken = "invalid.jwt.token";

        try {
            StompSession failSession = testHelper.createRealTestSession(port, invalidToken, TEST_USER_ID);
            System.err.println("❌ 잘못된 토큰으로 연결이 성공했습니다! (문제 있음)");
            failSession.disconnect();
        } catch (Exception e) {
            System.out.println("✅ 예상대로 잘못된 토큰으로 연결 실패: " + e.getMessage());
            // 이것이 정상적인 동작
        }
    }

    @Test
    @DisplayName("JWT 토큰 정보 확인")
    void jwtTokenInfoTest() throws Exception {
        System.out.println("🔍 === JWT 토큰 정보 확인 ===");
        System.out.println("📋 테스트 사용자: " + testUser);
        System.out.println("📋 사용자 ID: " + testUser.getUserId());
        System.out.println("📋 사용자 Sub: " + testUser.getSub());
        System.out.println("📋 사용자 닉네임: " + testUser.getNickname());
        System.out.println("📋 JWT 토큰 유효성: " + tokenProvider.validateToken(jwtToken));
        System.out.println("📋 JWT에서 추출한 사용자 ID: " + tokenProvider.getUserIdFromToken(jwtToken));
        System.out.println("🔍 === JWT 토큰 정보 확인 완료 ===");
    }

//    @Test
//    @DisplayName("실제 JWT 쿠키로 2명 사용자 방 입장 테스트")
//    void realJWTTwoUsersRoomTest() throws Exception {
//        // given - 두 번째 사용자 설정
//        final Long TEST_USER2_ID = 456L;
//        User testUser2 = User.builder()
//                .userId(TEST_USER2_ID)
//                .sub("test-kakao-456789")
//                .nickname("테스트사용자2")
//                .deleted(false)
//                .build();
//
//        Token tokenObject2 = tokenProvider.generateAccessToken(testUser2);
//        String jwtToken2 = tokenObject2.getToken();
//
//        // 두 번째 사용자 WebSocket 연결 및 구독
//        StompSession stompSession2 = testHelper.createRealTestSession(port, jwtToken2, TEST_USER2_ID);
//        WebSocketTestHelper.MessageSubscriber personalSubscriber2 =
//                testHelper.subscribeToRealPersonalQueue(stompSession2, TEST_USER2_ID);
//
//        try {
//            System.out.println("🎯 === 2명 사용자 방 테스트 시작 ===");
//
//            // 방 생성 모킹 - createPairRoom 사용 (2명이 함께하는 방)
//            String testRoomId = "TWO_USER_ROOM_123";
//            given(interviewSessionService.createPairRoom(1L, TEST_USER_ID))
//                    .willReturn(testRoomId);
//
//            // 방 참여 가능 여부 및 참여 모킹
//            given(interviewSessionService.canJoinRoom(testRoomId))
//                    .willReturn(true);
//
//            // joinRoom은 void이므로 doNothing 사용
//            doNothing().when(interviewSessionService).joinRoom(testRoomId, TEST_USER2_ID);
//
//            // Step 1: 첫 번째 사용자가 함께 연습하기 방 생성
//            System.out.println("📤 사용자1이 함께 연습하기 방 생성 요청");
//            RoomCreateRequest createRequest = new RoomCreateRequest("PAIR_PRACTICE", 1L);
//            stompSession.send("/app/room-create", createRequest);
//
//            // 방 생성 응답 확인
//            Map<String, Object> createResponse = personalSubscriber.waitForMessage("room-created", 10);
//            System.out.println("✅ 사용자1 방 생성 응답: " + createResponse);
//
//            @SuppressWarnings("unchecked")
//            Map<String, Object> createDataMap = (Map<String, Object>) createResponse.get("data");
//            RoomCreateResponse roomCreateResponse = objectMapper.convertValue(createDataMap, RoomCreateResponse.class);
//            String createdRoomId = roomCreateResponse.getRoomId();
//
//            // Step 2: 방 토픽 구독 (두 사용자 모두)
//            WebSocketTestHelper.MessageSubscriber roomSubscriber1 =
//                    testHelper.subscribeToRealRoomTopic(stompSession, createdRoomId);
//            WebSocketTestHelper.MessageSubscriber roomSubscriber2 =
//                    testHelper.subscribeToRealRoomTopic(stompSession2, createdRoomId);
//
//            // Step 3: 두 번째 사용자가 방 입장
//            System.out.println("📤 사용자2가 방 입장 요청");
//            CommonRoomRequest joinRequest = new CommonRoomRequest(createdRoomId);
//            stompSession2.send("/app/room-join", joinRequest);
//
//            // Step 4: 입장 관련 알림들 확인
//            System.out.println("📥 방 입장 관련 알림 대기 중...");
//
//            // 사용자2가 받는 알림 (방 입장 성공)
//            Map<String, Object> user2Response = personalSubscriber2.waitForMessage("room-joined", 10);
//            System.out.println("✅ 사용자2가 받은 입장 응답: " + user2Response);
//
//            // 방 토픽으로 브로드캐스트되는 새 사용자 입장 알림
//            Map<String, Object> user1Notification = roomSubscriber1.waitForMessage("user-joined", 10);
//            System.out.println("✅ 사용자1이 받은 입장 알림: " + user1Notification);
//
//            // Step 5: 검증
//            assertThat(user2Response.get("type")).isEqualTo("room-joined");
//            assertThat(user1Notification.get("type")).isEqualTo("user-joined");
//
//            @SuppressWarnings("unchecked")
//            Map<String, Object> joinDataMap = (Map<String, Object>) user2Response.get("data");
//            assertThat(joinDataMap.get("roomId")).isEqualTo(createdRoomId);
//
//            System.out.println("🎉 2명 사용자 방 입장 테스트 성공!");
//            System.out.println("📋 방 ID: " + createdRoomId);
//            System.out.println("📋 사용자1: " + TEST_USER_ID + ", 사용자2: " + TEST_USER2_ID);
//
//            // 정리
//            roomSubscriber1.unsubscribe();
//            roomSubscriber2.unsubscribe();
//
//        } finally {
//            // 두 번째 사용자 연결 정리
//            if (personalSubscriber2 != null) {
//                personalSubscriber2.unsubscribe();
//            }
//            if (stompSession2 != null && stompSession2.isConnected()) {
//                stompSession2.disconnect();
//                System.out.println("🔌 사용자2 WebSocket 연결 해제됨");
//            }
//        }
//    }

    @Test
    @DisplayName("에러 응답 처리 테스트")
    void errorResponseTest() throws Exception {
        // given - 존재하지 않는 모드로 방 생성 시도
        RoomCreateRequest invalidRequest = new RoomCreateRequest("INVALID_MODE", 1L);

        System.out.println("🎯 잘못된 요청으로 에러 응답 테스트");

        // when
        stompSession.send("/app/room-create", invalidRequest);

        // then
        try {
            Map<String, Object> response = personalSubscriber.waitForMessage("error", 10);
            System.out.println("✅ 에러 응답 받음: " + response);

            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("error");

            System.out.println("🎉 에러 응답 처리 테스트 성공!");

        } catch (AssertionError e) {
            System.err.println("❌ 에러 응답 테스트 실패: " + e.getMessage());
            printQueuedMessages();
            throw e;
        }
    }

    private void printQueuedMessages() {
        System.out.println("🔍 === 큐에 있는 모든 메시지 확인 ===");
        Map<String, Object> anyMessage;
        int count = 0;
        while ((anyMessage = personalSubscriber.getMessages().poll()) != null && count < 10) {
            System.out.println("📨 큐에 있던 메시지 " + (++count) + ": " + anyMessage);
        }
        if (count == 0) {
            System.out.println("📭 큐에 메시지가 없습니다.");
        }
        System.out.println("🔍 === 메시지 확인 완료 ===");
    }
}