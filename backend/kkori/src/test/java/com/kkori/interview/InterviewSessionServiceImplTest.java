package com.kkori.interview;

import com.kkori.component.InterviewRoomManager;
import com.kkori.component.TailQuestionGenerator;
import com.kkori.component.Transcriber;
import com.kkori.component.interview.InterviewRoom;
import com.kkori.component.interview.InterviewSession;
import com.kkori.component.interview.QuestionForm;
import com.kkori.entity.*;
import com.kkori.exception.audio.AudioProcessingException;
import com.kkori.exception.interview.InterviewRoomException;
import com.kkori.exception.interview.InterviewSessionException;
import com.kkori.exception.interview.TailQuestionException;
import com.kkori.repository.*;
import com.kkori.service.InterviewSessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static com.kkori.component.interview.QuestionType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterviewSessionService 구현 테스트")
class InterviewSessionServiceImplTest {

    @InjectMocks
    private InterviewSessionServiceImpl service;

    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private InterviewRecordRepository interviewRecordRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QuestionSetRepository questionSetRepository;
    @Mock
    private InterviewRoomManager roomManager;
    @Mock
    private Transcriber transcriber;
    @Mock
    private TailQuestionGenerator tailQuestionGenerator;

    // Test Constants
    private static final String ROOM_ID = "TEST123";
    private static final Long CREATOR_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long QUESTION_SET_ID = 1L;
    private static final Long INTERVIEW_ID = 100L;
    private static final String TRANSCRIBED_TEXT = "테스트 답변입니다";
    private static final String QUESTION_1_TEXT = "자기소개를 해주세요";
    private static final String QUESTION_2_TEXT = "지원동기는 무엇인가요";
    private static final String CUSTOM_QUESTION_TEXT = "커스텀 질문";
    private static final String TAIL_QUESTION_1 = "구체적으로 설명해주세요";
    private static final String TAIL_QUESTION_2 = "어려웠던 점은 무엇인가요";
    private static final String AUDIO_BASE64 = "GkXfo0OBAkKFgQIYU4BnAQAAAAAAAHTEU2bdgX+Wws+XmiHnQs+EaOFjhDMAAUABvABMAfWD" +
            "bwPfAMwA5QBZrmtTsKRFH6NLZvDLLFKTpUHnQs+EaOFjhDMABMAJAwCF2cEAAAAA8QEBAcABPABg" +
            "AICAYwDyQEAAaABgAHgEcABRyEpv1Kb4EjSEVPYABDGvMDPUMQIR+CAAIi8wMlIAAKwByAUkm8AL" +
            "AB0Lk8AAH2wByA=="; // 테스트용 WebM Base64 데이터

    // Mock Objects
    private User mockCreator;
    private User mockUser;
    private QuestionSet mockQuestionSet;
    private Interview mockInterview;
    private InterviewRoom mockRoom;
    private InterviewSession mockSession;

    /**
     * 최소한의 공통 설정만 수행
     */
    @BeforeEach
    void setUpCommon() {
        // Mock 객체만 초기화 - 스텁 설정은 각 테스트에서
        mockCreator = mock(User.class);
        mockUser = mock(User.class);
        mockQuestionSet = mock(QuestionSet.class);
        mockInterview = mock(Interview.class);
        mockRoom = mock(InterviewRoom.class);
        mockSession = mock(InterviewSession.class);
    }

    @Nested
    @DisplayName("방 생성 테스트")
    class CreateRoomTest {
        @BeforeEach
        void setUp() {
            // 방 생성 테스트에서만 필요한 설정
            setupQuestionSetQuestionMaps();
        }

        @Test
        @DisplayName("혼자 연습하기 방 생성 성공")
        void createSoloRoom_Success() {
            // given
            when(roomManager.createSoloRoom(eq(QUESTION_SET_ID), eq(CREATOR_ID), any(InterviewSession.class)))
                    .thenReturn(ROOM_ID);
            // when
            String roomId = service.createSoloRoom(QUESTION_SET_ID, CREATOR_ID);
            // then
            assertThat(roomId).isEqualTo(ROOM_ID);
            verify(questionSetRepository).findById(QUESTION_SET_ID);
            verify(roomManager).createSoloRoom(eq(QUESTION_SET_ID), eq(CREATOR_ID), any(InterviewSession.class));
        }

