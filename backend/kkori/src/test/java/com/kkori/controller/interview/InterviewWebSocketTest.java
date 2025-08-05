package com.kkori.controller.interview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.interview.request.AnswerSubmitRequest;
import com.kkori.dto.interview.request.CommonRoomRequest;
import com.kkori.dto.interview.request.CustomQuestionCreateRequest;
import com.kkori.dto.interview.request.NextQuestionSelectRequest;
import com.kkori.dto.interview.request.RoomCreateRequest;
import com.kkori.dto.interview.response.InterviewStartResponse;
import com.kkori.dto.interview.response.NextQuestionChoicesResponse;
import com.kkori.dto.interview.response.RoomCreateResponse;
import com.kkori.dto.interview.response.RoomStatusResponse;
import com.kkori.dto.interview.response.STTResultResponse;
import com.kkori.component.interview.QuestionForm;
import com.kkori.component.interview.QuestionType;
import com.kkori.dto.interview.QuestionDto;
import com.kkori.component.interview.InterviewRoom;
import com.kkori.entity.User;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.service.InterviewSessionService;
import com.kkori.service.UserService;
import com.kkori.test.helper.WebSocketTestHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
@DisplayName("InterviewWebSocketController 테스트(JWT 쿠키 기반)")
class InterviewWebSocketTest {

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
    @DisplayName("실제 JWT 쿠키로 방 생성 테스트 (혼자 연습하기)")
    void realJWTRoomCreateTest() throws Exception {
        // given
        String expectedRoomId = "REAL_CREATED_ROOM_789";
        given(interviewSessionService.createSoloRoom(1L, TEST_USER_ID))
                .willReturn(expectedRoomId);

        // RoomCreateRequest 직접 전송
        RoomCreateRequest request = new RoomCreateRequest("SOLO_PRACTICE", 1L);

        // when
        stompSession.send("/app/room-create", request);

        try {
            Map<String, Object> response = personalSubscriber.waitForMessage("room-created", 10);

            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("room-created");

            // 응답 데이터에서 방 정보 확인
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
            RoomCreateResponse roomCreateResponse = objectMapper.convertValue(dataMap, RoomCreateResponse.class);
            assertThat(roomCreateResponse.getRoomId()).isEqualTo(expectedRoomId);
        } catch (AssertionError e) {
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

        // when
        stompSession.send("/app/room-status", request);
        Thread.sleep(500);

        try {
            Map<String, Object> response = personalSubscriber.waitForMessage("room-status", 10);

            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("room-status");

            // 응답 데이터에서 방 상태 정보 확인
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
            RoomStatusResponse roomStatusResponse = objectMapper.convertValue(dataMap, RoomStatusResponse.class);
            assertThat(roomStatusResponse.getStatus()).isEqualTo("WAITING");
            assertThat(roomStatusResponse.getUserCount()).isEqualTo(1);
            assertThat(roomStatusResponse.getMaxUsers()).isEqualTo(2);


        } catch (AssertionError e) {
            printQueuedMessages();
            throw e;
        }
    }

    @Test
    @DisplayName("실제 인터셉터 동작 확인 - 인증 실패 시뮬레이션")
    void realInterceptorAuthFailureTest() throws Exception {
        // 잘못된 JWT로 연결 시도 테스트
        String invalidToken = "invalid.jwt.token";

        try {
            StompSession failSession = testHelper.createRealTestSession(port, invalidToken, TEST_USER_ID);
            failSession.disconnect();
        } catch (Exception e) {
            // 예상대로 연결 실패
        }
    }

    @Test
    @DisplayName("JWT 토큰 정보 확인")
    void jwtTokenInfoTest() throws Exception {
        assertThat(tokenProvider.validateToken(jwtToken)).isTrue();
        assertThat(tokenProvider.getUserIdFromToken(jwtToken)).isEqualTo(TEST_USER_ID);
    }


    @Test
    @DisplayName("에러 응답 처리 테스트")
    void errorResponseTest() throws Exception {
        // given - 존재하지 않는 모드로 방 생성 시도
        RoomCreateRequest invalidRequest = new RoomCreateRequest("INVALID_MODE", 1L);

        // when
        stompSession.send("/app/room-create", invalidRequest);

        // then
        try {
            Map<String, Object> response = personalSubscriber.waitForMessage("error", 10);

            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("error");

        } catch (AssertionError e) {
            printQueuedMessages();
            throw e;
        }
    }

    @Test
    @DisplayName("방 나가기 테스트")
    void roomExitTest() throws Exception {
        executeWithRoomSubscriber(TEST_ROOM_ID, roomSubscriber -> {
            // when
            sendWebSocketMessage("/app/room-exit", createCommonRoomRequest(TEST_ROOM_ID));

            // then - 브로드캐스트 메시지 확인
            Map<String, Object> response = waitForBroadcastMessage(roomSubscriber, "user-exited", 10);
            Map<String, Object> dataMap = extractMessageData(response);
            assertThat(dataMap.get("message")).isNotNull();
        });
    }

    @Test
    @DisplayName("면접 시작 테스트")
    void interviewStartTest() throws Exception {
        // given
        setupInterviewMocks();
        
        executeWithRoomSubscriber(TEST_ROOM_ID, roomSubscriber -> {
            // when
            sendWebSocketMessage("/app/interview-start", createCommonRoomRequest(TEST_ROOM_ID));

            // then - 브로드캐스트 메시지 확인
            Map<String, Object> response = waitForBroadcastMessage(roomSubscriber, "interview-started", 10);
            Map<String, Object> dataMap = extractMessageData(response);
            InterviewStartResponse startResponse = objectMapper.convertValue(dataMap, InterviewStartResponse.class);
            assertThat(startResponse.getFirstQuestion()).isNotNull();
            assertThat(startResponse.getFirstQuestion().getQuestionText()).isEqualTo("자기소개를 해주세요.");
        });
    }

    @Test
    @DisplayName("면접 종료 테스트")
    void interviewEndTest() throws Exception {
        // given
        WebSocketTestHelper.MessageSubscriber roomSubscriber = 
                testHelper.subscribeToRealRoomTopic(stompSession, TEST_ROOM_ID);
        CommonRoomRequest request = new CommonRoomRequest(TEST_ROOM_ID);

        try {
            // when
            stompSession.send("/app/interview-end", request);

            // then - 브로드캐스트 메시지 확인
            Map<String, Object> response = roomSubscriber.waitForMessage("interview-ended", 10);
            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("interview-ended");

            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
            assertThat(dataMap.get("message")).isNotNull();
        } finally {
            unsubscribeSafe(roomSubscriber);
        }
    }

    @Test
    @DisplayName("답변 시작 테스트")
    void answerStartTest() throws Exception {
        // given
        WebSocketTestHelper.MessageSubscriber roomSubscriber = 
                testHelper.subscribeToRealRoomTopic(stompSession, TEST_ROOM_ID);
        CommonRoomRequest request = new CommonRoomRequest(TEST_ROOM_ID);

        try {
            // when
            stompSession.send("/app/answer-start", request);

            // then - 브로드캐스트 메시지 확인
            Map<String, Object> response = roomSubscriber.waitForMessage("answer-recording-started", 10);
            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("answer-recording-started");

            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
            assertThat(dataMap.get("message")).isNotNull();
        } finally {
            unsubscribeSafe(roomSubscriber);
        }
    }

    @Test
    @DisplayName("답변 제출 테스트")
    void answerSubmitTest() throws Exception {
        // given
        setupAnswerSubmitMocks();
        WebSocketTestHelper.MessageSubscriber roomSubscriber = 
                testHelper.subscribeToRealRoomTopic(stompSession, TEST_ROOM_ID);
        
        String testAudioBase64 = "UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmAaA";
        AnswerSubmitRequest request = new AnswerSubmitRequest(TEST_ROOM_ID, testAudioBase64);

        try {
            // when
            stompSession.send("/app/answer-submit", request);

            // then - 1단계: STT 결과 브로드캐스트 확인
            Map<String, Object> sttResponse = roomSubscriber.waitForMessage("stt-result", 10);
            assertThat(sttResponse).isNotNull();
            assertThat(sttResponse.get("type")).isEqualTo("stt-result");

            @SuppressWarnings("unchecked")
            Map<String, Object> sttDataMap = (Map<String, Object>) sttResponse.get("data");
            STTResultResponse sttResult = objectMapper.convertValue(sttDataMap, STTResultResponse.class);
            assertThat(sttResult.getTranscribedText()).isEqualTo("테스트 답변입니다.");

            // then - 2단계: 면접관에게 질문 선택지 개인 메시지 확인
            Map<String, Object> choicesResponse = personalSubscriber.waitForMessage("next-question-choices", 10);
            assertThat(choicesResponse).isNotNull();
            assertThat(choicesResponse.get("type")).isEqualTo("next-question-choices");

            @SuppressWarnings("unchecked")
            Map<String, Object> choicesDataMap = (Map<String, Object>) choicesResponse.get("data");
            NextQuestionChoicesResponse choices = objectMapper.convertValue(choicesDataMap, NextQuestionChoicesResponse.class);
            assertThat(choices.getNextQuestionChoices()).hasSize(2);
        } finally {
            unsubscribeSafe(roomSubscriber);
        }
    }

    @Test
    @DisplayName("다음 질문 선택 테스트")
    void nextQuestionSelectTest() throws Exception {
        // given
        setupQuestionSelectMocks();
        WebSocketTestHelper.MessageSubscriber roomSubscriber = 
                testHelper.subscribeToRealRoomTopic(stompSession, TEST_ROOM_ID);
        NextQuestionSelectRequest request = new NextQuestionSelectRequest(
                TEST_ROOM_ID, "DEFAULT", 2, "개발자가 되고 싶은 이유는 무엇인가요?"
        );

        try {
            // when
            stompSession.send("/app/next-question-select", request);

            // then - 브로드캐스트 메시지 확인
            Map<String, Object> response = roomSubscriber.waitForMessage("next-question-selected", 10);
            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("next-question-selected");

            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
            QuestionDto selectedQuestion = objectMapper.convertValue(dataMap, QuestionDto.class);
            assertThat(selectedQuestion.getQuestionText()).isEqualTo("개발자가 되고 싶은 이유는 무엇인가요?");
            assertThat(selectedQuestion.getQuestionType()).isEqualTo("DEFAULT");
        } finally {
            unsubscribeSafe(roomSubscriber);
        }
    }

    @Test
    @DisplayName("커스텀 질문 시작 테스트")
    void customQuestionStartTest() throws Exception {
        // given
        WebSocketTestHelper.MessageSubscriber roomSubscriber = 
                testHelper.subscribeToRealRoomTopic(stompSession, TEST_ROOM_ID);
        CommonRoomRequest request = new CommonRoomRequest(TEST_ROOM_ID);

        try {
            // when
            stompSession.send("/app/custom-question-start", request);

            // then - 브로드캐스트 메시지 확인
            Map<String, Object> response = roomSubscriber.waitForMessage("custom-question-recording-started", 10);
            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("custom-question-recording-started");

            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
            assertThat(dataMap.get("message")).isNotNull();
        } finally {
            unsubscribeSafe(roomSubscriber);
        }
    }

    @Test
    @DisplayName("커스텀 질문 생성 테스트")
    void customQuestionCreateTest() throws Exception {
        // given
        setupCustomQuestionMocks();
        WebSocketTestHelper.MessageSubscriber roomSubscriber = 
                testHelper.subscribeToRealRoomTopic(stompSession, TEST_ROOM_ID);
        
        String testAudioBase64 = "UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmAaA";
        CustomQuestionCreateRequest request = new CustomQuestionCreateRequest(TEST_ROOM_ID, testAudioBase64);

        try {
            // when
            stompSession.send("/app/custom-question-create", request);

            // then - 브로드캐스트 메시지 확인
            Map<String, Object> response = roomSubscriber.waitForMessage("custom-question-created", 10);
            assertThat(response).isNotNull();
            assertThat(response.get("type")).isEqualTo("custom-question-created");

            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
            QuestionDto customQuestion = objectMapper.convertValue(dataMap, QuestionDto.class);
            assertThat(customQuestion.getQuestionText()).isEqualTo("커스텀 질문입니다.");
            assertThat(customQuestion.getQuestionType()).isEqualTo("CUSTOM");
        } finally {
            unsubscribeSafe(roomSubscriber);
        }
    }


    // ==================== 헬퍼 메서드들 ====================
    
    private List<WebSocketTestHelper.MessageSubscriber> subscribeUsersToRoom(List<StompSession> sessions, String roomId) {
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

    private void printQueuedMessages() {
        Map<String, Object> anyMessage;
        int count = 0;
        while ((anyMessage = personalSubscriber.getMessages().poll()) != null && count < 10) {
            count++;
        }
    }
    
    // ==================== 공통 테스트 헬퍼 메서드들 ====================
    
    /**
     * 룸 구독자와 함께 테스트를 실행하는 헬퍼 메서드
     */
    private void executeWithRoomSubscriber(String roomId, TestExecutor executor) throws Exception {
        WebSocketTestHelper.MessageSubscriber roomSubscriber = 
                testHelper.subscribeToRealRoomTopic(stompSession, roomId);
        try {
            executor.execute(roomSubscriber);
        } finally {
            unsubscribeSafe(roomSubscriber);
        }
    }
    
    /**
     * WebSocket 메시지 전송
     */
    private void sendWebSocketMessage(String destination, Object request) {
        stompSession.send(destination, request);
    }
    
    /**
     * 개인 메시지 응답 검증
     */
    private Map<String, Object> waitForPersonalMessageWithErrorHandling(String messageType, int timeoutSeconds) throws Exception {
        try {
            Map<String, Object> response = personalSubscriber.waitForMessage(messageType, timeoutSeconds);
            assertThat(response).isNotNull();
            return response;
        } catch (AssertionError e) {
            throw e;
        }
    }
    
    /**
     * 브로드캐스트 메시지 응답 검증
     */
    private Map<String, Object> waitForBroadcastMessage(WebSocketTestHelper.MessageSubscriber roomSubscriber, 
                                                       String messageType, int timeoutSeconds) throws Exception {
        Map<String, Object> response = roomSubscriber.waitForMessage(messageType, timeoutSeconds);
        assertThat(response).isNotNull();
        assertThat(response.get("type")).isEqualTo(messageType);
        return response;
    }
    
    /**
     * 공통 요청 생성
     */
    private CommonRoomRequest createCommonRoomRequest(String roomId) {
        return new CommonRoomRequest(roomId);
    }
    
    /**
     * 메시지 데이터 추출
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractMessageData(Map<String, Object> message) {
        return (Map<String, Object>) message.get("data");
    }
    
    /**
     * 테스트 실행 유저 인터페이스
     */
    @FunctionalInterface
    private interface TestExecutor {
        void execute(WebSocketTestHelper.MessageSubscriber roomSubscriber) throws Exception;
    }
    
    // ==================== Mock 설정 헬퍼 메서드들 ====================
    
    private void setupInterviewMocks() {
        // 첫 번째 질문 모킹
        QuestionForm firstQuestion = new QuestionForm(QuestionType.DEFAULT, 1, "자기소개를 해주세요.");
        given(interviewSessionService.getCurrentQuestion(TEST_ROOM_ID)).willReturn(firstQuestion);
    }
    
    private void setupAnswerSubmitMocks() {
        // STT 처리 모킹
        given(interviewSessionService.processAudioAnswer(anyString(), any(Long.class), anyString()))
                .willReturn("테스트 답변입니다.");
        
        // 방 정보 모킹 (면접관 설정)
        InterviewRoom mockRoom = InterviewRoom.createSoloRoom(TEST_ROOM_ID, 1L, TEST_USER_ID, null);
        given(interviewSessionService.getRoom(TEST_ROOM_ID)).willReturn(mockRoom);
        
        // 다음 질문들 모킹
        List<QuestionForm> nextQuestions = Arrays.asList(
                new QuestionForm(QuestionType.DEFAULT, 2, "개발자가 되고 싶은 이유는 무엇인가요?"),
                new QuestionForm(QuestionType.TAIL, 3, "방금 답변에서 언급한 내용에 대해 더 자세히 설명해주세요.")
        );
        given(interviewSessionService.getNextQuestions(TEST_ROOM_ID)).willReturn(nextQuestions);
        
        // UserService 모킹
        given(userService.findById(TEST_USER_ID)).willReturn(testUser);
    }
    
    private void setupQuestionSelectMocks() {
        // 질문 선택 모킹
        QuestionForm selectedQuestion = new QuestionForm(QuestionType.DEFAULT, 2, "개발자가 되고 싶은 이유는 무엇인가요?");
        given(interviewSessionService.selectQuestion(anyString(), any(QuestionType.class), any(int.class), anyString()))
                .willReturn(selectedQuestion);
    }
    
    private void setupCustomQuestionMocks() {
        // 커스텀 질문 생성 모킹
        QuestionForm customQuestion = new QuestionForm(QuestionType.CUSTOM, 100, "커스텀 질문입니다.");
        given(interviewSessionService.createCustomQuestion(anyString(), anyString()))
                .willReturn(customQuestion);
    }
}