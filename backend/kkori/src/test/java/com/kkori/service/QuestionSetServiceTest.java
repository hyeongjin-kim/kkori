package com.kkori.service;

import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.dto.question.response.QuestionSetResponse;
import com.kkori.entity.Question;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.User;
import com.kkori.entity.QuestionType;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.exception.user.UserException;
import com.kkori.repository.AnswerRepository;
import com.kkori.repository.QuestionRepository;
import com.kkori.repository.QuestionSetQuestionMapRepository;
import com.kkori.repository.QuestionSetRepository;
import com.kkori.repository.UserRepository;
import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.kkori.entity.QuestionType.DEFAULT;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionSetServiceTest {

    @Mock
    private QuestionSetRepository questionSetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionSetQuestionMapRepository questionSetQuestionMapRepository;

    @InjectMocks
    private QuestionSetServiceImpl questionSetService;

    @Test
    @DisplayName("해피케이스: 초기 질문들과 함께 질문세트 생성 성공")
    void createQuestionSetWithInitialQuestions_Success() {
        // Given
        Long userId = 1L;
        String title = "Java 기초 질문세트";
        String description = "Java 개발자를 위한 기초 질문 모음";

        User owner = createUser(userId, "owner@test.com");
        CreateNewQuestionSetRequest request = createQuestionSetRequest(title, description);
        QuestionSet savedQuestionSet = createQuestionSet(1L, owner, title, description);
        Question savedQuestion = createQuestion(1L, "자바의 특징을 설명해주세요.");

        given(userRepository.findById(userId)).willReturn(Optional.of(owner));
        given(questionSetRepository.save(any(QuestionSet.class))).willReturn(savedQuestionSet);
        given(questionRepository.save(any(Question.class))).willReturn(savedQuestion);
        given(questionSetRepository.findById(savedQuestionSet.getId()))
                .willReturn(Optional.of(savedQuestionSet));

        // When
        Long result = questionSetService.createQuestionSetWithInitialQuestions(userId, request, title);

        // Then
        assertThat(result).isEqualTo(1L);

        verify(userRepository, times(2)).findById(userId);
        verify(questionSetRepository).save(any(QuestionSet.class));
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("예외케이스: 제목 없이 질문세트 생성 시도")
    void createQuestionSetWithInitialQuestions_NoTitle() {
        // Given
        Long userId = 1L;
        String emptyTitle = "";
        CreateNewQuestionSetRequest request = createQuestionSetRequest("테스트", "설명");

        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.createQuestionSetWithInitialQuestions(userId, request, emptyTitle))
                .isInstanceOf(QuestionSetException.class);
        
        verifyNoInteractions(userRepository, questionSetRepository, questionRepository);
    }

    @Test
    @DisplayName("예외케이스: 존재하지 않는 사용자로 질문세트 생성 시도")
    void createQuestionSetWithInitialQuestions_UserNotFound() {
        // Given
        Long nonExistentUserId = 999L;
        String title = "테스트 질문세트";
        CreateNewQuestionSetRequest request = createQuestionSetRequest(title, "설명");
        
        given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.createQuestionSetWithInitialQuestions(nonExistentUserId, request, title))
                .isInstanceOf(UserException.class);
        
        verify(userRepository).findById(nonExistentUserId);
        verifyNoInteractions(questionSetRepository, questionRepository);
    }

    @Test
    @DisplayName("해피케이스: 질문세트 상세 조회 성공 (소유자)")
    void getQuestionSetDetail_Success_Owner() {
        // Given
        Long userId = 1L;
        Long questionSetId = 100L;
        
        User owner = createUser(userId, "owner@test.com");
        QuestionSet questionSet = createQuestionSet(questionSetId, owner, "테스트 질문세트", "설명");
        
        given(questionSetRepository.findByIdWithQuestionsAndTags(questionSetId))
                .willReturn(Optional.of(questionSet));

        // When
        QuestionSetResponse response = questionSetService.getQuestionSetDetail(userId, questionSetId);

        // Then
        assertThat(response.getId()).isEqualTo(questionSetId);
        assertThat(response.getTitle()).isEqualTo("테스트 질문세트");
        assertThat(response.getOwnerNickname()).isEqualTo("테스트사용자");
        
        verify(questionSetRepository).findByIdWithQuestionsAndTags(questionSetId);
    }

    @Test
    @DisplayName("해피케이스: 질문세트 상세 조회 성공 (공개된 질문세트)")
    void getQuestionSetDetail_Success_SharedQuestionSet() {
        // Given
        Long viewerUserId = 2L;
        Long questionSetId = 100L;
        
        User owner = createUser(1L, "owner@test.com");
        QuestionSet questionSet = createQuestionSet(questionSetId, owner, "공개 질문세트", "공개된 질문세트입니다");
        questionSet.updateSharedStatus(true); // 공개 설정
        
        given(questionSetRepository.findByIdWithQuestionsAndTags(questionSetId))
                .willReturn(Optional.of(questionSet));

        // When
        QuestionSetResponse response = questionSetService.getQuestionSetDetail(viewerUserId, questionSetId);

        // Then
        assertThat(response.getId()).isEqualTo(questionSetId);
        assertThat(response.getIsPublic()).isTrue();
        
        verify(questionSetRepository).findByIdWithQuestionsAndTags(questionSetId);
    }

    @Test
    @DisplayName("예외케이스: 존재하지 않는 질문세트 조회")
    void getQuestionSetDetail_QuestionSetNotFound() {
        // Given
        Long userId = 1L;
        Long nonExistentQuestionSetId = 999L;
        
        given(questionSetRepository.findByIdWithQuestionsAndTags(nonExistentQuestionSetId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.getQuestionSetDetail(userId, nonExistentQuestionSetId))
                .isInstanceOf(QuestionSetException.class);
        
        verify(questionSetRepository).findByIdWithQuestionsAndTags(nonExistentQuestionSetId);
    }

    @Test
    @DisplayName("예외케이스: 접근 권한 없는 질문세트 조회")
    void getQuestionSetDetail_AccessDenied() {
        // Given
        Long unauthorizedUserId = 999L;
        Long questionSetId = 100L;
        
        User owner = createUser(1L, "owner@test.com");
        QuestionSet privateQuestionSet = createQuestionSet(questionSetId, owner, "비공개 질문세트", "설명");
        privateQuestionSet.updateSharedStatus(false); // 비공개 설정
        
        given(questionSetRepository.findByIdWithQuestionsAndTags(questionSetId))
                .willReturn(Optional.of(privateQuestionSet));

        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.getQuestionSetDetail(unauthorizedUserId, questionSetId))
                .isInstanceOf(QuestionSetException.class);
        
        verify(questionSetRepository).findByIdWithQuestionsAndTags(questionSetId);
    }

    @Test
    @DisplayName("해피케이스: 사용자 질문세트 목록 조회 성공")
    void getUserQuestionSets_Success() {
        // Given
        Long userId = 1L;
        int page = 0;
        int size = 10;
        
        User owner = createUser(userId, "owner@test.com");
        QuestionSet questionSet1 = createQuestionSet(1L, owner, "질문세트 1", "설명 1");
        QuestionSet questionSet2 = createQuestionSet(2L, owner, "질문세트 2", "설명 2");
        
        Pageable pageable = PageRequest.of(page, size);
        
        given(userRepository.findById(userId)).willReturn(Optional.of(owner));
        given(questionSetRepository.findByOwnerUserIdOrderByVersionDesc(userId, pageable))
                .willReturn(Arrays.asList(questionSet1, questionSet2));

        // When
        List<QuestionSetResponse> responses = questionSetService.getUserQuestionSets(userId, page, size);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("질문세트 1");
        assertThat(responses.get(1).getTitle()).isEqualTo("질문세트 2");
        
        verify(userRepository).findById(userId);
        verify(questionSetRepository).findByOwnerUserIdOrderByVersionDesc(userId, pageable);
    }

    @Test
    @DisplayName("해피케이스: 공유 질문세트 목록 조회 성공")
    void getSharedQuestionSets_Success() {
        // Given
        Long userId = 1L;
        int page = 0;
        int size = 10;
        
        User currentUser = createUser(userId, "user@test.com");
        User otherUser = createUser(2L, "other@test.com");
        QuestionSet sharedQuestionSet = createQuestionSet(1L, otherUser, "공유 질문세트", "공유된 질문세트입니다");
        sharedQuestionSet.updateSharedStatus(true);
        
        Pageable pageable = PageRequest.of(page, size);
        
        given(userRepository.findById(userId)).willReturn(Optional.of(currentUser));
        given(questionSetRepository.findSharedQuestionSets(userId, pageable))
                .willReturn(Arrays.asList(sharedQuestionSet));

        // When
        List<QuestionSetResponse> responses = questionSetService.getSharedQuestionSets(userId, page, size);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("공유 질문세트");
        assertThat(responses.get(0).getIsPublic()).isTrue();
        
        verify(userRepository).findById(userId);
        verify(questionSetRepository).findSharedQuestionSets(userId, pageable);
    }

    // 테스트 헬퍼 메서드들
    private User createUser(Long userId, String email) {
        return User.builder()
                .userId(userId)
                .sub(email)
                .nickname("테스트사용자")
                .build();
    }

    private QuestionSet createQuestionSet(Long id, User owner, String title, String description) {
        QuestionSet questionSet = QuestionSet.builder()
                .ownerUserId(owner)
                .title(title)
                .description(description)
                .versionNumber(1)
                .isPublic(false)
                .build();
        try {
            Field idField = QuestionSet.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(questionSet, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return questionSet;
    }

    private Question createQuestion(Long id, String content) {
        return Question.defaultBuilder()
                .content(content)
                .expectedAnswer("테스트 예상 답변")
                .build();
    }

    private CreateNewQuestionSetRequest createQuestionSetRequest(String title, String description) {
        CreateQuestionRequest questionRequest = CreateQuestionRequest.builder()
                .content("자바의 특징을 설명해주세요.")
                .questionType(DEFAULT.getCode())
                .expectedAnswer("플랫폼 독립적, 객체지향적")
                .build();
        
        return CreateNewQuestionSetRequest.builder()
                .title(title)
                .description(description)
                .questions(Arrays.asList(questionRequest))
                .build();
    }
}