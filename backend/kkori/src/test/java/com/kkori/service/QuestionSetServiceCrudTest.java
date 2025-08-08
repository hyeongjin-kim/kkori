package com.kkori.service;

import com.kkori.dto.question.request.*;
import com.kkori.dto.question.response.*;
import com.kkori.entity.*;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.exception.user.UserException;
import com.kkori.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * QuestionSetService의 CRUD 기능에 대한 종합 테스트
 * 테스트 범위:
 * - 질문 세트 생성 (CREATE)
 * - 질문 세트 복사 (COPY) 
 * - 새 버전 생성 (VERSION)
 * - 메타데이터 수정 (UPDATE)
 * - 답변 수정 (MODIFY_ANSWER)
 * - 소프트 삭제 (SOFT_DELETE)
 * - 질문 제거 (REMOVE_QUESTION)
 * - 목록 조회 및 필터링 (READ)
 */
@ExtendWith(MockitoExtension.class)
class QuestionSetServiceCrudTest {

    @Mock private QuestionSetRepository questionSetRepository;
    @Mock private UserRepository userRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private QuestionSetQuestionMapRepository questionSetQuestionMapRepository;
    @Mock private TagRepository tagRepository;
    @Mock private QuestionSetTagRepository questionSetTagRepository;

    @InjectMocks
    private QuestionSetServiceImpl questionSetService;

