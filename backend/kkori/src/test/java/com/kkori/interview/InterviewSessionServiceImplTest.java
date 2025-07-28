package com.kkori.interview;

import com.kkori.component.InterviewRoomManager;
import com.kkori.component.TailQuestionGenerator;
import com.kkori.component.Transcriber;
import com.kkori.component.interview.InterviewRoom;
import com.kkori.component.interview.InterviewSession;
import com.kkori.component.interview.QuestionForm;
import com.kkori.component.interview.QuestionType;
import com.kkori.component.interview.RoomStatus;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterviewSessionService 구현 테스트")
class InterviewSessionServiceImplTest {

    @InjectMocks
    private InterviewSessionServiceImpl service;

    @Mock private InterviewRepository interviewRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private InterviewRecordRepository interviewRecordRepository;
    @Mock private UserRepository userRepository;
    @Mock private QuestionSetRepository questionSetRepository;
    @Mock private QuestionSetItemRepository questionSetItemRepository;

    @Mock private InterviewRoomManager roomManager;
    @Mock private Transcriber transcriber;
    @Mock private TailQuestionGenerator tailQuestionGenerator;

    // Test Constants
    private static final String ROOM_ID = "TEST123";
    private static final Long CREATOR_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long QUESTION_SET_ID = 1L;
    private static final Long INTERVIEW_ID = 100L;
    private static final String AUDIO_FILE_PATH = "demo.m4a";
    private static final String TRANSCRIBED_TEXT = "테스트 답변입니다";
    private static final String QUESTION_1_TEXT = "자기소개를 해주세요";
    private static final String QUESTION_2_TEXT = "지원동기는 무엇인가요";
    private static final String CUSTOM_QUESTION_TEXT = "커스텀 질문";
    private static final String TAIL_QUESTION_1 = "구체적으로 설명해주세요";
    private static final String TAIL_QUESTION_2 = "어려웠던 점은 무엇인가요";

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
            setupQuestionSetItems();
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
            verify(questionSetItemRepository).findByQuestionSet_SetIdOrderBySortOrderAsc(QUESTION_SET_ID);
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
            verify(questionSetItemRepository).findByQuestionSet_SetIdOrderBySortOrderAsc(QUESTION_SET_ID);
            verify(roomManager).createPairRoom(eq(QUESTION_SET_ID), eq(CREATOR_ID), any(InterviewSession.class));
        }

        private void setupQuestionSetItems() {
            Question question1 = mock(Question.class);
            Question question2 = mock(Question.class);
            when(question1.getContent()).thenReturn(QUESTION_1_TEXT);
            when(question2.getContent()).thenReturn(QUESTION_2_TEXT);

            QuestionSetItem item1 = mock(QuestionSetItem.class);
            QuestionSetItem item2 = mock(QuestionSetItem.class);
            when(item1.getQuestion()).thenReturn(question1);
            when(item2.getQuestion()).thenReturn(question2);

            List<QuestionSetItem> questionSetItems = List.of(item1, item2);
            when(questionSetItemRepository.findByQuestionSet_SetIdOrderBySortOrderAsc(QUESTION_SET_ID))
                    .thenReturn(questionSetItems);
        }
    }

    @Nested
    @DisplayName("면접 시작 테스트")
    class StartInterviewTest {

        // 공통 setUp 제거 - 각 테스트에서 필요한 것만 설정

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

            // when & then
            assertThatThrownBy(() -> service.startInterview(ROOM_ID, CREATOR_ID))
                    .isInstanceOf(InterviewSessionException.class);
        }
    }

    @Nested
    @DisplayName("음성 답변 처리 테스트")
    class AudioAnswerTest {

        @BeforeEach
        void setUp() {
            // 음성 답변 테스트를 위한 설정
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getIntervieweeId()).thenReturn(USER_ID);
            when(transcriber.transcribe(AUDIO_FILE_PATH)).thenReturn(TRANSCRIBED_TEXT);
        }

        @Test
        @DisplayName("음성 답변 처리 성공")
        void processAudioAnswer_Success() {
            // when
            String result = service.processAudioAnswer(ROOM_ID, USER_ID, AUDIO_FILE_PATH);

            // then
            assertThat(result).isEqualTo(TRANSCRIBED_TEXT);
            verify(transcriber).transcribe(AUDIO_FILE_PATH);
        }

        @Test
        @DisplayName("면접자가 아닌 사용자가 답변 시도 시 실패")
        void processAudioAnswer_Fail_NotInterviewee() {
            // when & then
            assertThatThrownBy(() -> service.processAudioAnswer(ROOM_ID, CREATOR_ID, AUDIO_FILE_PATH))
                    .isInstanceOf(InterviewSessionException.class);
        }

        @Test
        @DisplayName("면접 시작 전 답변 시도 시 실패")
        void processAudioAnswer_Fail_InterviewNotStarted() {
            // given
            when(mockRoom.isStarted()).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> service.processAudioAnswer(ROOM_ID, USER_ID, AUDIO_FILE_PATH))
                    .isInstanceOf(InterviewRoomException.class);
        }

        @Test
        @DisplayName("STT 처리 실패 시 예외 발생")
        void processAudioAnswer_Fail_TranscriptionError() {
            // given
            when(transcriber.transcribe(AUDIO_FILE_PATH)).thenThrow(new RuntimeException("STT Error"));

            // when & then
            assertThatThrownBy(() -> service.processAudioAnswer(ROOM_ID, USER_ID, AUDIO_FILE_PATH))
                    .isInstanceOf(AudioProcessingException.class);
        }
    }

    @Nested
    @DisplayName("꼬리질문 생성 테스트")
    class TailQuestionTest {

        @BeforeEach
        void setUp() {
            // 꼬리질문 테스트를 위한 설정
            List<String> gptQuestions = List.of(TAIL_QUESTION_1, TAIL_QUESTION_2);
            when(tailQuestionGenerator.generateTailQuestions(any())).thenReturn(gptQuestions);

            List<QuestionForm> expectedQuestions = List.of(
                    new QuestionForm(QuestionType.TAIL, 1, TAIL_QUESTION_1),
                    new QuestionForm(QuestionType.TAIL, 2, TAIL_QUESTION_2),
                    new QuestionForm(QuestionType.DEFAULT, 2, QUESTION_2_TEXT)
            );
            when(mockSession.getNextQuestions(gptQuestions)).thenReturn(expectedQuestions);
        }

        @Test
        @DisplayName("꼬리질문 생성 성공")
        void generateTailQuestions_Success() {
            // when
            List<QuestionForm> result = service.generateTailQuestions(ROOM_ID);

            // then
            assertThat(result).hasSize(3);

            List<QuestionForm> tailQuestions = result.stream()
                    .filter(q -> q.getQuestionType() == QuestionType.TAIL)
                    .toList();
            assertThat(tailQuestions).hasSize(2);

            List<QuestionForm> defaultQuestions = result.stream()
                    .filter(q -> q.getQuestionType() == QuestionType.DEFAULT)
                    .toList();
            assertThat(defaultQuestions).hasSize(1);

            verify(tailQuestionGenerator).generateTailQuestions(any());
        }

        @Test
        @DisplayName("꼬리질문 생성 실패 시 예외 발생")
        void generateTailQuestions_Fail() {
            // given
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
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getInterviewId()).thenReturn(INTERVIEW_ID);
            when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(mockInterview));

            Map<QuestionForm, String> questionAnswers = new LinkedHashMap<>();
            when(mockSession.getQuestionAnswer()).thenReturn(questionAnswers);

            // when
            service.exitRoom(ROOM_ID, USER_ID);

            // then
            verify(interviewRepository).findById(INTERVIEW_ID);
            verify(mockInterview).complete();
            verify(roomManager).exitRoom(ROOM_ID, USER_ID);
        }
    }

    @Nested
    @DisplayName("면접 완료 테스트")
    class CompleteInterviewTest {

        @BeforeEach
        void setUp() {
            // 면접 완료 테스트를 위한 설정
            when(mockRoom.isStarted()).thenReturn(true);
            when(mockRoom.getInterviewId()).thenReturn(INTERVIEW_ID);
            when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(mockInterview));

            Map<QuestionForm, String> questionAnswers = new LinkedHashMap<>();
            when(mockSession.getQuestionAnswer()).thenReturn(questionAnswers);
        }

        @Test
        @DisplayName("면접 완료 성공")
        void completeInterview_Success() {
            // when
            service.completeInterview(ROOM_ID);

            // then
            verify(mockInterview).complete();
            verify(roomManager).completeInterview(ROOM_ID);
        }

        @Test
        @DisplayName("면접 시작 전 완료 시도 시 실패")
        void completeInterview_Fail_NotStarted() {
            // given
            when(mockRoom.isStarted()).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> service.completeInterview(ROOM_ID))
                    .isInstanceOf(InterviewRoomException.class);
        }
    }

    @Nested
    @DisplayName("세션 관리 테스트")
    class SessionManagementTest {

        @BeforeEach
        void setup(){
            when(roomManager.getSession(ROOM_ID)).thenReturn(mockSession);
        }

        @Test
        @DisplayName("현재 질문 조회")
        void getCurrentQuestion() {
            // given
            QuestionForm currentQuestion = new QuestionForm(QuestionType.DEFAULT, 1, QUESTION_1_TEXT);
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
            QuestionForm selectedQuestion = new QuestionForm(QuestionType.CUSTOM, 1, CUSTOM_QUESTION_TEXT);
            when(mockSession.selectQuestion(QuestionType.CUSTOM, 1, CUSTOM_QUESTION_TEXT)).thenReturn(selectedQuestion);

            // when
            QuestionForm result = service.selectQuestion(ROOM_ID, QuestionType.CUSTOM, 1, CUSTOM_QUESTION_TEXT);

            // then
            assertThat(result).isEqualTo(selectedQuestion);
        }

        @Test
        @DisplayName("커스텀 질문 생성")
        void createCustomQuestion() {
            // given
            QuestionForm customQuestion = new QuestionForm(QuestionType.CUSTOM, 1, CUSTOM_QUESTION_TEXT);
            when(mockSession.createCustomQuestion(CUSTOM_QUESTION_TEXT)).thenReturn(customQuestion);


            // when
            QuestionForm result = service.createCustomQuestion(ROOM_ID, CUSTOM_QUESTION_TEXT);

            // then
            assertThat(result).isEqualTo(customQuestion);
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
            // when
            service.swapRoles(ROOM_ID);

            // then
            verify(roomManager).swapRoles(ROOM_ID);
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
        }
    }
}