        @Test
        @DisplayName("함께 연습하기 방 생성 성공")
        void createPairRoom_Success() {
            // given
            when(roomManager.createPairRoom(eq(QUESTION_SET_ID), eq(CREATOR_ID), any(InterviewSession.class)))
                    .thenReturn(ROOM_ID);
            // when
            String roomId = service.createPairRoom(QUESTION_SET_ID, CREATOR_ID);
            // then
            assertThat(roomId).isEqualTo(ROOM_ID);
            verify(questionSetRepository).findById(QUESTION_SET_ID);
            verify(roomManager).createPairRoom(eq(QUESTION_SET_ID), eq(CREATOR_ID), any(InterviewSession.class));
        }

        private void setupQuestionSetQuestionMaps() {
            Question question1 = mock(Question.class);
            Question question2 = mock(Question.class);
            when(question1.getId()).thenReturn(1L);
            when(question2.getId()).thenReturn(2L);
            when(question1.getContent()).thenReturn(QUESTION_1_TEXT);
            when(question2.getContent()).thenReturn(QUESTION_2_TEXT);
            QuestionSetQuestionMap map1 = mock(QuestionSetQuestionMap.class);
            QuestionSetQuestionMap map2 = mock(QuestionSetQuestionMap.class);
            when(map1.getQuestion()).thenReturn(question1);
            when(map2.getQuestion()).thenReturn(question2);
            when(map1.getDisplayOrder()).thenReturn(1);
            when(map2.getDisplayOrder()).thenReturn(2);
            List<QuestionSetQuestionMap> questionMaps = List.of(map1, map2);
            when(questionSetRepository.findById(QUESTION_SET_ID)).thenReturn(java.util.Optional.of(mockQuestionSet));
            when(mockQuestionSet.getQuestionMaps()).thenReturn(questionMaps);
        }
    }