    private User testUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        testUser = createUser(1L, "test@example.com", "테스트사용자");
        otherUser = createUser(2L, "other@example.com", "다른사용자");
    }

    @Nested
    @DisplayName("CREATE 기능 테스트")
    class CreateOperationsTest {

        @Test
        @DisplayName("해피케이스: 질문과 답변을 포함한 질문 세트 생성 성공")
        void createQuestionSetWithQuestions_Success() {
            // Given
            CreateQuestionSetWithQuestionsRequest request = createQuestionSetRequest();
            QuestionSet savedQuestionSet = createQuestionSet(1L, testUser, "Spring Boot 질문세트", "백엔드 면접 질문");
            Question savedQuestion = createQuestion(1L, "Spring Boot란?");
            Answer savedAnswer = createAnswer(1L, "Spring Boot는...", testUser);

            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(questionSetRepository.save(any(QuestionSet.class))).willReturn(savedQuestionSet);
            given(questionRepository.save(any(Question.class))).willReturn(savedQuestion);
            given(answerRepository.save(any(Answer.class))).willReturn(savedAnswer);
            given(questionSetQuestionMapRepository.save(any(QuestionSetQuestionMap.class)))
                    .willReturn(createQuestionMap(1L, savedQuestionSet, savedQuestion, savedAnswer, 1));

            // When
            CreateQuestionSetResponse response = questionSetService.createQuestionSetWithQuestions(1L, request);

            // Then
            assertThat(response.getQuestionSetId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("Spring Boot 질문세트");
            assertThat(response.getVersionNumber()).isEqualTo(1);
            assertThat(response.getParentVersionId()).isNull();
            assertThat(response.getIsPublic()).isFalse();
            assertThat(response.getQuestionMaps()).hasSize(1);

            verify(questionSetRepository).save(any(QuestionSet.class));
            verify(questionRepository).save(any(Question.class));
            verify(answerRepository).save(any(Answer.class));
            verify(questionSetQuestionMapRepository).save(any(QuestionSetQuestionMap.class));
        }

        @Test
        @DisplayName("예외케이스: 존재하지 않는 사용자")
        void createQuestionSetWithQuestions_UserNotFound() {
            // Given
            CreateQuestionSetWithQuestionsRequest request = createQuestionSetRequest();
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> questionSetService.createQuestionSetWithQuestions(999L, request))
                    .isInstanceOf(UserException.class);

            verify(userRepository).findById(999L);
            verifyNoInteractions(questionSetRepository, questionRepository);
        }

        @Test
        @DisplayName("해피케이스: 질문 세트 복사 성공")
        void copyQuestionSet_Success() {
            // Given
            CopyQuestionSetRequest request = CopyQuestionSetRequest.builder()
                    .originalQuestionSetId(1L)
                    .title("복사된 질문세트")
                    .description("원본에서 복사함")
                    .copyTags(true)
                    .build();

            QuestionSet originalQuestionSet = createQuestionSet(1L, testUser, "원본 질문세트", "원본 설명");
            originalQuestionSet.updatePublicStatus(true); // 공개 설정
            QuestionSet copiedQuestionSet = createQuestionSet(2L, testUser, "복사된 질문세트", "원본에서 복사함");
            
            Question question = createQuestion(1L, "Spring Boot란?");
            Answer answer = createAnswer(1L, "Spring Boot는...", testUser);
            QuestionSetQuestionMap originalMap = createQuestionMap(1L, originalQuestionSet, question, answer, 1);

            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(questionSetRepository.findByIdAndNotDeleted(1L)).willReturn(Optional.of(originalQuestionSet));
            given(questionSetRepository.save(any(QuestionSet.class))).willReturn(copiedQuestionSet);
            given(questionSetQuestionMapRepository.findByQuestionSetIdWithDetails(1L))
                    .willReturn(Arrays.asList(originalMap));
            given(questionSetQuestionMapRepository.save(any(QuestionSetQuestionMap.class)))
                    .willReturn(createQuestionMap(2L, copiedQuestionSet, question, answer, 1));

            // When
            CreateQuestionSetResponse response = questionSetService.copyQuestionSet(1L, request);

            // Then
            assertThat(response.getQuestionSetId()).isEqualTo(2L);
            assertThat(response.getTitle()).isEqualTo("복사된 질문세트");
            assertThat(response.getParentVersionId()).isEqualTo(1L); // 원본을 부모로 참조
            assertThat(response.getQuestionMaps()).hasSize(1);

            verify(questionSetRepository).findByIdAndNotDeleted(1L);
            verify(questionSetRepository).save(any(QuestionSet.class));
            verify(questionSetQuestionMapRepository).findByQuestionSetIdWithDetails(1L);
        }

        @Test
        @DisplayName("예외케이스: 접근 권한 없는 질문세트 복사 시도")
        void copyQuestionSet_AccessDenied() {
            // Given
            CopyQuestionSetRequest request = CopyQuestionSetRequest.builder()
                    .originalQuestionSetId(1L)
                    .title("복사 시도")
                    .description("권한 없음")
                    .build();

            QuestionSet privateQuestionSet = createQuestionSet(1L, otherUser, "비공개 질문세트", "설명");
            privateQuestionSet.updatePublicStatus(false); // 비공개 설정

            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(questionSetRepository.findByIdAndNotDeleted(1L)).willReturn(Optional.of(privateQuestionSet));

            // When & Then
            assertThatThrownBy(() -> questionSetService.copyQuestionSet(1L, request))
                    .isInstanceOf(QuestionSetException.class);

            verify(questionSetRepository).findByIdAndNotDeleted(1L);
            verify(questionSetRepository, never()).save(any(QuestionSet.class));
        }
    }

    @Nested
    @DisplayName("UPDATE 기능 테스트")
    class UpdateOperationsTest {

        @Test
        @DisplayName("해피케이스: 메타데이터 수정 성공")
        void updateQuestionSetMetadata_Success() {
            // Given
            UpdateQuestionSetMetadataRequest request = UpdateQuestionSetMetadataRequest.builder()
                    .title("수정된 제목")
                    .description("수정된 설명")
                    .isPublic(true)
                    .build();

            QuestionSet questionSet = createQuestionSet(1L, testUser, "원본 제목", "원본 설명");
            QuestionSet updatedQuestionSet = createQuestionSet(1L, testUser, "수정된 제목", "수정된 설명");
            updatedQuestionSet.updatePublicStatus(true);

            // Mock 호출 순서를 명확히 설정
            given(questionSetRepository.findByIdAndNotDeleted(1L))
                    .willReturn(Optional.of(questionSet), Optional.of(updatedQuestionSet));
            
            given(questionSetRepository.save(any(QuestionSet.class))).willReturn(updatedQuestionSet);
            given(questionSetQuestionMapRepository.findByQuestionSetIdWithDetails(1L)).willReturn(new ArrayList<>());
            // getQuestionSetDetailNew에서 태그 조회도 필요
            given(questionSetTagRepository.findByQuestionSetIdWithTag(1L)).willReturn(new ArrayList<>());

            // When
            QuestionSetDetailResponse response = questionSetService.updateQuestionSetMetadata(1L, 1L, request);

            // Then
            assertThat(response.getQuestionSetId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("수정된 제목");
            assertThat(response.getDescription()).isEqualTo("수정된 설명");
            assertThat(response.getIsPublic()).isTrue();

            /**
             * 1. 첫 번째 호출: updateQuestionSetMetadata 메서드 시작
             *   부분에서 질문 세트를 조회하여 권한 검사 및 업데이트
             *   수행
             *   2. 두 번째 호출: 메서드 마지막에
             *   getQuestionSetDetailNew(questionSetId) 호출하여
             *   업데이트된 결과를 반환하기 위해 다시 조회
             */
            verify(questionSetRepository, times(2)).findByIdAndNotDeleted(1L);
            verify(questionSetRepository, atLeastOnce()).save(any(QuestionSet.class));
        }

        @Test
        @DisplayName("예외케이스: 소유자가 아닌 사용자의 메타데이터 수정 시도")
        void updateQuestionSetMetadata_NotOwner() {
            // Given
            UpdateQuestionSetMetadataRequest request = UpdateQuestionSetMetadataRequest.builder()
                    .title("해킹 시도")
                    .build();

            QuestionSet questionSet = createQuestionSet(1L, otherUser, "다른 사람의 질문세트", "설명");
            given(questionSetRepository.findByIdAndNotDeleted(1L)).willReturn(Optional.of(questionSet));

            // When & Then
            assertThatThrownBy(() -> questionSetService.updateQuestionSetMetadata(1L, 1L, request))
                    .isInstanceOf(QuestionSetException.class);

            verify(questionSetRepository).findByIdAndNotDeleted(1L);
            verify(questionSetRepository, never()).save(any(QuestionSet.class));
        }
    }

    @Nested
    @DisplayName("DELETE 기능 테스트") 
    class DeleteOperationsTest {

        @Test
        @DisplayName("해피케이스: 질문 세트 소프트 삭제 성공")
        void softDeleteQuestionSet_Success() {
            // Given
            QuestionSet questionSet = createQuestionSet(1L, testUser, "삭제할 질문세트", "설명");
            given(questionSetRepository.findById(1L)).willReturn(Optional.of(questionSet));
            given(questionSetRepository.save(any(QuestionSet.class))).willReturn(questionSet);

            // When
            QuestionSetDetailResponse response = questionSetService.softDeleteQuestionSet(1L, 1L);

            // Then
            assertThat(response.getQuestionSetId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("삭제할 질문세트");
            // 삭제된 상태이므로 질문 목록은 비어있어야 함
            assertThat(response.getQuestionMaps()).isEmpty();

            verify(questionSetRepository).findById(1L);
            verify(questionSetRepository).save(any(QuestionSet.class));
        }

        @Test
        @DisplayName("예외케이스: 이미 삭제된 질문세트 재삭제 시도")
        void softDeleteQuestionSet_AlreadyDeleted() {
            // Given
            QuestionSet deletedQuestionSet = createQuestionSet(1L, testUser, "이미 삭제된 질문세트", "설명");
            deletedQuestionSet.softDelete(); // 이미 삭제된 상태

            given(questionSetRepository.findById(1L)).willReturn(Optional.of(deletedQuestionSet));

            // When & Then
            assertThatThrownBy(() -> questionSetService.softDeleteQuestionSet(1L, 1L))
                    .isInstanceOf(QuestionSetException.class);

            verify(questionSetRepository).findById(1L);
            verify(questionSetRepository, never()).save(any(QuestionSet.class));
        }

        @Test
        @DisplayName("해피케이스: 질문세트에서 특정 질문 제거 성공")
        void removeQuestionFromSet_Success() {
            // Given
            QuestionSet questionSet = createQuestionSet(1L, testUser, "질문세트", "설명");
            Question question = createQuestion(1L, "제거할 질문");
            Answer answer = createAnswer(1L, "답변", testUser);
            QuestionSetQuestionMap questionMap = createQuestionMap(1L, questionSet, question, answer, 1);

            given(questionSetRepository.findByIdAndNotDeleted(1L)).willReturn(Optional.of(questionSet));
            given(questionSetQuestionMapRepository.findByIdWithDetails(1L)).willReturn(Optional.of(questionMap));
            given(questionSetQuestionMapRepository.findByQuestionSetIdOrderByDisplayOrder(1L))
                    .willReturn(new ArrayList<>()); // 제거 후 빈 목록

            // When
            QuestionMapResponse response = questionSetService.removeQuestionFromSet(1L, 1L, 1L);

            // Then
            assertThat(response.getMapId()).isEqualTo(1L);
            assertThat(response.getQuestionId()).isEqualTo(1L);

            verify(questionSetQuestionMapRepository).findByIdWithDetails(1L);
            verify(questionSetQuestionMapRepository).delete(questionMap);
            verify(questionSetQuestionMapRepository).findByQuestionSetIdOrderByDisplayOrder(1L);
        }
    }

    @Nested
    @DisplayName("READ 기능 테스트")
    class ReadOperationsTest {

        @Test
        @DisplayName("해피케이스: 필터링된 질문 세트 목록 조회 - 내 질문세트만")
        void getQuestionSetList_MyQuestionSets_Success() {
            // Given
            QuestionSet questionSet = createQuestionSet(1L, testUser, "내 질문세트", "설명");
            Page<QuestionSet> questionSetsPage = new PageImpl<>(Arrays.asList(questionSet));

            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(questionSetRepository.findMyQuestionSets(eq(1L), any(Pageable.class)))
                    .willReturn(questionSetsPage);
            given(questionSetQuestionMapRepository.countByQuestionSetId(anyLong())).willReturn(5L);

            // When
            Page<QuestionSetListResponse> response = questionSetService.getQuestionSetList(
                    1L, 0, 10, "createdAt,desc", "me", null, null);

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getQuestionSetId()).isEqualTo(1L);
            assertThat(response.getContent().get(0).getQuestionCount()).isEqualTo(5);
            assertThat(response.getContent().get(0).getOwnerNickname()).isEqualTo("테스트사용자");

            verify(questionSetRepository).findMyQuestionSets(eq(1L), any(Pageable.class));
        }

        @Test
        @DisplayName("해피케이스: 공유된 질문 세트 목록 조회")
        void getQuestionSetList_PublicQuestionSets_Success() {
            // Given
            QuestionSet sharedQuestionSet = createQuestionSet(1L, otherUser, "공개 질문세트", "설명");
            sharedQuestionSet.updatePublicStatus(true);
            Page<QuestionSet> questionSetsPage = new PageImpl<>(Arrays.asList(sharedQuestionSet));

            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(questionSetRepository.findPublicQuestionSetsWithPaging(eq(1L), any(Pageable.class)))
                    .willReturn(questionSetsPage);
            given(questionSetQuestionMapRepository.countByQuestionSetId(anyLong())).willReturn(3L);

            // When
            Page<QuestionSetListResponse> response = questionSetService.getQuestionSetList(
                    1L, 0, 10, "createdAt,desc", null, true, null);

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getIsPublic()).isTrue();
            assertThat(response.getContent().get(0).getOwnerNickname()).isEqualTo("다른사용자");

            verify(questionSetRepository).findPublicQuestionSetsWithPaging(eq(1L), any(Pageable.class));
        }

        @Test
        @DisplayName("해피케이스: 버전 히스토리 조회 성공")
        void getQuestionSetVersionsNew_Success() {
            // Given
            QuestionSet baseVersion = createQuestionSet(1L, testUser, "질문세트 v1", "설명");
            QuestionSet newVersion = createQuestionSet(2L, testUser, "질문세트 v2", "설명");
            newVersion = QuestionSet.builder()
                    .ownerUserId(testUser)
                    .title("질문세트 v2")
                    .description("설명")
                    .versionNumber(2)
                    .parentVersionId(baseVersion)
                    .isPublic(false)
                    .build();

            given(questionSetRepository.findByIdAndNotDeleted(1L)).willReturn(Optional.of(baseVersion));
            given(questionSetRepository.findAllVersionsByQuestionSetId(1L, 1L))
                    .willReturn(Arrays.asList(baseVersion, newVersion));
            given(questionSetQuestionMapRepository.countByQuestionSetId(anyLong())).willReturn(3L);

            // When
            List<QuestionSetListResponse> response = questionSetService.getQuestionSetVersionsNew(1L, 1L);

            // Then
            assertThat(response).hasSize(2);
            assertThat(response.get(0).getVersionNumber()).isEqualTo(1);
            assertThat(response.get(1).getVersionNumber()).isEqualTo(2);
            assertThat(response.get(1).getParentVersionId()).isEqualTo(1L);

            verify(questionSetRepository).findByIdAndNotDeleted(1L);
            verify(questionSetRepository).findAllVersionsByQuestionSetId(1L, 1L);
        }
    }

    // =================== 테스트 헬퍼 메서드들 ===================

    private User createUser(Long userId, String email, String nickname) {
        return User.builder()
                .userId(userId)
                .sub(email)
                .nickname(nickname)
                .build();
    }

    private QuestionSet createQuestionSet(Long id, User owner, String title, String description) {
        QuestionSet questionSet = QuestionSet.builder()
                .ownerUserId(owner)
                .title(title)
                .description(description)
                .versionNumber(1)
                .parentVersionId(null)
                .isPublic(false)
                .build();
        setFieldValue(questionSet, "id", id);
        return questionSet;
    }

    private Question createQuestion(Long id, String content) {
        Question question = Question.createDefault(content, "기본 답변");
        setFieldValue(question, "id", id);
        return question;
    }

    private Answer createAnswer(Long id, String content, User createdBy) {
        Answer answer = Answer.builder()
                .content(content)
                .createdBy(createdBy)
                .build();
        setFieldValue(answer, "id", id);
        return answer;
    }

    private QuestionSetQuestionMap createQuestionMap(Long id, QuestionSet questionSet, Question question, Answer answer, Integer displayOrder) {
        QuestionSetQuestionMap questionMap = QuestionSetQuestionMap.builder()
                .questionSet(questionSet)
                .question(question)
                .answer(answer)
                .displayOrder(displayOrder)
                .build();
        setFieldValue(questionMap, "id", id);
        return questionMap;
    }

    private CreateQuestionSetWithQuestionsRequest createQuestionSetRequest() {
        CreateQuestionWithAnswerRequest questionRequest = CreateQuestionWithAnswerRequest.builder()
                .content("Spring Boot의 자동 설정 원리는?")
                .expectedAnswer("@EnableAutoConfiguration을 통해...")
                .build();

        return CreateQuestionSetWithQuestionsRequest.builder()
                .title("Spring Boot 질문세트")
                .description("백엔드 면접 질문")
                .questions(Arrays.asList(questionRequest))
                .tags(Arrays.asList("",""))
                .build();
    }

    /**
     * 리플렉션을 사용해서 엔티티의 private ID 필드를 설정하는 헬퍼 메서드
     */
    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field " + fieldName + " on " + target.getClass().getSimpleName(), e);
        }
    }

}