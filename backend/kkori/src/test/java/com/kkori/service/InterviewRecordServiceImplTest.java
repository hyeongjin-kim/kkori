package com.kkori.service;

import com.kkori.component.interview.RoomStatus;
import com.kkori.dto.interview.response.InterviewRecordDetailResponse;
import com.kkori.dto.interview.response.InterviewRecordListResponse;
import com.kkori.entity.*;
import com.kkori.exception.interview.InterviewRecordException;
import com.kkori.repository.InterviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewRecordServiceImplTest {

    @Mock private InterviewRepository interviewRepository;
    
    @InjectMocks private InterviewRecordServiceImpl interviewRecordService;

    private User interviewer;
    private User interviewee;
    private QuestionSet questionSet;
    private Interview interview;

    @BeforeEach
    void setUp() {
        interviewer = createUser(1L, "interviewer@test.com", "면접관");
        interviewee = createUser(2L, "interviewee@test.com", "면접자");
        questionSet = createQuestionSet(1L, "Spring Boot 면접");
        interview = createInterview(1L, interviewer, interviewee, questionSet, "ROOM123");
    }

    @Test
    @DisplayName("해피케이스: 전체 면접 기록 목록 조회 성공")
    void getInterviewRecords_AllRoles_Success() {
        // Given
        Long userId = 1L;
        int page = 0, size = 10;
        String role = null; // 전체 조회
        
        Page<Interview> interviewPage = new PageImpl<>(Arrays.asList(interview));
        
        given(interviewRepository.findByUserIdAndCompleted(eq(userId), any(Pageable.class)))
                .willReturn(interviewPage);
        
        // When
        Page<InterviewRecordListResponse> result = interviewRecordService.getInterviewRecords(userId, page, size, role);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        InterviewRecordListResponse response = result.getContent().get(0);
        assertThat(response.getInterviewId()).isEqualTo(1L);
        assertThat(response.getRoomId()).isEqualTo("ROOM123");
        assertThat(response.getInterviewerNickname()).isEqualTo("면접관");
        assertThat(response.getIntervieweeNickname()).isEqualTo("면접자");
        assertThat(response.getQuestionSetTitle()).isEqualTo("Spring Boot 면접");
        assertThat(response.getUserRole()).isEqualTo("INTERVIEWER");
        
        verify(interviewRepository).findByUserIdAndCompleted(eq(userId), any(Pageable.class));
    }
    
    @Test
    @DisplayName("해피케이스: 면접관 역할 필터로 면접 기록 목록 조회 성공")
    void getInterviewRecords_InterviewerRole_Success() {
        // Given
        Long userId = 1L;
        int page = 0, size = 10;
        String role = "interviewer";
        
        Page<Interview> interviewPage = new PageImpl<>(Arrays.asList(interview));
        
        given(interviewRepository.findByInterviewerIdAndCompleted(eq(userId), any(Pageable.class)))
                .willReturn(interviewPage);
        
        // When
        Page<InterviewRecordListResponse> result = interviewRecordService.getInterviewRecords(userId, page, size, role);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserRole()).isEqualTo("INTERVIEWER");
        
        verify(interviewRepository).findByInterviewerIdAndCompleted(eq(userId), any(Pageable.class));
    }
    
    @Test
    @DisplayName("해피케이스: 면접자 역할 필터로 면접 기록 목록 조회 성공")
    void getInterviewRecords_IntervieweeRole_Success() {
        // Given
        Long userId = 2L;
        int page = 0, size = 10;
        String role = "interviewee";
        
        Page<Interview> interviewPage = new PageImpl<>(Arrays.asList(interview));
        
        given(interviewRepository.findByIntervieweeIdAndCompleted(eq(userId), any(Pageable.class)))
                .willReturn(interviewPage);
        
        // When
        Page<InterviewRecordListResponse> result = interviewRecordService.getInterviewRecords(userId, page, size, role);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserRole()).isEqualTo("INTERVIEWEE");
        
        verify(interviewRepository).findByIntervieweeIdAndCompleted(eq(userId), any(Pageable.class));
    }
    
    @Test
    @DisplayName("해피케이스: 면접 상세 기록 조회 성공")
    void getInterviewRecordDetail_Success() {
        // Given
        Long userId = 1L;
        Long interviewId = 1L;
        
        // InterviewRecord 설정
        Question question1 = Question.createDefault("Spring Boot란?", "예상답변1");
        Question question2 = Question.createTail("좀 더 자세히 설명해주세요", question1);
        setFieldValue(question1, "id", 1L);
        setFieldValue(question2, "id", 2L);
        
        Answer answer1 = createAnswer(1L, "Spring Boot는...", interviewee);
        Answer answer2 = createAnswer(2L, "더 자세한 설명...", interviewee);
        
        InterviewRecord record1 = createInterviewRecord(1L, interview, question1, answer1, 1);
        InterviewRecord record2 = createInterviewRecord(2L, interview, question2, answer2, 2);
        
        interview.getInterviewRecords().addAll(Arrays.asList(record1, record2));
        
        given(interviewRepository.findByIdAndUserIdWithRecords(interviewId, userId))
                .willReturn(Optional.of(interview));
        
        // When
        InterviewRecordDetailResponse result = interviewRecordService.getInterviewRecordDetail(userId, interviewId);
        
        // Then
        assertThat(result.getInterviewId()).isEqualTo(1L);
        assertThat(result.getRoomId()).isEqualTo("ROOM123");
        assertThat(result.getUserRole()).isEqualTo("INTERVIEWER");
        assertThat(result.getQuestionAnswers()).hasSize(2);
        
        // 첫 번째 질문-답변 검증
        assertThat(result.getQuestionAnswers().get(0).getOrderNum()).isEqualTo(1);
        assertThat(result.getQuestionAnswers().get(0).getQuestionType()).isEqualTo(QuestionType.DEFAULT);
        assertThat(result.getQuestionAnswers().get(0).getParentQuestionId()).isNull();
        
        // 두 번째 질문-답변 검증 (꼬리 질문)
        assertThat(result.getQuestionAnswers().get(1).getOrderNum()).isEqualTo(2);
        assertThat(result.getQuestionAnswers().get(1).getQuestionType()).isEqualTo(QuestionType.TAIL);
        assertThat(result.getQuestionAnswers().get(1).getParentQuestionId()).isEqualTo(1L);
        assertThat(result.getQuestionAnswers().get(1).getParentQuestionContent()).isEqualTo("Spring Boot란?");
        
        verify(interviewRepository).findByIdAndUserIdWithRecords(interviewId, userId);
    }
    
    @Test
    @DisplayName("예외케이스: 존재하지 않는 면접 상세 조회")
    void getInterviewRecordDetail_NotFound() {
        // Given
        Long userId = 1L;
        Long nonExistentInterviewId = 999L;
        
        given(interviewRepository.findByIdAndUserIdWithRecords(nonExistentInterviewId, userId))
                .willReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> interviewRecordService.getInterviewRecordDetail(userId, nonExistentInterviewId))
                .isInstanceOf(InterviewRecordException.class);
        
        verify(interviewRepository).findByIdAndUserIdWithRecords(nonExistentInterviewId, userId);
    }
    
    @Test
    @DisplayName("예외케이스: 권한 없는 사용자의 면접 상세 조회")
    void getInterviewRecordDetail_AccessDenied() {
        // Given
        Long unauthorizedUserId = 999L;
        Long interviewId = 1L;
        
        given(interviewRepository.findByIdAndUserIdWithRecords(interviewId, unauthorizedUserId))
                .willReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> interviewRecordService.getInterviewRecordDetail(unauthorizedUserId, interviewId))
                .isInstanceOf(InterviewRecordException.class);
        
        verify(interviewRepository).findByIdAndUserIdWithRecords(interviewId, unauthorizedUserId);
    }
    
    @Test
    @DisplayName("예외케이스: 면접 기록 목록 조회 중 DB 에러 발생")
    void getInterviewRecords_DatabaseError() {
        // Given
        Long userId = 1L;
        int page = 0, size = 10;
        String role = null;
        
        given(interviewRepository.findByUserIdAndCompleted(eq(userId), any(Pageable.class)))
                .willThrow(new RuntimeException("Database connection error"));
        
        // When & Then
        assertThatThrownBy(() -> interviewRecordService.getInterviewRecords(userId, page, size, role))
                .isInstanceOf(InterviewRecordException.class);
        
        verify(interviewRepository).findByUserIdAndCompleted(eq(userId), any(Pageable.class));
    }
    
    @Test
    @DisplayName("예외케이스: 면접 상세 조회 중 데이터 처리 에러 발생") 
    void getInterviewRecordDetail_DataProcessingError() {
        // Given
        Long userId = 1L;
        Long interviewId = 1L;
        
        // InterviewRecord를 null로 설정하여 NullPointerException 유발
        Interview interviewWithNullRecords = createInterview(1L, interviewer, interviewee, questionSet, "ROOM123");
        interviewWithNullRecords.getInterviewRecords().add(null); // null 레코드 추가
        
        given(interviewRepository.findByIdAndUserIdWithRecords(interviewId, userId))
                .willReturn(Optional.of(interviewWithNullRecords));
        
        // When & Then
        assertThatThrownBy(() -> interviewRecordService.getInterviewRecordDetail(userId, interviewId))
                .isInstanceOf(InterviewRecordException.class);
        
        verify(interviewRepository).findByIdAndUserIdWithRecords(interviewId, userId);
    }

    // ============== 헬퍼 메서드들 ==============
    
    private User createUser(Long userId, String email, String nickname) {
        User user = User.builder()
                .userId(userId)
                .sub(email)
                .nickname(nickname)
                .build();
        return user;
    }
    
    private QuestionSet createQuestionSet(Long id, String title) {
        QuestionSet questionSet = QuestionSet.builder()
                .ownerUserId(interviewer)
                .title(title)
                .description("테스트 설명")
                .versionNumber(1)
                .isPublic(true)
                .build();
        setFieldValue(questionSet, "id", id);
        return questionSet;
    }
    
    private Interview createInterview(Long id, User interviewer, User interviewee, QuestionSet questionSet, String roomId) {
        Interview interview = Interview.builder()
                .interviewer(interviewer)
                .interviewee(interviewee)
                .usedQuestionSet(questionSet)
                .roomId(roomId)
                .build();
        setFieldValue(interview, "interviewId", id);
        setFieldValue(interview, "status", RoomStatus.COMPLETED);
        setFieldValue(interview, "completedAt", LocalDateTime.now());
        return interview;
    }
    
    private Answer createAnswer(Long id, String content, User createdBy) {
        Answer answer = Answer.builder()
                .content(content)
                .createdBy(createdBy)
                .build();
        setFieldValue(answer, "id", id);
        setFieldValue(answer, "createdAt", LocalDateTime.now());
        return answer;
    }
    
    private InterviewRecord createInterviewRecord(Long id, Interview interview, Question question, Answer answer, int orderNum) {
        InterviewRecord record = InterviewRecord.builder()
                .interview(interview)
                .question(question)
                .answer(answer)
                .orderNum(orderNum)
                .build();
        setFieldValue(record, "recordId", id);
        return record;
    }
    
    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 상위 클래스에서 필드를 찾아보기
            try {
                Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException("Failed to set field " + fieldName + " on " + target.getClass().getSimpleName(), ex);
            }
        }
    }
}