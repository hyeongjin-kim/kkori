package com.kkori.service;

import static com.kkori.entity.QuestionType.*;
import static java.util.Arrays.*;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.kkori.entity.QuestionSetQuestionMap;
import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.entity.Question;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.User;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.exception.user.UserException;
import com.kkori.repository.QuestionRepository;
import com.kkori.repository.QuestionSetRepository;
import com.kkori.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class QuestionSetServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long NOT_EXISTING_USER_ID = 999L;
    private static final Long QUESTION_SET_ID = 1L;
    private static final Long NOT_EXISTING_QUESTION_SET_ID = 999L;
    private static final String QUESTION_SET_TITLE = "백엔드 개발자 면접 질문";
    private static final String QUESTION_SET_DESCRIPTION = "백엔드 개발자를 위한 면접 질문 세트입니다.";
    private static final String QUESTION_CONTENT_1 = "스프링 프레임워크의 특징을 설명해주세요.";
    private static final String QUESTION_CONTENT_2 = "데이터베이스 트랜잭션에 대해 설명해주세요.";
    private static final String EXPECTED_ANSWER_1 = "IoC, DI, AOP 등의 특징이 있습니다.";
    private static final String EXPECTED_ANSWER_2 = "ACID 특성을 만족하는 작업 단위입니다.";

    @InjectMocks
    private QuestionSetServiceImpl questionSetService;

    @Mock
    private QuestionSetRepository questionSetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    private User user;
    private QuestionSet questionSet;
    private CreateNewQuestionSetRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(USER_ID)
                .sub("kakao123")
                .nickname("테스터")
                .build();

        questionSet = QuestionSet.builder()
                .ownerUserId(user)
                .title(QUESTION_SET_TITLE)
                .description(QUESTION_SET_DESCRIPTION)
                .versionNumber(1)
                .isShared(false)
                .build();

        ReflectionTestUtils.setField(questionSet, "id", QUESTION_SET_ID);

        List<CreateQuestionRequest> questions = asList(
                CreateQuestionRequest.builder()
                        .content(QUESTION_CONTENT_1)
                        .questionType(1)
                        .expectedAnswer(EXPECTED_ANSWER_1)
                        .build(),
                CreateQuestionRequest.builder()
                        .content(QUESTION_CONTENT_2)
                        .questionType(2)
                        .expectedAnswer(EXPECTED_ANSWER_2)
                        .build()
        );

        request = CreateNewQuestionSetRequest.builder()
                .title(QUESTION_SET_TITLE)
                .description(QUESTION_SET_DESCRIPTION)
                .questions(questions)
                .build();
    }

    @Test
    @DisplayName("정상적인 요청으로 질문 세트와 질문들이 생성된다")
    void createNewQuestionSetWithQuestion_Success() {
        given(userRepository.findById(USER_ID)).willReturn(of(user));
        given(questionSetRepository.save(any(QuestionSet.class))).willReturn(questionSet);
        given(questionSetRepository.findById(anyLong())).willReturn(of(questionSet));
        given(questionRepository.save(any(Question.class)))
                .willReturn(Question.builder()
                        .content(QUESTION_CONTENT_1)
                        .questionType(CUSTOM)
                        .expectedAnswer(EXPECTED_ANSWER_1)
                        .build())
                .willReturn(Question.builder()
                        .content(QUESTION_CONTENT_2)
                        .questionType(DEFAULT)
                        .expectedAnswer(EXPECTED_ANSWER_2)
                        .build());

        Long result = questionSetService.createQuestionSetWithInitialQuestions(USER_ID, request, QUESTION_SET_TITLE);

        assertNotNull(result);
        verify(userRepository, times(3)).findById(USER_ID);
        verify(questionSetRepository, times(1)).save(any(QuestionSet.class));
        verify(questionRepository, times(2)).save(any(Question.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 요청 시 UserException 발생")
    void createNewQuestionSetWithQuestion_UserNotFound_ThrowsUserException() {
        given(userRepository.findById(NOT_EXISTING_USER_ID)).willReturn(empty());

        UserException exception = assertThrows(UserException.class, () ->
                questionSetService.createQuestionSetWithInitialQuestions(NOT_EXISTING_USER_ID, request, QUESTION_SET_TITLE));

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository, times(1)).findById(NOT_EXISTING_USER_ID);
        verify(questionSetRepository, times(0)).save(any(QuestionSet.class));
        verify(questionRepository, times(0)).save(any(Question.class));
    }

    @Test
    @DisplayName("빈 질문 리스트로 요청 시 QuestionSetException 발생")
    void createNewQuestionSetWithQuestion_EmptyQuestions_ThrowsQuestionSetException() {
        CreateNewQuestionSetRequest emptyQuestionsRequest = CreateNewQuestionSetRequest.builder()
                .title(QUESTION_SET_TITLE)
                .description(QUESTION_SET_DESCRIPTION)
                .questions(Collections.emptyList())
                .build();

        QuestionSetException exception = assertThrows(QuestionSetException.class, () ->
                questionSetService.createQuestionSetWithInitialQuestions(USER_ID, emptyQuestionsRequest, QUESTION_SET_TITLE));

        assertEquals("질문이 비어있습니다.", exception.getMessage());
        verify(userRepository, times(0)).findById(USER_ID);
        verify(questionSetRepository, times(0)).save(any(QuestionSet.class));
        verify(questionRepository, times(0)).save(any(Question.class));
    }

    @Test
    @DisplayName("null 질문 리스트로 요청 시 QuestionSetException 발생")
    void createNewQuestionSetWithQuestion_NullQuestions_ThrowsQuestionSetException() {
        CreateNewQuestionSetRequest nullQuestionsRequest = CreateNewQuestionSetRequest.builder()
                .title(QUESTION_SET_TITLE)
                .description(QUESTION_SET_DESCRIPTION)
                .questions(null)
                .build();

        QuestionSetException exception = assertThrows(QuestionSetException.class, () ->
                questionSetService.createQuestionSetWithInitialQuestions(USER_ID, nullQuestionsRequest, QUESTION_SET_TITLE));

        assertEquals("질문이 비어있습니다.", exception.getMessage());
        verify(userRepository, times(0)).findById(USER_ID);
        verify(questionSetRepository, times(0)).save(any(QuestionSet.class));
        verify(questionRepository, times(0)).save(any(Question.class));
    }

    @Test
    @DisplayName("질문 세트에 질문을 추가한다")
    void addQuestionToQuestionSet_Success() {
        CreateQuestionRequest questionRequest = CreateQuestionRequest.builder()
                .content("새로운 질문입니다.")
                .questionType(1)
                .expectedAnswer("새로운 답변입니다.")
                .build();

        Question newQuestion = Question.builder()
                .content("새로운 질문입니다.")
                .questionType(CUSTOM)
                .expectedAnswer("새로운 답변입니다.")
                .build();

        ReflectionTestUtils.setField(newQuestion, "id", 10L);

        lenient().when(userRepository.findById(USER_ID)).thenReturn(of(user));
        lenient().when(questionSetRepository.findById(QUESTION_SET_ID)).thenReturn(of(questionSet));
        lenient().when(questionRepository.save(any(Question.class))).thenReturn(newQuestion);

        Long result = questionSetService.addQuestionToQuestionSet(USER_ID, QUESTION_SET_ID, questionRequest);

        assertNotNull(result);

        verify(questionSetRepository, times(1)).findById(QUESTION_SET_ID);
        verify(questionRepository, times(1)).save(any(Question.class));

        ArgumentCaptor<Question> questionCaptor = forClass(Question.class);
        verify(questionRepository).save(questionCaptor.capture());
        Question savedQuestion = questionCaptor.getValue();

        assertThat(savedQuestion.getContent()).isEqualTo("새로운 질문입니다.");
        assertThat(savedQuestion.getQuestionType()).isEqualTo(CUSTOM);
        assertThat(savedQuestion.getExpectedAnswer()).isEqualTo("새로운 답변입니다.");
    }

    @Test
    @DisplayName("질문 세트에 질문 추가 시 존재하지 않는 사용자 ID로 요청하면 UserException 발생")
    void addQuestionToQuestionSet_UserNotFound_ThrowsUserException() {
        CreateQuestionRequest questionRequest = CreateQuestionRequest.builder()
                .content("새로운 질문입니다.")
                .questionType(1)
                .expectedAnswer("새로운 답변입니다.")
                .build();

        given(userRepository.findById(NOT_EXISTING_USER_ID)).willReturn(empty());

        UserException exception = assertThrows(UserException.class, () ->
                questionSetService.addQuestionToQuestionSet(NOT_EXISTING_USER_ID, QUESTION_SET_ID, questionRequest));

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository, times(1)).findById(NOT_EXISTING_USER_ID);
        verify(questionSetRepository, times(0)).findById(anyLong());
        verify(questionRepository, times(0)).save(any(Question.class));
    }

    @Test
    @DisplayName("질문 세트에 질문 추가 시 존재하지 않는 질문 세트 ID로 요청하면 QuestionSetException 발생")
    void addQuestionToQuestionSet_QuestionSetNotFound_ThrowsQuestionSetException() {
        CreateQuestionRequest questionRequest = CreateQuestionRequest.builder()
                .content("새로운 질문입니다.")
                .questionType(1)
                .expectedAnswer("새로운 답변입니다.")
                .build();

        lenient().when(userRepository.findById(USER_ID)).thenReturn(of(user));
        lenient().when(questionSetRepository.findById(NOT_EXISTING_QUESTION_SET_ID)).thenReturn(empty());

        QuestionSetException exception = assertThrows(QuestionSetException.class, () ->
                questionSetService.addQuestionToQuestionSet(USER_ID, NOT_EXISTING_QUESTION_SET_ID, questionRequest));

        assertEquals("질문 세트를 찾을 수 없습니다.", exception.getMessage());
        verify(questionSetRepository, times(1)).findById(NOT_EXISTING_QUESTION_SET_ID);
        verify(questionRepository, times(0)).save(any(Question.class));
    }

    @Test
    @DisplayName("질문 세트에 질문 추가 시 다른 사용자의 질문 세트에 접근하면 QuestionSetException 발생")
    void addQuestionToQuestionSet_NotOwner_ThrowsQuestionSetException() {
        User otherUser = User.builder()
                .userId(2L)
                .sub("other123")
                .nickname("다른사용자")
                .build();
        QuestionSet otherUserQuestionSet = QuestionSet.builder()
                .ownerUserId(otherUser)
                .title("다른 사용자의 질문 세트")
                .description("다른 사용자가 만든 질문 세트입니다.")
                .versionNumber(1)
                .isShared(false)
                .build();

        CreateQuestionRequest questionRequest = CreateQuestionRequest.builder()
                .content("새로운 질문입니다.")
                .questionType(1)
                .expectedAnswer("새로운 답변입니다.")
                .build();

        given(userRepository.findById(USER_ID)).willReturn(of(user));
        given(questionSetRepository.findById(QUESTION_SET_ID)).willReturn(of(otherUserQuestionSet));

        QuestionSetException exception = assertThrows(QuestionSetException.class, () ->
                questionSetService.addQuestionToQuestionSet(USER_ID, QUESTION_SET_ID, questionRequest));

        assertEquals("질문 세트에 대한 권한이 없습니다.", exception.getMessage());
        verify(questionSetRepository, times(1)).findById(QUESTION_SET_ID);
        verify(questionRepository, times(0)).save(any(Question.class));
    }

    @Test
    @DisplayName("질문 세트만 생성한다")
    void createNewQuestionSet_Success() {
        given(userRepository.findById(USER_ID)).willReturn(of(user));
        given(questionSetRepository.save(any(QuestionSet.class))).willReturn(questionSet);

        Long result = questionSetService.createNewQuestionSet(USER_ID, QUESTION_SET_TITLE, QUESTION_SET_DESCRIPTION);

        assertNotNull(result);
        verify(questionSetRepository, times(1)).save(any(QuestionSet.class));

        ArgumentCaptor<QuestionSet> questionSetCaptor = forClass(QuestionSet.class);
        verify(questionSetRepository).save(questionSetCaptor.capture());
        QuestionSet savedQuestionSet = questionSetCaptor.getValue();

        assertThat(savedQuestionSet.getTitle()).isEqualTo(QUESTION_SET_TITLE);
        assertThat(savedQuestionSet.getDescription()).isEqualTo(QUESTION_SET_DESCRIPTION);
        assertThat(savedQuestionSet.getOwnerUserId()).isEqualTo(user);
        assertThat(savedQuestionSet.getVersionNumber()).isEqualTo(1);
        assertThat(savedQuestionSet.getIsShared()).isFalse();
    }

    @Test
    @DisplayName("질문 세트 생성 시 존재하지 않는 사용자 ID로 요청하면 UserException 발생")
    void createNewQuestionSet_UserNotFound_ThrowsUserException() {
        given(userRepository.findById(NOT_EXISTING_USER_ID)).willReturn(empty());

        UserException exception = assertThrows(UserException.class, () ->
                questionSetService.createNewQuestionSet(NOT_EXISTING_USER_ID, QUESTION_SET_TITLE, QUESTION_SET_DESCRIPTION));

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository, times(1)).findById(NOT_EXISTING_USER_ID);
        verify(questionSetRepository, times(0)).save(any(QuestionSet.class));
    }

    @Test
    @DisplayName("기존 질문 세트에서 새 버전 생성")
    void createNewQuestionSetWithQuestion_FromExisting_Success() {
        Long parentQuestionSetId = 2L;

        QuestionSet parentQuestionSet = QuestionSet.builder()
                .ownerUserId(user)
                .title("원본 질문 세트")
                .description("원본 설명")
                .versionNumber(1)
                .isShared(false)
                .build();

        Question parentQuestion = Question.builder()
                .content("부모 질문")
                .questionType(CUSTOM)
                .expectedAnswer("부모 답변")
                .build();
        ReflectionTestUtils.setField(parentQuestion, "id", 100L);

        QuestionSetQuestionMap questionMap = QuestionSetQuestionMap.of(parentQuestionSet, parentQuestion, 1);
        parentQuestionSet.getQuestionMaps().add(questionMap);

        CreateNewQuestionSetRequest versioningRequest = CreateNewQuestionSetRequest.builder()
                .title("새 버전 질문 세트")
                .description("새 버전 설명")
                .questions(asList(
                        CreateQuestionRequest.builder()
                                .content("추가 질문")
                                .questionType(1)
                                .expectedAnswer("추가 답변")
                                .build()
                ))
                .parentVersionId(parentQuestionSetId)
                .build();

        QuestionSet newVersionQuestionSet = QuestionSet.builder()
                .ownerUserId(user)
                .title("새 버전 질문 세트")
                .description("새 버전 설명")
                .versionNumber(2)
                .parentVersionId(parentQuestionSet)
                .isShared(false)
                .build();

        ReflectionTestUtils.setField(newVersionQuestionSet, "id", 3L);

        given(userRepository.findById(USER_ID)).willReturn(of(user));
        given(questionSetRepository.findById(parentQuestionSetId)).willReturn(of(parentQuestionSet));
        given(questionSetRepository.findMaxVersionNumberByParentVersionId(parentQuestionSetId)).willReturn(of(1));
        given(questionSetRepository.save(any(QuestionSet.class))).willReturn(newVersionQuestionSet);
        given(questionSetRepository.findById(3L)).willReturn(of(newVersionQuestionSet));

        given(questionRepository.save(any(Question.class)))
                .willReturn(Question.builder()
                        .content("복제 질문")
                        .questionType(CUSTOM)
                        .expectedAnswer("복제 답변")
                        .build())
                .willReturn(Question.builder()
                        .content("추가 질문")
                        .questionType(CUSTOM)
                        .expectedAnswer("추가 답변")
                        .build());

        Long result = questionSetService.createQuestionSetWithInitialQuestions(USER_ID, versioningRequest, "새 버전 질문 세트");

        assertNotNull(result);
        verify(userRepository, times(2)).findById(USER_ID);
        verify(questionSetRepository, times(1)).findById(parentQuestionSetId);
        verify(questionSetRepository, times(1)).findMaxVersionNumberByParentVersionId(parentQuestionSetId);
        verify(questionSetRepository, times(1)).save(any(QuestionSet.class));
        verify(questionSetRepository, times(1)).findById(3L);
        verify(questionRepository, times(2)).save(any(Question.class));
    }

    @Test
    @DisplayName("기존 질문 세트에서 새 버전 생성 시 소유자가 다르면 QuestionSetException 발생")
    void createNewQuestionSetWithQuestion_FromExisting_NotOwner_ThrowsQuestionSetException() {
        Long parentQuestionSetId = 2L;

        User otherUser = User.builder()
                .userId(3L)
                .sub("other456")
                .nickname("다른사용자")
                .build();

        QuestionSet otherUserQuestionSet = QuestionSet.builder()
                .ownerUserId(otherUser)
                .title("다른 사용자의 질문 세트")
                .description("다른 사용자 설명")
                .versionNumber(1)
                .isShared(false)
                .build();

        CreateNewQuestionSetRequest versioningRequest = CreateNewQuestionSetRequest.builder()
                .title("새 버전 질문 세트")
                .description("새 버전 설명")
                .questions(asList(
                        CreateQuestionRequest.builder()
                                .content("추가 질문")
                                .questionType(1)
                                .expectedAnswer("추가 답변")
                                .build()
                ))
                .parentVersionId(parentQuestionSetId)
                .build();

        given(userRepository.findById(USER_ID)).willReturn(of(user));
        given(questionSetRepository.findById(parentQuestionSetId)).willReturn(of(otherUserQuestionSet));

        QuestionSetException exception = assertThrows(QuestionSetException.class, () ->
                questionSetService.createQuestionSetWithInitialQuestions(USER_ID, versioningRequest, "새 버전 질문 세트"));

        assertEquals("질문 세트에 대한 권한이 없습니다.", exception.getMessage());
        verify(questionSetRepository, times(1)).findById(parentQuestionSetId);
        verify(questionSetRepository, times(0)).save(any(QuestionSet.class));
    }

    @Test
    @DisplayName("존재하지 않는 부모 질문 세트에서 새 버전 생성 시 QuestionSetException 발생")
    void createNewQuestionSetWithQuestion_FromNonExistingParent_ThrowsQuestionSetException() {
        Long nonExistingParentId = 999L;

        CreateNewQuestionSetRequest versioningRequest = CreateNewQuestionSetRequest.builder()
                .title("새 버전 질문 세트")
                .description("새 버전 설명")
                .questions(asList(
                        CreateQuestionRequest.builder()
                                .content("추가 질문")
                                .questionType(1)
                                .expectedAnswer("추가 답변")
                                .build()
                ))
                .parentVersionId(nonExistingParentId)
                .build();

        given(userRepository.findById(USER_ID)).willReturn(of(user));
        given(questionSetRepository.findById(nonExistingParentId)).willReturn(empty());

        QuestionSetException exception = assertThrows(QuestionSetException.class, () ->
                questionSetService.createQuestionSetWithInitialQuestions(USER_ID, versioningRequest, "새 버전 질문 세트"));

        assertEquals("질문 세트를 찾을 수 없습니다.", exception.getMessage());
        verify(questionSetRepository, times(1)).findById(nonExistingParentId);
        verify(questionSetRepository, times(0)).save(any(QuestionSet.class));
    }

}