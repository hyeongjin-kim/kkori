package com.kkori.service;

import com.kkori.dto.interview.response.InterviewTailQuestionResponse;
import com.kkori.dto.interview.response.SubmitTailQuestionAnswerResponse;
import com.kkori.entity.Interview;
import com.kkori.entity.InterviewTailQuestion;
import com.kkori.entity.Question;
import com.kkori.entity.User;
import com.kkori.component.interview.RoomStatus;
import com.kkori.exception.interview.InterviewSessionException;
import com.kkori.exception.interview.TailQuestionException;
import com.kkori.exception.user.UserException;
import com.kkori.repository.InterviewRepository;
import com.kkori.repository.InterviewTailQuestionRepository;
import com.kkori.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewTailQuestionServiceTest {

    @Mock
    private InterviewTailQuestionRepository interviewTailQuestionRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InterviewTailQuestionServiceImpl interviewTailQuestionService;

    @Test
    @DisplayName("해피케이스: 면접 꼬리질문 목록 조회 성공")
    void getInterviewTailQuestions_Success() {
        // Given
        Long userId = 1L;
        Long interviewId = 100L;
        
        User interviewer = createUser(userId, "interviewer@test.com");
        User interviewee = createUser(2L, "interviewee@test.com");
        Interview interview = createInterview(interviewId, interviewer, interviewee);
        
        Question originalQuestion = createQuestion(1L, "Java의 특징을 설명해주세요.");
        InterviewTailQuestion tailQuestion = createTailQuestion(1L, interview, originalQuestion, "자바의 장점은 무엇인가요?");
        
        given(interviewRepository.findById(interviewId)).willReturn(Optional.of(interview));
        given(interviewTailQuestionRepository.findByInterviewIdWithDetails(interviewId))
                .willReturn(Arrays.asList(tailQuestion));

        // When
        List<InterviewTailQuestionResponse> responses = interviewTailQuestionService
                .getInterviewTailQuestions(userId, interviewId);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getContent()).isEqualTo("자바의 장점은 무엇인가요?");
        assertThat(responses.get(0).getOriginalQuestionId()).isEqualTo(1L);
        
        verify(interviewRepository).findById(interviewId);
        verify(interviewTailQuestionRepository).findByInterviewIdWithDetails(interviewId);
    }

    @Test
    @DisplayName("예외케이스: 존재하지 않는 면접 조회")
    void getInterviewTailQuestions_InterviewNotFound() {
        // Given
        Long userId = 1L;
        Long nonExistentInterviewId = 999L;
        
        given(interviewRepository.findById(nonExistentInterviewId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> 
                interviewTailQuestionService.getInterviewTailQuestions(userId, nonExistentInterviewId))
                .isInstanceOf(InterviewSessionException.class);
        
        verify(interviewRepository).findById(nonExistentInterviewId);
        verifyNoInteractions(interviewTailQuestionRepository);
    }

    @Test
    @DisplayName("예외케이스: 면접 접근 권한 없음")
    void getInterviewTailQuestions_AccessDenied() {
        // Given
        Long unauthorizedUserId = 999L;
        Long interviewId = 100L;
        
        User interviewer = createUser(1L, "interviewer@test.com");
        User interviewee = createUser(2L, "interviewee@test.com");
        Interview interview = createInterview(interviewId, interviewer, interviewee);
        
        given(interviewRepository.findById(interviewId)).willReturn(Optional.of(interview));

        // When & Then
        assertThatThrownBy(() -> 
                interviewTailQuestionService.getInterviewTailQuestions(unauthorizedUserId, interviewId))
                .isInstanceOf(InterviewSessionException.class);
        
        verify(interviewRepository).findById(interviewId);
        verifyNoInteractions(interviewTailQuestionRepository);
    }

    @Test
    @DisplayName("해피케이스: 꼬리질문 답변 제출 성공")
    void submitTailQuestionAnswer_Success() {
        // Given
        Long tailQuestionId = 1L;
        String userAnswer = "자바는 플랫폼 독립적이고 객체지향적입니다.";
        
        User interviewer = createUser(1L, "interviewer@test.com");
        User interviewee = createUser(2L, "interviewee@test.com");
        Interview interview = createInterview(100L, interviewer, interviewee);
        interview.complete(); // 완료되지 않은 면접으로 설정하려면 이 라인 제거
        
        Question originalQuestion = createQuestion(1L, "Java의 특징을 설명해주세요.");
        InterviewTailQuestion tailQuestion = createTailQuestion(tailQuestionId, interview, originalQuestion, "자바의 장점은 무엇인가요?");
        
        // 면접이 완료되지 않도록 설정
        given(interviewTailQuestionRepository.findById(tailQuestionId))
                .willReturn(Optional.of(tailQuestion));
        
        // When
        SubmitTailQuestionAnswerResponse response = interviewTailQuestionService
                .submitTailQuestionAnswer(tailQuestionId, userAnswer);

        // Then
        assertThat(response.getTailQuestionId()).isEqualTo(tailQuestionId);
        assertThat(response.getIsAnswered()).isTrue();
        assertThat(tailQuestion.getUserAnswer()).isEqualTo(userAnswer);
        assertThat(tailQuestion.isAnswered()).isTrue();
    }

    @Test
    @DisplayName("예외케이스: 존재하지 않는 꼬리질문 답변 제출")
    void submitTailQuestionAnswer_TailQuestionNotFound() {
        // Given
        Long nonExistentTailQuestionId = 999L;
        String userAnswer = "답변";
        
        given(interviewTailQuestionRepository.findById(nonExistentTailQuestionId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> 
                interviewTailQuestionService.submitTailQuestionAnswer(nonExistentTailQuestionId, userAnswer))
                .isInstanceOf(TailQuestionException.class);
        
        verify(interviewTailQuestionRepository).findById(nonExistentTailQuestionId);
    }

    @Test
    @DisplayName("예외케이스: 이미 답변한 꼬리질문에 재답변 시도")
    void submitTailQuestionAnswer_AlreadyAnswered() {
        // Given
        Long tailQuestionId = 1L;
        String userAnswer = "새로운 답변";
        
        User interviewer = createUser(1L, "interviewer@test.com");
        User interviewee = createUser(2L, "interviewee@test.com");
        Interview interview = createInterview(100L, interviewer, interviewee);
        
        Question originalQuestion = createQuestion(1L, "Java의 특징을 설명해주세요.");
        InterviewTailQuestion tailQuestion = createTailQuestion(tailQuestionId, interview, originalQuestion, "자바의 장점은 무엇인가요?");
        tailQuestion.submitAnswer("이미 제출된 답변"); // 이미 답변됨
        
        given(interviewTailQuestionRepository.findById(tailQuestionId))
                .willReturn(Optional.of(tailQuestion));

        // When & Then
        assertThatThrownBy(() -> 
                interviewTailQuestionService.submitTailQuestionAnswer(tailQuestionId, userAnswer))
                .isInstanceOf(TailQuestionException.class);
        
        verify(interviewTailQuestionRepository).findById(tailQuestionId);
    }

    @Test
    @DisplayName("예외케이스: 완료된 면접의 꼬리질문 답변 시도")
    void submitTailQuestionAnswer_CompletedInterview() {
        // Given
        Long tailQuestionId = 1L;
        String userAnswer = "답변";
        
        User interviewer = createUser(1L, "interviewer@test.com");
        User interviewee = createUser(2L, "interviewee@test.com");
        Interview interview = createInterview(100L, interviewer, interviewee);
        interview.complete(); // 면접 완료
        
        Question originalQuestion = createQuestion(1L, "Java의 특징을 설명해주세요.");
        InterviewTailQuestion tailQuestion = createTailQuestion(tailQuestionId, interview, originalQuestion, "자바의 장점은 무엇인가요?");
        
        given(interviewTailQuestionRepository.findById(tailQuestionId))
                .willReturn(Optional.of(tailQuestion));

        // When & Then
        assertThatThrownBy(() -> 
                interviewTailQuestionService.submitTailQuestionAnswer(tailQuestionId, userAnswer))
                .isInstanceOf(TailQuestionException.class);
        
        verify(interviewTailQuestionRepository).findById(tailQuestionId);
    }

    @Test
    @DisplayName("해피케이스: 사용자 최근 학습 데이터 조회 성공")
    void getRecentUserLearningData_Success() {
        // Given
        Long userId = 1L;
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        
        User user = createUser(userId, "user@test.com");
        given(userRepository.existsById(userId)).willReturn(true);
        given(interviewTailQuestionRepository.findRecentTailQuestionsByUser(userId, fromDate))
                .willReturn(Arrays.asList());

        // When
        List<InterviewTailQuestionResponse> responses = interviewTailQuestionService
                .getRecentUserLearningData(userId, fromDate);

        // Then
        assertThat(responses).isNotNull();
        verify(userRepository).existsById(userId);
        verify(interviewTailQuestionRepository).findRecentTailQuestionsByUser(userId, fromDate);
    }

    @Test
    @DisplayName("예외케이스: 존재하지 않는 사용자의 학습 데이터 조회")
    void getRecentUserLearningData_UserNotFound() {
        // Given
        Long nonExistentUserId = 999L;
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        
        given(userRepository.existsById(nonExistentUserId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> 
                interviewTailQuestionService.getRecentUserLearningData(nonExistentUserId, fromDate))
                .isInstanceOf(UserException.class);
        
        verify(userRepository).existsById(nonExistentUserId);
        verifyNoInteractions(interviewTailQuestionRepository);
    }

    // 테스트 헬퍼 메서드들
    private User createUser(Long userId, String email) {
        return User.builder()
                .userId(userId)
                .sub(email)
                .nickname("테스트사용자")
                .build();
    }

    private Interview createInterview(Long interviewId, User interviewer, User interviewee) {
        return Interview.builder()
                .interviewer(interviewer)
                .interviewee(interviewee)
                .roomId("ROOM_" + interviewId)
                .build();
    }

    private Question createQuestion(Long questionId, String content) {
        return Question.builder()
                .content(content)
                .build();
    }

    private InterviewTailQuestion createTailQuestion(Long id, Interview interview, Question originalQuestion, String content) {
        return InterviewTailQuestion.builder()
                .interview(interview)
                .originalQuestion(originalQuestion)
                .content(content)
                .originalUserAnswer("원본 답변")
                .questionOrder(1)
                .generationContext("테스트 컨텍스트")
                .build();
    }
}