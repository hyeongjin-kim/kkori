package com.kkori.controller.interview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.component.interview.InterviewRoom;
import com.kkori.component.interview.QuestionForm;
import com.kkori.component.interview.QuestionType;
import com.kkori.dto.interview.QuestionDto;
import com.kkori.dto.interview.request.AnswerSubmitRequest;
import com.kkori.dto.interview.request.CommonRoomRequest;
import com.kkori.dto.interview.request.NextQuestionSelectRequest;
import com.kkori.dto.interview.request.RoomCreateRequest;
import com.kkori.dto.interview.response.InterviewStartResponse;
import com.kkori.dto.interview.response.NextQuestionChoicesResponse;
import com.kkori.dto.interview.response.RoomCreateResponse;
import com.kkori.dto.interview.response.STTResultResponse;
import com.kkori.entity.User;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.service.InterviewSessionService;
import com.kkori.service.UserService;
import com.kkori.test.helper.WebSocketTestHelper;
import java.util.Arrays;
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
@DisplayName("혼자 연습하기 (비즈니스 로직 통합 테스트)")
class SoloPracticeTest {

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
    private WebSocketTestHelper.MessageSubscriber roomSubscriber;

    private final Long TEST_USER_ID = 123L;
    private final Long QUESTION_SET_ID = 1L;
    private final String EXPECTED_ROOM_ID = "SOLO_ROOM_123";
    private final String TEST_AUDIO_BASE64 = "UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmAaAkOa2+/EcyQGNI7X8s16KwU";
    
    private String jwtToken;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();

        // 테스트용 User 객체 생성
        testUser = User.builder()
                .userId(TEST_USER_ID)
                .sub("test-kakao-123456")
                .nickname("혼자연습사용자")
                .deleted(false)
                .build();

        // JWT 토큰 생성 및 WebSocket 연결
        Token tokenObject = tokenProvider.generateAccessToken(testUser);
        jwtToken = tokenObject.getToken();
        stompSession = testHelper.createRealTestSession(port, jwtToken, TEST_USER_ID);
        personalSubscriber = testHelper.subscribeToRealPersonalQueue(stompSession, TEST_USER_ID);
        
        // 서비스 모킹 설정
        setupServiceMocks();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (roomSubscriber != null) {
            roomSubscriber.unsubscribe();
        }
        if (personalSubscriber != null) {
            personalSubscriber.unsubscribe();
        }

