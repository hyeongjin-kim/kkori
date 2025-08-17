package com.kkori.service;

import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.dto.question.request.EditQuestionSetVersionRequest;
import com.kkori.dto.question.request.QuestionAnswerModificationRequest;
import com.kkori.dto.question.request.CreateQuestionWithAnswerRequest;
import com.kkori.dto.question.response.QuestionSetResponse;
import com.kkori.dto.question.response.CreateQuestionSetResponse;
import com.kkori.entity.Question;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.QuestionSetQuestionMap;
import com.kkori.entity.User;
import com.kkori.entity.QuestionType;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.exception.user.UserException;
import com.kkori.repository.AnswerRepository;
import com.kkori.repository.QuestionRepository;
import com.kkori.repository.QuestionSetQuestionMapRepository;
import com.kkori.repository.QuestionSetRepository;
import com.kkori.repository.QuestionSetTagRepository;
import com.kkori.repository.TagRepository;
import com.kkori.repository.UserRepository;
import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private QuestionSetQuestionMapRepository questionSetQuestionMapRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private QuestionSetTagRepository questionSetTagRepository;

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
        questionSet.updatePublicStatus(true); // 공개 설정
        
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
        privateQuestionSet.updatePublicStatus(false); // 비공개 설정
        
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
        Page<QuestionSet> questionSetPage = new PageImpl<>(Arrays.asList(questionSet1, questionSet2), pageable, 2);
        given(questionSetRepository.findMyQuestionSets(userId, pageable))
                .willReturn(questionSetPage);

        // When
        List<QuestionSetResponse> responses = questionSetService.getUserQuestionSets(userId, page, size);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("질문세트 1");
        assertThat(responses.get(1).getTitle()).isEqualTo("질문세트 2");
        
        verify(userRepository).findById(userId);
        verify(questionSetRepository).findMyQuestionSets(userId, pageable);
    }

    @Test
    @DisplayName("해피케이스: 공개 질문세트 목록 조회 성공")
    void getPublicQuestionSets_Success() {
        // Given
        Long userId = 1L;
        int page = 0;
        int size = 10;
        
        User currentUser = createUser(userId, "user@test.com");
        User otherUser = createUser(2L, "other@test.com");
        QuestionSet sharedQuestionSet = createQuestionSet(1L, otherUser, "공개 질문세트", "공개된 질문세트입니다");
        sharedQuestionSet.updatePublicStatus(true);
        
        Pageable pageable = PageRequest.of(page, size);
        
        given(userRepository.findById(userId)).willReturn(Optional.of(currentUser));
        given(questionSetRepository.findPublicQuestionSets(userId, pageable))
                .willReturn(Arrays.asList(sharedQuestionSet));

        // When
        List<QuestionSetResponse> responses = questionSetService.getPublicQuestionSets(userId, page, size);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("공개 질문세트");
        assertThat(responses.get(0).getIsPublic()).isTrue();
        
        verify(userRepository).findById(userId);
        verify(questionSetRepository).findPublicQuestionSets(userId, pageable);
    }

    @Test
    @DisplayName("경계케이스: 빈 페이지 조회 (공개 질문세트 없음)")
    void getPublicQuestionSets_EmptyResult() {
        // Given
        Long userId = 1L;
        int page = 0;
        int size = 10;
        
        User currentUser = createUser(userId, "user@test.com");
        Pageable pageable = PageRequest.of(page, size);
        
        given(userRepository.findById(userId)).willReturn(Optional.of(currentUser));
        given(questionSetRepository.findPublicQuestionSets(userId, pageable))
                .willReturn(Arrays.asList()); // 빈 리스트

        // When
        List<QuestionSetResponse> responses = questionSetService.getPublicQuestionSets(userId, page, size);

        // Then
        assertThat(responses).isEmpty();
        
        verify(userRepository).findById(userId);
        verify(questionSetRepository).findPublicQuestionSets(userId, pageable);
    }

    @Test
    @DisplayName("경계케이스: 사용자 질문세트 목록이 비어있는 경우")
    void getUserQuestionSets_EmptyResult() {
        // Given
        Long userId = 1L;
        int page = 0;
        int size = 10;
        
        User owner = createUser(userId, "owner@test.com");
        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionSet> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        
        given(userRepository.findById(userId)).willReturn(Optional.of(owner));
        given(questionSetRepository.findMyQuestionSets(userId, pageable))
                .willReturn(emptyPage);

        // When
        List<QuestionSetResponse> responses = questionSetService.getUserQuestionSets(userId, page, size);

        // Then
        assertThat(responses).isEmpty();
        
        verify(userRepository).findById(userId);
        verify(questionSetRepository).findMyQuestionSets(userId, pageable);
    }

    @Test
    @DisplayName("예외케이스: 사용자 목록 조회 시 존재하지 않는 사용자")
    void getUserQuestionSets_UserNotFound() {
        // Given
        Long nonExistentUserId = 999L;
        int page = 0;
        int size = 10;
        
        given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.getUserQuestionSets(nonExistentUserId, page, size))
                .isInstanceOf(UserException.class);
        
        verify(userRepository).findById(nonExistentUserId);
        verifyNoInteractions(questionSetRepository);
    }

    @Test
    @DisplayName("예외케이스: 공개 질문세트 조회 시 존재하지 않는 사용자")
    void getPublicQuestionSets_UserNotFound() {
        // Given
        Long nonExistentUserId = 999L;
        int page = 0;
        int size = 10;
        
        given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.getPublicQuestionSets(nonExistentUserId, page, size))
                .isInstanceOf(UserException.class);
        
        verify(userRepository).findById(nonExistentUserId);
        verifyNoInteractions(questionSetRepository);
    }

    @Test
    @DisplayName("경계케이스: 페이지 경계값 테스트 (음수 페이지) - Spring 자체 검증")
    void getUserQuestionSets_NegativePage() {
        // Given
        Long userId = 1L;
        int negativePage = -1;
        int size = 10;
        
        User owner = createUser(userId, "owner@test.com");
        given(userRepository.findById(userId)).willReturn(Optional.of(owner));
        
        // When & Then - Spring PageRequest가 음수를 허용하지 않음
        assertThatThrownBy(() -> 
                questionSetService.getUserQuestionSets(userId, negativePage, size))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("경계케이스: 페이지 크기 경계값 테스트 (0 크기) - Spring 자체 검증")
    void getUserQuestionSets_ZeroSize() {
        // Given
        Long userId = 1L;
        int page = 0;
        int zeroSize = 0;
        
        User owner = createUser(userId, "owner@test.com");
        given(userRepository.findById(userId)).willReturn(Optional.of(owner));
        
        // When & Then - Spring PageRequest가 0 크기를 허용하지 않음
        assertThatThrownBy(() -> 
                questionSetService.getUserQuestionSets(userId, page, zeroSize))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("해피케이스: 큰 페이지 크기도 정상 처리")
    void getUserQuestionSets_LargePageSize() {
        // Given
        Long userId = 1L;
        int page = 0;
        int largeSize = 1000; // 큰 크기도 정상 처리됨
        
        User owner = createUser(userId, "owner@test.com");
        Pageable pageable = PageRequest.of(page, largeSize);
        Page<QuestionSet> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        
        given(userRepository.findById(userId)).willReturn(Optional.of(owner));
        given(questionSetRepository.findMyQuestionSets(userId, pageable))
                .willReturn(emptyPage);

        // When
        List<QuestionSetResponse> responses = questionSetService.getUserQuestionSets(userId, page, largeSize);

        // Then
        assertThat(responses).isEmpty();
        
        verify(userRepository).findById(userId);
        verify(questionSetRepository).findMyQuestionSets(userId, pageable);
    }

    @Test
    @DisplayName("예외케이스: null 사용자 ID로 질문세트 생성 - userRepository에서 에러")
    void createQuestionSetWithInitialQuestions_NullUserId() {
        // Given
        Long nullUserId = null;
        String title = "테스트 제목";
        CreateNewQuestionSetRequest request = createQuestionSetRequest(title, "설명");
        
        given(userRepository.findById(nullUserId)).willReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.createQuestionSetWithInitialQuestions(nullUserId, request, title))
                .isInstanceOf(UserException.class); // findUserById에서 UserException 발생
    }

    @Test
    @DisplayName("예외케이스: null 요청 객체로 질문세트 생성 - NullPointerException")
    void createQuestionSetWithInitialQuestions_NullRequest() {
        // Given
        Long userId = 1L;
        String title = "테스트 제목";
        CreateNewQuestionSetRequest nullRequest = null;
        
        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.createQuestionSetWithInitialQuestions(userId, nullRequest, title))
                .isInstanceOf(NullPointerException.class); // null.getQuestions() 호출 시 NPE
                
        verifyNoInteractions(userRepository, questionSetRepository, questionRepository);
    }

    @Test
    @DisplayName("예외케이스: 빈 제목으로 질문세트 생성")
    void createQuestionSetWithInitialQuestions_EmptyTitle() {
        // Given
        Long userId = 1L;
        String emptyTitle = ""; // 빈 제목
        CreateNewQuestionSetRequest request = createQuestionSetRequest("정상 제목", "설명");
        
        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.createQuestionSetWithInitialQuestions(userId, request, emptyTitle))
                .isInstanceOf(QuestionSetException.class); // validateCreateQuestionSetRequest에서 검증
                
        verifyNoInteractions(userRepository, questionSetRepository, questionRepository);
    }

    @Test
    @DisplayName("예외케이스: null 제목으로 질문세트 생성")
    void createQuestionSetWithInitialQuestions_NullTitle() {
        // Given
        Long userId = 1L;
        String nullTitle = null; // null 제목
        CreateNewQuestionSetRequest request = createQuestionSetRequest("정상 제목", "설명");
        
        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.createQuestionSetWithInitialQuestions(userId, request, nullTitle))
                .isInstanceOf(QuestionSetException.class); // validateCreateQuestionSetRequest에서 검증
                
        verifyNoInteractions(userRepository, questionSetRepository, questionRepository);
    }

    @Test
    @DisplayName("예외케이스: 빈 질문 리스트로 질문세트 생성")
    void createQuestionSetWithInitialQuestions_EmptyQuestions() {
        // Given
        Long userId = 1L;
        String title = "테스트 질문세트";
        
        CreateNewQuestionSetRequest requestWithEmptyQuestions = CreateNewQuestionSetRequest.builder()
                .title(title)
                .description("설명")
                .questions(Arrays.asList()) // 빈 질문 리스트
                .build();
        
        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.createQuestionSetWithInitialQuestions(userId, requestWithEmptyQuestions, title))
                .isInstanceOf(QuestionSetException.class); // validateQuestionsNotEmpty에서 검증
                
        verifyNoInteractions(userRepository, questionSetRepository, questionRepository);
    }

    @Test
    @DisplayName("예외케이스: null 질문 리스트로 질문세트 생성")
    void createQuestionSetWithInitialQuestions_NullQuestions() {
        // Given
        Long userId = 1L;
        String title = "테스트 질문세트";
        
        CreateNewQuestionSetRequest requestWithNullQuestions = CreateNewQuestionSetRequest.builder()
                .title(title)
                .description("설명")
                .questions(null) // null 질문 리스트
                .build();
        
        // When & Then
        assertThatThrownBy(() -> 
                questionSetService.createQuestionSetWithInitialQuestions(userId, requestWithNullQuestions, title))
                .isInstanceOf(QuestionSetException.class); // validateQuestionsNotEmpty에서 검증
                
        verifyNoInteractions(userRepository, questionSetRepository, questionRepository);
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
        return Question.createDefault(content, "예상 답변");
    }

    private CreateNewQuestionSetRequest createQuestionSetRequest(String title, String description) {
        CreateQuestionRequest questionRequest = CreateQuestionRequest.builder()
                .content("자바의 특징을 설명해주세요.")
                .expectedAnswer("플랫폼 독립적, 객체지향적")
                .build();
        
        return CreateNewQuestionSetRequest.builder()
                .title(title)
                .description(description)
                .questions(Arrays.asList(questionRequest))
                .build();
    }

    @Test
    @DisplayName("질문 세트 편집으로 새 버전 생성 - 해피 케이스")
    void editQuestionSetVersion_Success() {
        // Given
        Long userId = 1L;
        Long parentQuestionSetId = 100L;
        
        User user = createUser(userId, "test@example.com");
        QuestionSet parentQuestionSet = createQuestionSet(parentQuestionSetId, user, "부모 질문세트", "부모 설명");
        
        Question existingQuestion = Question.createDefault("기존 질문", "기존 답변");
        
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(questionSetRepository.findByIdAndNotDeleted(parentQuestionSetId))
                .willReturn(Optional.of(parentQuestionSet));
        given(questionRepository.findById(101L)).willReturn(Optional.of(existingQuestion));
        given(questionSetRepository.findMaxVersionNumberByParentVersionId(any())).willReturn(Optional.of(1));
        
        // 새 질문 세트는 실제 객체 반환하되 ID만 설정
        given(questionSetRepository.save(any(QuestionSet.class))).willAnswer(invocation -> {
            QuestionSet qs = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = QuestionSet.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(qs, 200L);
            } catch (Exception e) {
                // ignore
            }
            return qs;
        });
        
        given(questionRepository.save(any(Question.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(questionSetQuestionMapRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        given(tagRepository.findByTag(anyString())).willReturn(Optional.empty());
        given(tagRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        given(questionSetTagRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        
        EditQuestionSetVersionRequest request = EditQuestionSetVersionRequest.builder()
                .parentQuestionSetId(parentQuestionSetId)
                .existingQuestions(Arrays.asList(
                    QuestionAnswerModificationRequest.builder()
                        .questionId(101L)
                        .newExpectedAnswer("새 답변")
                        .displayOrder(1)
                        .build()
                ))
                .newQuestions(Arrays.asList(
                    CreateQuestionWithAnswerRequest.builder()
                        .content("새 질문")
                        .expectedAnswer("새 질문 답변")
                        .build()
                ))
                .tags(Arrays.asList("Java"))
                .build();
        
        // When
        CreateQuestionSetResponse result = questionSetService.editQuestionSetVersion(userId, request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(parentQuestionSet.getTitle());
        assertThat(result.getParentVersionId()).isEqualTo(parentQuestionSetId);
        assertThat(result.getVersionNumber()).isEqualTo(2);
        
        verify(questionRepository, times(2)).save(any(Question.class)); // 기존 질문 수정 + 새 질문 생성
        verify(questionSetQuestionMapRepository, times(2)).save(any());
    }
    
    @Test
    @DisplayName("질문 세트 편집으로 새 버전 생성 - 사용자 없음 예외")
    void editQuestionSetVersion_UserNotFound_ThrowsException() {
        // Given
        Long userId = 999L;
        Long parentQuestionSetId = 100L;
        
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        
        EditQuestionSetVersionRequest request = EditQuestionSetVersionRequest.builder()
                .parentQuestionSetId(parentQuestionSetId)
                .existingQuestions(Arrays.asList())
                .newQuestions(Arrays.asList())
                .build();
        
        // When & Then
        assertThatThrownBy(() -> questionSetService.editQuestionSetVersion(userId, request))
                .isInstanceOf(UserException.class);
        
        verify(questionSetRepository, never()).findByIdAndNotDeleted(any());
        verify(questionRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("질문 세트 편집으로 새 버전 생성 - 부모 질문세트 없음 예외")
    void editQuestionSetVersion_ParentQuestionSetNotFound_ThrowsException() {
        // Given
        Long userId = 1L;
        Long parentQuestionSetId = 999L;
        
        User user = createUser(userId, "test@example.com");
        
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(questionSetRepository.findByIdAndNotDeleted(parentQuestionSetId))
                .willReturn(Optional.empty());
        
        EditQuestionSetVersionRequest request = EditQuestionSetVersionRequest.builder()
                .parentQuestionSetId(parentQuestionSetId)
                .existingQuestions(Arrays.asList())
                .newQuestions(Arrays.asList())
                .build();
        
        // When & Then
        assertThatThrownBy(() -> questionSetService.editQuestionSetVersion(userId, request))
                .isInstanceOf(QuestionSetException.class);
        
        verify(questionRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("질문 세트 편집으로 새 버전 생성 - 기존 질문 없음 예외")
    void editQuestionSetVersion_ExistingQuestionNotFound_ThrowsException() {
        // Given
        Long userId = 1L;
        Long parentQuestionSetId = 100L;
        
        User user = createUser(userId, "test@example.com");
        QuestionSet parentQuestionSet = createQuestionSet(parentQuestionSetId, user, "부모 질문세트", "부모 설명");
        
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(questionSetRepository.findByIdAndNotDeleted(parentQuestionSetId))
                .willReturn(Optional.of(parentQuestionSet));
        given(questionRepository.findById(999L)).willReturn(Optional.empty()); // 존재하지 않는 질문
        given(questionSetRepository.findMaxVersionNumberByParentVersionId(any())).willReturn(Optional.of(1));
        given(questionSetRepository.save(any(QuestionSet.class))).willAnswer(invocation -> invocation.getArgument(0));
        
        EditQuestionSetVersionRequest request = EditQuestionSetVersionRequest.builder()
                .parentQuestionSetId(parentQuestionSetId)
                .existingQuestions(Arrays.asList(
                    QuestionAnswerModificationRequest.builder()
                        .questionId(999L) // 존재하지 않는 질문 ID
                        .newExpectedAnswer("새 답변")
                        .displayOrder(1)
                        .build()
                ))
                .newQuestions(Arrays.asList())
                .build();
        
        // When & Then
        assertThatThrownBy(() -> questionSetService.editQuestionSetVersion(userId, request))
                .isInstanceOf(QuestionSetException.class);
        
        verify(questionSetQuestionMapRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("질문 세트 편집으로 새 버전 생성 - 빈 요청 처리")
    void editQuestionSetVersion_EmptyRequest_Success() {
        // Given
        Long userId = 1L;
        Long parentQuestionSetId = 100L;
        
        User user = createUser(userId, "test@example.com");
        QuestionSet parentQuestionSet = createQuestionSet(parentQuestionSetId, user, "부모 질문세트", "부모 설명");
        
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(questionSetRepository.findByIdAndNotDeleted(parentQuestionSetId))
                .willReturn(Optional.of(parentQuestionSet));
        given(questionSetRepository.findMaxVersionNumberByParentVersionId(any())).willReturn(Optional.of(1));
        
        given(questionSetRepository.save(any(QuestionSet.class))).willAnswer(invocation -> {
            QuestionSet qs = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = QuestionSet.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(qs, 200L);
            } catch (Exception e) {
                // ignore
            }
            return qs;
        });
        
        EditQuestionSetVersionRequest request = EditQuestionSetVersionRequest.builder()
                .parentQuestionSetId(parentQuestionSetId)
                .existingQuestions(null) // 기존 질문 수정 없음
                .newQuestions(null)      // 새 질문 추가 없음
                .tags(null)
                .build();
        
        // When
        CreateQuestionSetResponse result = questionSetService.editQuestionSetVersion(userId, request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(parentQuestionSet.getTitle());
        assertThat(result.getParentVersionId()).isEqualTo(parentQuestionSetId);
        assertThat(result.getVersionNumber()).isEqualTo(2);
        
        // 질문 처리가 없으므로 질문/매핑 저장은 호출되지 않음
        verify(questionRepository, never()).save(any(Question.class));
        verify(questionSetQuestionMapRepository, never()).save(any());
    }

    @Test
    @DisplayName("질문 세트 편집으로 새 버전 생성 - 권한 없음 예외")
    void editQuestionSetVersion_NoPermission_ThrowsException() {
        // Given
        Long userId = 1L;
        Long otherUserId = 2L;
        Long parentQuestionSetId = 100L;
        
        User user = createUser(userId, "test@example.com");
        User otherUser = createUser(otherUserId, "other@example.com");
        QuestionSet parentQuestionSet = createQuestionSet(parentQuestionSetId, otherUser, "다른 사용자 질문세트", "설명");
        
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(questionSetRepository.findByIdAndNotDeleted(parentQuestionSetId))
                .willReturn(Optional.of(parentQuestionSet));
        
        EditQuestionSetVersionRequest request = EditQuestionSetVersionRequest.builder()
                .parentQuestionSetId(parentQuestionSetId)
                .existingQuestions(Arrays.asList())
                .newQuestions(Arrays.asList())
                .build();
        
        // When & Then
        assertThatThrownBy(() -> questionSetService.editQuestionSetVersion(userId, request))
                .isInstanceOf(QuestionSetException.class);
        
        verify(questionRepository, never()).save(any());
        verify(questionSetRepository, never()).save(any());
    }
}