    @Nested
    @DisplayName("면접 시작 테스트")
    class StartInterviewTest {
        @Test
        @DisplayName("면접 시작 성공")
        void startInterview_Success() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockInterview.getInterviewId()).thenReturn(INTERVIEW_ID);
            when(mockRoom.getCreatorId()).thenReturn(CREATOR_ID);
            when(mockRoom.getInterviewerId()).thenReturn(CREATOR_ID);
            when(mockRoom.getIntervieweeId()).thenReturn(USER_ID);
            when(mockRoom.getQuestionSetId()).thenReturn(QUESTION_SET_ID);
            when(userRepository.findById(CREATOR_ID)).thenReturn(Optional.of(mockCreator));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
            when(questionSetRepository.findById(QUESTION_SET_ID)).thenReturn(Optional.of(mockQuestionSet));
            when(interviewRepository.save(any(Interview.class))).thenReturn(mockInterview);
            // when
            Long interviewId = service.startInterview(ROOM_ID, CREATOR_ID);
            // then
            assertThat(interviewId).isEqualTo(INTERVIEW_ID);
            verify(roomManager).startInterview(ROOM_ID, INTERVIEW_ID);
            verify(interviewRepository).save(any(Interview.class));
        }

        @Test
        @DisplayName("권한 없는 사용자가 면접 시작 시도 시 실패")
        void startInterview_Fail_NoPermission() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.getCreatorId()).thenReturn(CREATOR_ID);
            // when & then
            assertThatThrownBy(() -> service.startInterview(ROOM_ID, USER_ID))
                    .isInstanceOf(InterviewSessionException.class);
        }

        @Test
        @DisplayName("존재하지 않는 질문셋으로 면접 시작 시 실패")
        void startInterview_Fail_QuestionSetNotFound() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.getCreatorId()).thenReturn(CREATOR_ID);
            when(mockRoom.getInterviewerId()).thenReturn(CREATOR_ID);
            when(mockRoom.getIntervieweeId()).thenReturn(USER_ID);
            when(mockRoom.getQuestionSetId()).thenReturn(QUESTION_SET_ID);
            when(userRepository.findById(CREATOR_ID)).thenReturn(Optional.of(mockCreator));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
            when(questionSetRepository.findById(QUESTION_SET_ID)).thenReturn(Optional.empty());
            // when & then
            assertThatThrownBy(() -> service.startInterview(ROOM_ID, CREATOR_ID))
                    .isInstanceOf(InterviewSessionException.class);
        }
    }

    @Nested
    @DisplayName("음성 답변 처리 테스트")
    class AudioAnswerTest {
        @Test
        @DisplayName("음성 답변 처리 성공")
        void processAudioAnswer_Success() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(roomManager.getSession(ROOM_ID)).thenReturn(mockSession);
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getIntervieweeId()).thenReturn(USER_ID);
            when(transcriber.transcribe(anyString())).thenReturn(TRANSCRIBED_TEXT);
            // when
            String result = service.processAudioAnswer(ROOM_ID, USER_ID, AUDIO_BASE64);
            // then
            assertThat(result).isEqualTo(TRANSCRIBED_TEXT);
            verify(transcriber).transcribe(anyString());
        }

        @Test
        @DisplayName("면접자가 아닌 사용자가 답변 시도 시 실패")
        void processAudioAnswer_Fail_NotInterviewee() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.getIntervieweeId()).thenReturn(USER_ID);  // 면접자는 USER_ID
            // isStarted() 설정 제거 - 첫 번째 검증에서 실패하므로 호출되지 않음
            // when & then - CREATOR_ID가 답변 시도 (권한 없음)
            assertThatThrownBy(() -> service.processAudioAnswer(ROOM_ID, CREATOR_ID, AUDIO_BASE64))
                    .isInstanceOf(InterviewSessionException.class);
        }

        @Test
        @DisplayName("면접 시작 전 답변 시도 시 실패")
        void processAudioAnswer_Fail_InterviewNotStarted() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.getIntervieweeId()).thenReturn(USER_ID);
            when(mockRoom.isStarted()).thenReturn(false);  // 면접 시작 전
            // when & then
            assertThatThrownBy(() -> service.processAudioAnswer(ROOM_ID, USER_ID, AUDIO_BASE64))
                    .isInstanceOf(InterviewRoomException.class);
        }

        @Test
        @DisplayName("STT 처리 실패 시 예외 발생")
        void processAudioAnswer_Fail_TranscriptionError() {
            // given - 권한 검증은 통과하도록 설정
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getIntervieweeId()).thenReturn(USER_ID);
            // STT 처리 실패 설정
            when(transcriber.transcribe(anyString())).thenThrow(new RuntimeException("STT Error"));

            // when & then
            assertThatThrownBy(() -> service.processAudioAnswer(ROOM_ID, USER_ID, AUDIO_BASE64))
                    .isInstanceOf(AudioProcessingException.class);
        }
    }

    @Nested
    @DisplayName("꼬리질문 생성 테스트")
    class TailQuestionTest {
        @Test
        @DisplayName("꼬리질문 생성 성공")
        void generateTailQuestions_Success() {
            // given
            when(roomManager.getSession(ROOM_ID)).thenReturn(mockSession);
            List<String> gptQuestions = List.of(TAIL_QUESTION_1, TAIL_QUESTION_2);
            when(tailQuestionGenerator.generateTailQuestions(any())).thenReturn(gptQuestions);
            List<QuestionForm> expectedQuestions = List.of(
                    new QuestionForm(TAIL, 1, TAIL_QUESTION_1),
                    new QuestionForm(TAIL, 2, TAIL_QUESTION_2),
                    new QuestionForm(DEFAULT, 2, QUESTION_2_TEXT)
            );
            when(mockSession.getNextQuestions(gptQuestions)).thenReturn(expectedQuestions);
            // when
            List<QuestionForm> result = service.generateTailQuestions(ROOM_ID);
            // then
            assertThat(result).hasSize(3);
            List<QuestionForm> tailQuestions = result.stream()
                    .filter(q -> q.getQuestionType() == TAIL)
                    .toList();
            assertThat(tailQuestions).hasSize(2);
            List<QuestionForm> defaultQuestions = result.stream()
                    .filter(q -> q.getQuestionType() == DEFAULT)
                    .toList();
            assertThat(defaultQuestions).hasSize(1);
            verify(tailQuestionGenerator).generateTailQuestions(any());
        }

        @Test
        @DisplayName("꼬리질문 생성 실패 시 예외 발생")
        void generateTailQuestions_Fail() {
            // given
            when(roomManager.getSession(ROOM_ID)).thenReturn(mockSession);
            when(tailQuestionGenerator.generateTailQuestions(any())).thenThrow(new RuntimeException("GPT Error"));
            // when & then
            assertThatThrownBy(() -> service.generateTailQuestions(ROOM_ID))
                    .isInstanceOf(TailQuestionException.class);
        }
    }

    @Nested
    @DisplayName("방 나가기 테스트")
    class ExitRoomTest {
        @Test
        @DisplayName("면접 시작 전 방 나가기 - DB 저장 없음")
        void exitRoom_BeforeInterview() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.isStarted()).thenReturn(false);
            // when
            service.exitRoom(ROOM_ID, USER_ID);
            // then
            verify(roomManager).exitRoom(ROOM_ID, USER_ID);
            verify(interviewRepository, never()).findById(any());
        }

        @Test
        @DisplayName("면접 진행 중 방 나가기 - DB 저장됨")
        void exitRoom_DuringInterview() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getInterviewId()).thenReturn(INTERVIEW_ID);
            when(mockRoom.getSession()).thenReturn(mockSession);
            when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(mockInterview));
            // 면접 진행 중 질문-답변 데이터 설정
            QuestionForm question1 = new QuestionForm(DEFAULT, 1, QUESTION_1_TEXT);
            QuestionForm question2 = new QuestionForm(CUSTOM, 2, CUSTOM_QUESTION_TEXT);
            Map<QuestionForm, String> questionAnswers = Map.of(
                    question1, "진행 중 답변 1",
                    question2, "진행 중 답변 2"
            );
            when(mockSession.getQuestionAnswer()).thenReturn(questionAnswers);
            // DB 저장 관련 Mock 설정
            Question mockQuestion1 = mock(Question.class);
            Question mockQuestion2 = mock(Question.class);
            Answer mockAnswer1 = mock(Answer.class);
            Answer mockAnswer2 = mock(Answer.class);
            when(questionRepository.findById(1L)).thenReturn(Optional.of(mockQuestion1));
            when(questionRepository.save(any(Question.class))).thenReturn(mockQuestion2);
            when(answerRepository.save(any(Answer.class)))
                    .thenReturn(mockAnswer1)
                    .thenReturn(mockAnswer2);
            when(mockInterview.getInterviewee()).thenReturn(mockUser);
            // when
            service.exitRoom(ROOM_ID, USER_ID);
            // then
            // 면접 완료 처리 검증
            verify(interviewRepository).findById(INTERVIEW_ID);
            verify(mockInterview).complete();
            // DB 저장 로직 검증 (saveInterviewData 호출 결과)
            verify(questionRepository).findById(1L); // 기본 질문 조회
            verify(questionRepository, times(1)).save(any(Question.class)); // 커스텀 질문 저장
            verify(answerRepository, times(2)).save(any(Answer.class)); // 답변 2개 저장
            verify(interviewRecordRepository, times(2)).save(any(InterviewRecord.class)); // 면접 기록 2개 저장
            // 마지막에 방에서 사용자 제거
            verify(roomManager).exitRoom(ROOM_ID, USER_ID);
        }
    }

    @Nested
    @DisplayName("면접 완료 테스트")
    class CompleteInterviewTest {
        @Test
        @DisplayName("면접 완료 성공 - DB 저장까지 포함")
        void completeInterview_Success() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getInterviewId()).thenReturn(INTERVIEW_ID);
            when(mockRoom.getSession()).thenReturn(mockSession);
            when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(mockInterview));
            // 질문-답변 데이터 Mock 설정 (DB 저장 로직 테스트용)
            QuestionForm defaultQuestion = new QuestionForm(DEFAULT, 1, QUESTION_1_TEXT);
            QuestionForm customQuestion = new QuestionForm(CUSTOM, 2, CUSTOM_QUESTION_TEXT);
            Map<QuestionForm, String> questionAnswers = Map.of(
                    defaultQuestion, "기본 질문 답변",
                    customQuestion, "커스텀 질문 답변"
            );
            when(mockSession.getQuestionAnswer()).thenReturn(questionAnswers);
            // DB 저장 관련 Mock 설정
            Question mockDefaultQuestion = mock(Question.class);
            Question mockCustomQuestion = mock(Question.class);
            Answer mockDefaultAnswer = mock(Answer.class);
            Answer mockCustomAnswer = mock(Answer.class);
            when(questionRepository.findById(1L)).thenReturn(Optional.of(mockDefaultQuestion));
            when(questionRepository.save(any(Question.class))).thenReturn(mockCustomQuestion);
            when(answerRepository.save(any(Answer.class)))
                    .thenReturn(mockDefaultAnswer)
                    .thenReturn(mockCustomAnswer);
            when(mockInterview.getInterviewee()).thenReturn(mockUser);
            // when
            service.completeInterview(ROOM_ID);
            // then
            verify(mockInterview).complete();
            verify(roomManager).completeInterview(ROOM_ID);
            // DB 저장 로직 검증
            verify(questionRepository).findById(1L); // 기본 질문 조회
            verify(questionRepository).save(any(Question.class)); // 커스텀 질문 저장
            verify(answerRepository, times(2)).save(any(Answer.class)); // 답변 2개 저장
            verify(interviewRecordRepository, times(2)).save(any(InterviewRecord.class)); // 면접 기록 2개 저장
        }

        @Test
        @DisplayName("면접 시작 전 완료 시도 시 실패")
        void completeInterview_Fail_NotStarted() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.isStarted()).thenReturn(false);
            // when & then
            assertThatThrownBy(() -> service.completeInterview(ROOM_ID))
                    .isInstanceOf(InterviewRoomException.class);
            // DB 저장 로직이 호출되지 않았는지 확인
            verify(interviewRepository, never()).findById(any());
            verify(questionRepository, never()).save(any());
            verify(answerRepository, never()).save(any());
            verify(interviewRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("빈 답변이 있을 때도 Answer와 InterviewRecord가 저장되어야 함")
        void completeInterview_WithEmptyAnswer() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getInterviewId()).thenReturn(INTERVIEW_ID);
            when(mockRoom.getSession()).thenReturn(mockSession);
            when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(mockInterview));
            // 빈 답변이 포함된 질문-답변 데이터
            QuestionForm question1 = new QuestionForm(DEFAULT, 1, QUESTION_1_TEXT);
            QuestionForm question2 = new QuestionForm(DEFAULT, 2, QUESTION_2_TEXT);
            Map<QuestionForm, String> questionAnswers = Map.of(
                    question1, "정상 답변",
                    question2, ""  // 빈 답변
            );
            when(mockSession.getQuestionAnswer()).thenReturn(questionAnswers);
            Question mockQuestion1 = mock(Question.class);
            Question mockQuestion2 = mock(Question.class);
            Answer mockAnswer1 = mock(Answer.class);
            when(questionRepository.findById(1L)).thenReturn(Optional.of(mockQuestion1));
            when(questionRepository.findById(2L)).thenReturn(Optional.of(mockQuestion2));
            when(answerRepository.save(any(Answer.class))).thenReturn(mockAnswer1);
            when(mockInterview.getInterviewee()).thenReturn(mockUser);
            // when
            service.completeInterview(ROOM_ID);
            // then
            // 빈 답변에 대해서도 Answer 저장이 2번 호출되어야 함
            verify(answerRepository, times(2)).save(any(Answer.class));
            verify(interviewRecordRepository, times(2)).save(any(InterviewRecord.class));
        }

        @Test
        @DisplayName("기본 질문 → 꼬리질문 저장 시 현재 질문이 부모로 설정됨")
        void completeInterview_DefaultQuestionToTailQuestion() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getInterviewId()).thenReturn(INTERVIEW_ID);
            when(mockRoom.getSession()).thenReturn(mockSession);
            when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(mockInterview));
            
            // 기본 질문 → 꼬리질문 순서의 질문-답변 데이터 (LinkedHashMap으로 순서 보장)
            QuestionForm defaultQuestion = new QuestionForm(DEFAULT, 1, QUESTION_1_TEXT);
            QuestionForm tailQuestion = new QuestionForm(TAIL, 2, TAIL_QUESTION_1);
            LinkedHashMap<QuestionForm, String> questionAnswers = new LinkedHashMap<>();
            questionAnswers.put(defaultQuestion, "기본 질문 답변");
            questionAnswers.put(tailQuestion, "꼬리 질문 답변");
            
            when(mockSession.getQuestionAnswer()).thenReturn(questionAnswers);
            
            Question mockDefaultQuestion = mock(Question.class);
            Answer mockAnswer1 = mock(Answer.class);
            Answer mockAnswer2 = mock(Answer.class);
            
            // 기본 질문 조회 (저장하지 않음)
            when(questionRepository.findById(1L)).thenReturn(Optional.of(mockDefaultQuestion));
            // 꼬리질문 저장 시 실제 생성된 Question 객체를 그대로 반환하도록 설정
            when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            when(answerRepository.save(any(Answer.class)))
                    .thenReturn(mockAnswer1)
                    .thenReturn(mockAnswer2);
            when(mockInterview.getInterviewee()).thenReturn(mockUser);
            
            // when
            service.completeInterview(ROOM_ID);
            
            // then
            verify(questionRepository).findById(1L); // 기본 질문 조회
            
            // ArgumentCaptor로 저장된 Question 객체 검증 (꼬리질문만 저장됨)
            ArgumentCaptor<Question> questionCaptor = ArgumentCaptor.forClass(Question.class);
            verify(questionRepository).save(questionCaptor.capture()); // save() 호출 검증 (횟수 확인 안함)
            
            Question savedTailQuestion = questionCaptor.getValue();
            
            // 디버깅: 실제 저장된 객체 확인
            System.out.println("=== DEBUG INFO ===");
            System.out.println("Captured Question Type: " + savedTailQuestion.getQuestionType());
            System.out.println("Expected: " + com.kkori.entity.QuestionType.TAIL);
            System.out.println("Question Content: " + savedTailQuestion.getContent());
            System.out.println("Question Parent: " + savedTailQuestion.getParent());
            
            // 실제 Question.createTail() 동작 확인
            try {
                Question testTailQuestion = Question.createTail("테스트 꼬리질문", mockDefaultQuestion);
                System.out.println("Direct createTail() Type: " + testTailQuestion.getQuestionType());
                System.out.println("Direct createTail() Parent: " + testTailQuestion.getParent());
            } catch (Exception e) {
                System.out.println("createTail() ERROR: " + e.getMessage());
            }
            System.out.println("==================");
            
            // 실제 Question.createTail()로 생성된 객체 검증
            assertThat(savedTailQuestion.getQuestionType()).isEqualTo(com.kkori.entity.QuestionType.TAIL);
            assertThat(savedTailQuestion.getParent()).isEqualTo(mockDefaultQuestion);
            assertThat(savedTailQuestion.getContent()).isEqualTo(TAIL_QUESTION_1);
            
            verify(answerRepository, times(2)).save(any(Answer.class)); // 답변 2개 저장
            verify(interviewRecordRepository, times(2)).save(any(InterviewRecord.class)); // 면접 기록 2개 저장
        }

        @Test
        @DisplayName("커스텀 질문 → 꼬리질문 저장 시 현재 질문이 부모로 설정됨")
        void completeInterview_CustomQuestionToTailQuestion() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getInterviewId()).thenReturn(INTERVIEW_ID);
            when(mockRoom.getSession()).thenReturn(mockSession);
            when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(mockInterview));
            
            // 커스텀 질문 → 꼬리질문 순서의 질문-답변 데이터
            QuestionForm customQuestion = new QuestionForm(CUSTOM, 1, CUSTOM_QUESTION_TEXT);
            QuestionForm tailQuestion = new QuestionForm(TAIL, 2, TAIL_QUESTION_1);
            LinkedHashMap<QuestionForm, String> questionAnswers = new LinkedHashMap<>();
            questionAnswers.put(customQuestion, "커스텀 질문 답변");
            questionAnswers.put(tailQuestion, "꼬리 질문 답변");
            
            when(mockSession.getQuestionAnswer()).thenReturn(questionAnswers);
            
            Question mockCustomQuestion = mock(Question.class);
            Question mockTailQuestion = mock(Question.class);
            Answer mockAnswer1 = mock(Answer.class);
            Answer mockAnswer2 = mock(Answer.class);
            
            // 질문 저장 - 순서대로 반환
            when(questionRepository.save(any(Question.class)))
                    .thenReturn(mockCustomQuestion)  // 첫 번째: 커스텀 질문
                    .thenReturn(mockTailQuestion);   // 두 번째: 꼬리질문
            
            when(answerRepository.save(any(Answer.class)))
                    .thenReturn(mockAnswer1)
                    .thenReturn(mockAnswer2);
            when(mockInterview.getInterviewee()).thenReturn(mockUser);
            
            // when
            service.completeInterview(ROOM_ID);
            
            // then
            // ArgumentCaptor로 저장된 Question 객체들 검증
            ArgumentCaptor<Question> questionCaptor = ArgumentCaptor.forClass(Question.class);
            verify(questionRepository, times(2)).save(questionCaptor.capture());
            
            List<Question> savedQuestions = questionCaptor.getAllValues();
            
            // 첫 번째 저장: 커스텀 질문
            Question savedCustomQuestion = savedQuestions.get(0);
            assertThat(savedCustomQuestion.getQuestionType()).isEqualTo(com.kkori.entity.QuestionType.CUSTOM);
            assertThat(savedCustomQuestion.getContent()).isEqualTo(CUSTOM_QUESTION_TEXT);
            
            // 두 번째 저장: 꼬리질문 (커스텀 질문이 부모)
            Question savedTailQuestion = savedQuestions.get(1);
            assertThat(savedTailQuestion.getQuestionType()).isEqualTo(com.kkori.entity.QuestionType.TAIL);
            assertThat(savedTailQuestion.getParent()).isEqualTo(mockCustomQuestion);
            assertThat(savedTailQuestion.getContent()).isEqualTo(TAIL_QUESTION_1);
            
            verify(answerRepository, times(2)).save(any(Answer.class));
            verify(interviewRecordRepository, times(2)).save(any(InterviewRecord.class));
        }

        @Test
        @DisplayName("복합 시나리오: 기본질문 → 꼬리질문 → 커스텀질문 → 꼬리질문")
        void completeInterview_ComplexScenario() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getInterviewId()).thenReturn(INTERVIEW_ID);
            when(mockRoom.getSession()).thenReturn(mockSession);
            when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(mockInterview));
            
            // 복합 시나리오: DEFAULT → TAIL → CUSTOM → TAIL
            QuestionForm defaultQuestion = new QuestionForm(DEFAULT, 1, QUESTION_1_TEXT);
            QuestionForm tailQuestion1 = new QuestionForm(TAIL, 2, TAIL_QUESTION_1);
            QuestionForm customQuestion = new QuestionForm(CUSTOM, 3, CUSTOM_QUESTION_TEXT);
            QuestionForm tailQuestion2 = new QuestionForm(TAIL, 4, TAIL_QUESTION_2);
            
            LinkedHashMap<QuestionForm, String> questionAnswers = new LinkedHashMap<>();
            questionAnswers.put(defaultQuestion, "기본 질문 답변");
            questionAnswers.put(tailQuestion1, "첫 번째 꼬리질문 답변");
            questionAnswers.put(customQuestion, "커스텀 질문 답변");
            questionAnswers.put(tailQuestion2, "두 번째 꼬리질문 답변");
            
            when(mockSession.getQuestionAnswer()).thenReturn(questionAnswers);
            
            Question mockDefaultQuestion = mock(Question.class);
            Question mockTailQuestion1 = mock(Question.class);
            Question mockCustomQuestion = mock(Question.class);
            Question mockTailQuestion2 = mock(Question.class);
            
            // 기본 질문 조회
            when(questionRepository.findById(1L)).thenReturn(Optional.of(mockDefaultQuestion));
            
            // 질문 저장 순서대로 반환
            when(questionRepository.save(any(Question.class)))
                    .thenReturn(mockTailQuestion1)    // 첫 번째 꼬리질문
                    .thenReturn(mockCustomQuestion)   // 커스텀 질문  
                    .thenReturn(mockTailQuestion2);   // 두 번째 꼬리질문
            
            when(answerRepository.save(any(Answer.class)))
                    .thenReturn(mock(Answer.class))
                    .thenReturn(mock(Answer.class))
                    .thenReturn(mock(Answer.class))
                    .thenReturn(mock(Answer.class));
            when(mockInterview.getInterviewee()).thenReturn(mockUser);
            
            // when
            service.completeInterview(ROOM_ID);
            
            // then
            verify(questionRepository).findById(1L); // 기본 질문 조회
            
            // ArgumentCaptor로 저장된 Question 객체들 검증
            ArgumentCaptor<Question> questionCaptor = ArgumentCaptor.forClass(Question.class);
            verify(questionRepository, times(3)).save(questionCaptor.capture());
            
            List<Question> savedQuestions = questionCaptor.getAllValues();
            
            // 첫 번째 저장: 꼬리질문 (기본 질문을 부모로)
            Question savedTailQuestion1 = savedQuestions.get(0);
            assertThat(savedTailQuestion1.getQuestionType()).isEqualTo(com.kkori.entity.QuestionType.TAIL);
            assertThat(savedTailQuestion1.getParent()).isEqualTo(mockDefaultQuestion);
            
            // 두 번째 저장: 커스텀 질문
            Question savedCustomQuestion = savedQuestions.get(1);
            assertThat(savedCustomQuestion.getQuestionType()).isEqualTo(com.kkori.entity.QuestionType.CUSTOM);
            
            // 세 번째 저장: 꼬리질문 (커스텀 질문을 부모로)
            Question savedTailQuestion2 = savedQuestions.get(2);
            assertThat(savedTailQuestion2.getQuestionType()).isEqualTo(com.kkori.entity.QuestionType.TAIL);
            assertThat(savedTailQuestion2.getParent()).isEqualTo(mockCustomQuestion);
            
            verify(answerRepository, times(4)).save(any(Answer.class));
            verify(interviewRecordRepository, times(4)).save(any(InterviewRecord.class));
        }
    }

    @Nested
    @DisplayName("세션 관리 테스트")
    class SessionManagementTest {
        @BeforeEach
        void setup() {
            when(roomManager.getSession(ROOM_ID)).thenReturn(mockSession);
        }

        @Test
        @DisplayName("현재 질문 조회")
        void getCurrentQuestion() {
            // given
            QuestionForm currentQuestion = new QuestionForm(DEFAULT, 1, QUESTION_1_TEXT);
            when(mockSession.getCurrentQuestion()).thenReturn(currentQuestion);
            // when
            QuestionForm result = service.getCurrentQuestion(ROOM_ID);
            // then
            assertThat(result).isEqualTo(currentQuestion);
        }

        @Test
        @DisplayName("질문 선택")
        void selectQuestion() {
            // given
            QuestionForm selectedQuestion = new QuestionForm(CUSTOM, 1, CUSTOM_QUESTION_TEXT);
            when(mockSession.selectQuestion(CUSTOM, 1, CUSTOM_QUESTION_TEXT)).thenReturn(selectedQuestion);
            // when
            QuestionForm result = service.selectQuestion(ROOM_ID, CUSTOM, 1, CUSTOM_QUESTION_TEXT);
            // then
            assertThat(result).isEqualTo(selectedQuestion);
        }

        @Test
        @DisplayName("커스텀 질문 생성")
        void createCustomQuestion() {
            // given
            QuestionForm customQuestion = new QuestionForm(CUSTOM, 1, CUSTOM_QUESTION_TEXT);

            when(roomManager.getSession(ROOM_ID)).thenReturn(mockSession);
            when(transcriber.transcribe(anyString())).thenReturn(CUSTOM_QUESTION_TEXT);
            when(mockSession.createCustomQuestion(CUSTOM_QUESTION_TEXT)).thenReturn(customQuestion);
            
            // when
            QuestionForm result = service.createCustomQuestion(ROOM_ID, AUDIO_BASE64);

            // then
            assertThat(result).isEqualTo(customQuestion);
            verify(transcriber).transcribe(anyString());
            verify(mockSession).createCustomQuestion(CUSTOM_QUESTION_TEXT);
        }
    }
    
    @Nested
    @DisplayName("유틸리티 메서드 테스트")
    class UtilityTest {

        @Test
        @DisplayName("방 참여")
        void joinRoom() {
            // when
            service.joinRoom(ROOM_ID, USER_ID);
            // then
            verify(roomManager).joinRoom(ROOM_ID, USER_ID);
        }

        @Test
        @DisplayName("역할 변경")
        void swapRoles() {
            // given
            when(roomManager.getRoom(ROOM_ID)).thenReturn(mockRoom);
            
            // roomManager.swapRoles()가 실제 동작을 하도록 설정
            doAnswer(invocation -> {
                InterviewRoom room = roomManager.getRoom(ROOM_ID);
                room.swapRoles();
                return null;
            }).when(roomManager).swapRoles(ROOM_ID);

            // when
            service.swapRoles(ROOM_ID);
            // then
            verify(roomManager).swapRoles(ROOM_ID);
            verify(mockRoom).swapRoles();
        }

        @Test
        @DisplayName("참여 가능 여부 확인")
        void canJoinRoom() {
            // given
            when(roomManager.canJoinRoom(ROOM_ID)).thenReturn(true);
            // when
            boolean result = service.canJoinRoom(ROOM_ID);
            // then
            assertThat(result).isTrue();
            verify(roomManager).canJoinRoom(ROOM_ID);
        }

        @Test
        @DisplayName("면접 시작 가능 여부 확인")
        void canStartInterview() {
            // given
            when(roomManager.canStartInterview(ROOM_ID)).thenReturn(true);
            // when
            boolean result = service.canStartInterview(ROOM_ID);
            // then
            assertThat(result).isTrue();
            verify(roomManager).canStartInterview(ROOM_ID);
        }
    }

}