        Thread.sleep(500);

        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }

    @Test
    @DisplayName("혼자 연습하기")
    void soloPracticeTDDTest() throws Exception {
        try {
            // 1. 방 생성
            String roomId = createSoloRoom();
            
            // 2. 면접 시작
            startInterview(roomId);
            
            // 3. 답변 제출 (STT 처리)
            submitAnswer(roomId);
            
            // 4. 다음 질문 선택
            selectNextQuestion(roomId);
            
            // 5. 면접 종료
            endInterview(roomId);
            
        } finally {
            cleanup();
        }
    }

    // ==================== 테스트 헬퍼 메서드들 ====================

    private void setupServiceMocks() {
        // 방 생성 모킹
        given(interviewSessionService.createSoloRoom(QUESTION_SET_ID, TEST_USER_ID))
                .willReturn(EXPECTED_ROOM_ID);
        
        // 솔로 방 생성 (면접관과 면접자가 동일한 사용자)
        InterviewRoom mockRoom = InterviewRoom.createSoloRoom(EXPECTED_ROOM_ID, QUESTION_SET_ID, TEST_USER_ID, null);
        given(interviewSessionService.getRoom(EXPECTED_ROOM_ID)).willReturn(mockRoom);
        
        // 첫 번째 질문 모킹 (QuestionType, int, String 순서)
        QuestionForm firstQuestion = new QuestionForm(QuestionType.DEFAULT, 1, "자기소개를 해주세요.");
        given(interviewSessionService.getCurrentQuestion(EXPECTED_ROOM_ID)).willReturn(firstQuestion);
        
        // STT 처리 모킹
        given(interviewSessionService.processAudioAnswer(anyString(), any(Long.class), anyString()))
                .willReturn("안녕하세요. 저는 개발자 지망생입니다.");
        
        // 다음 질문들 모킹
        List<QuestionForm> nextQuestions = Arrays.asList(
                new QuestionForm(QuestionType.DEFAULT, 2, "개발자가 되고 싶은 이유는 무엇인가요?"),
                new QuestionForm(QuestionType.TAIL, 0, "방금 답변에서 언급한 내용에 대해 더 자세히 설명해주세요."),
                new QuestionForm(QuestionType.TAIL, 0, "당신은 뭘 하고 싶나요?")
        );
        given(interviewSessionService.getNextQuestions(EXPECTED_ROOM_ID)).willReturn(nextQuestions);
        
        // 질문 선택 모킹
        QuestionForm selectedQuestion = new QuestionForm(QuestionType.DEFAULT, 2, "개발자가 되고 싶은 이유는 무엇인가요?");
        given(interviewSessionService.selectQuestion(anyString(), any(QuestionType.class), any(int.class), anyString()))
                .willReturn(selectedQuestion);
        
        // UserService 모킹 (면접관과 면접자가 같은 사용자)
        given(userService.findById(TEST_USER_ID)).willReturn(testUser);
    }
    
    private String createSoloRoom() throws Exception {
        // 방 생성 요청
        RoomCreateRequest request = new RoomCreateRequest("SOLO_PRACTICE", QUESTION_SET_ID);
        stompSession.send("/app/room-create", request);

        // 방 생성 응답 확인
        Map<String, Object> response = personalSubscriber.waitForMessage("room-created", 10);
        assertThat(response).isNotNull();
        assertThat(response.get("type")).isEqualTo("room-created");

        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
        RoomCreateResponse roomCreateResponse = objectMapper.convertValue(dataMap, RoomCreateResponse.class);
        String roomId = roomCreateResponse.getRoomId();
        
        assertThat(roomId).isEqualTo(EXPECTED_ROOM_ID);
        
        // 방 토픽 구독 (브로드캐스트 메시지 수신용)
        roomSubscriber = testHelper.subscribeToRealRoomTopic(stompSession, roomId);
        
        return roomId;
    }
    
    private void startInterview(String roomId) throws Exception {
        // 면접 시작 요청
        CommonRoomRequest request = new CommonRoomRequest(roomId);
        stompSession.send("/app/interview-start", request);

        // 면접 시작 응답 확인 (브로드캐스트)
        Map<String, Object> response = roomSubscriber.waitForMessage("interview-started", 10);
        assertThat(response).isNotNull();
        assertThat(response.get("type")).isEqualTo("interview-started");

        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
        InterviewStartResponse startResponse = objectMapper.convertValue(dataMap, InterviewStartResponse.class);
        
        assertThat(startResponse.getFirstQuestion()).isNotNull();
        assertThat(startResponse.getFirstQuestion().getQuestionText()).isEqualTo("자기소개를 해주세요.");
    }
    
    private void submitAnswer(String roomId) throws Exception {
        // 답변 제출 요청
        AnswerSubmitRequest request = new AnswerSubmitRequest(roomId, TEST_AUDIO_BASE64);
        stompSession.send("/app/answer-submit", request);

        // 1단계: STT 결과 브로드캐스트 확인
        Map<String, Object> sttResponse = roomSubscriber.waitForMessage("stt-result", 10);
        assertThat(sttResponse).isNotNull();
        assertThat(sttResponse.get("type")).isEqualTo("stt-result");

        @SuppressWarnings("unchecked")
        Map<String, Object> sttDataMap = (Map<String, Object>) sttResponse.get("data");
        STTResultResponse sttResult = objectMapper.convertValue(sttDataMap, STTResultResponse.class);
        assertThat(sttResult.getTranscribedText()).isEqualTo("안녕하세요. 저는 개발자 지망생입니다.");

        // 2단계: 면접관에게 질문 선택지 개인 메시지 확인 (혼자 연습이므로 자신에게)
        Map<String, Object> choicesResponse = personalSubscriber.waitForMessage("next-question-choices", 10);
        assertThat(choicesResponse).isNotNull();
        assertThat(choicesResponse.get("type")).isEqualTo("next-question-choices");

        @SuppressWarnings("unchecked")
        Map<String, Object> choicesDataMap = (Map<String, Object>) choicesResponse.get("data");
        NextQuestionChoicesResponse choices = objectMapper.convertValue(choicesDataMap, NextQuestionChoicesResponse.class);
        assertThat(choices.getNextQuestionChoices()).hasSize(3);
        
        List<QuestionDto> questions = choices.getNextQuestionChoices();
        assertThat(questions.get(0).getQuestionText()).isEqualTo("개발자가 되고 싶은 이유는 무엇인가요?");
        assertThat(questions.get(1).getQuestionText()).isEqualTo("방금 답변에서 언급한 내용에 대해 더 자세히 설명해주세요.");
        assertThat(questions.get(2).getQuestionText()).isEqualTo("당신은 뭘 하고 싶나요?");
    }
    
    private void selectNextQuestion(String roomId) throws Exception {
        // 다음 질문 선택 요청
        NextQuestionSelectRequest request = new NextQuestionSelectRequest(
                roomId, "DEFAULT", 2, "개발자가 되고 싶은 이유는 무엇인가요?"
        );
        stompSession.send("/app/next-question-select", request);

        // 질문 선택 응답 확인 (브로드캐스트)
        Map<String, Object> response = roomSubscriber.waitForMessage("next-question-selected", 10);
        assertThat(response).isNotNull();
        assertThat(response.get("type")).isEqualTo("next-question-selected");

        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
        QuestionDto selectedQuestion = objectMapper.convertValue(dataMap, QuestionDto.class);
        
        assertThat(selectedQuestion.getQuestionText()).isEqualTo("개발자가 되고 싶은 이유는 무엇인가요?");
        assertThat(selectedQuestion.getQuestionType()).isEqualTo("DEFAULT");
    }
    
    private void endInterview(String roomId) throws Exception {
        // 면접 종료 요청
        CommonRoomRequest request = new CommonRoomRequest(roomId);
        stompSession.send("/app/interview-end", request);

        // 면접 종료 응답 확인 (브로드캐스트)
        Map<String, Object> response = roomSubscriber.waitForMessage("interview-ended", 10);
        assertThat(response).isNotNull();
        assertThat(response.get("type")).isEqualTo("interview-ended");

        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) response.get("data");
        assertThat(dataMap.get("message")).isNotNull();
    }
    
    private void cleanup() {
        if (roomSubscriber != null) {
            roomSubscriber.unsubscribe();
        }
    }
}