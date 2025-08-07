package com.kkori.controller;

import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.dto.question.response.QuestionSetResponse;
import com.kkori.dto.question.response.QuestionSummaryResponse;
import com.kkori.dto.question.response.TagResponse;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.security.CustomUserDetails;
import com.kkori.service.QuestionSetService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(QuestionSetController.class)
class QuestionSetControllerTest {

    private static final Long TEST_USER_ID = 123L;
    private static final String TEST_TITLE = "테스트 질문 세트";
    private static final String TEST_DESCRIPTION = "테스트 질문 세트 설명";
    private static final String TEST_QUESTION_CONTENT = "질문 내용";
    private static final int TEST_QUESTION_TYPE = 1;
    private static final String TEST_EXPECTED_ANSWER = "예상 답변";
    private static final Long MOCK_QUESTION_SET_ID = 1L;

    private static final String API_URL = "/api/questionsets";
    private static final String SUCCESS_MESSAGE = "질문 세트와 첫 질문이 생성되었습니다";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestionSetService questionSetService;

    private void setAuthentication() {
        CustomUserDetails principal = new CustomUserDetails(
                QuestionSetControllerTest.TEST_USER_ID,
                "testUserSub",
                false,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("새 질문 세트 생성 API 성공 테스트")
    void createQuestionSet_Success() throws Exception {
        CreateQuestionRequest question = buildQuestion(TEST_QUESTION_CONTENT, TEST_QUESTION_TYPE);
        CreateNewQuestionSetRequest requestDto = buildRequest(TEST_TITLE, of(question));

        given(questionSetService.createQuestionSetWithInitialQuestions(
                anyLong(),
                any(CreateNewQuestionSetRequest.class),
                anyString()
        )).willReturn(MOCK_QUESTION_SET_ID);

        setAuthentication();

        mockMvc.perform(post(API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(MOCK_QUESTION_SET_ID))
                .andExpect(jsonPath("$.message").value(SUCCESS_MESSAGE));
    }

    @Test
    @DisplayName("질문 리스트가 비어 있을 때 400 Bad Request")
    void createQuestionSet_Fail_EmptyQuestions() throws Exception {
        CreateNewQuestionSetRequest requestDto = buildRequest(TEST_TITLE, of());

        given(questionSetService.createQuestionSetWithInitialQuestions(
                anyLong(),
                any(CreateNewQuestionSetRequest.class),
                anyString()
        )).willThrow(QuestionSetException.emptyQuestions());

        setAuthentication();

        mockMvc.perform(post(API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("질문 리스트는 필수입니다."));
    }

    @Test
    @DisplayName("질문 세트 제목이 없을 때 400 Bad Request")
    void createQuestionSet_Fail_NoTitle() throws Exception {
        CreateQuestionRequest question = buildQuestion(TEST_QUESTION_CONTENT, TEST_QUESTION_TYPE);
        CreateNewQuestionSetRequest requestDto = buildRequest(null, of(question));

        given(questionSetService.createQuestionSetWithInitialQuestions(
                anyLong(),
                any(CreateNewQuestionSetRequest.class),
                anyString()
        )).willThrow(QuestionSetException.noTitle());

        setAuthentication();

        mockMvc.perform(post(API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("질문 세트 제목은 필수입니다."));
    }

    @Test
    @DisplayName("질문 세트 제목이 빈 문자열일 때 400 Bad Request")
    void createQuestionSet_Fail_BlankTitle() throws Exception {
        CreateQuestionRequest question = buildQuestion(TEST_QUESTION_CONTENT, TEST_QUESTION_TYPE);
        CreateNewQuestionSetRequest requestDto = buildRequest("", of(question));

        given(questionSetService.createQuestionSetWithInitialQuestions(
                anyLong(),
                any(CreateNewQuestionSetRequest.class),
                anyString()
        )).willThrow(QuestionSetException.blankTitle());

        setAuthentication();

        mockMvc.perform(post(API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("질문 세트 제목은 필수입니다."));
    }

    @Test
    @DisplayName("질문 내용이 없을 때 400 Bad Request")
    void createQuestionSet_Fail_NoQuestionContent() throws Exception {
        CreateQuestionRequest question = buildQuestion(null, TEST_QUESTION_TYPE);
        CreateNewQuestionSetRequest requestDto = buildRequest(TEST_TITLE, of(question));

        given(questionSetService.createQuestionSetWithInitialQuestions(
                anyLong(),
                any(CreateNewQuestionSetRequest.class),
                anyString()
        )).willThrow(QuestionSetException.noQuestionContent());

        setAuthentication();

        mockMvc.perform(post(API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("질문 내용은 필수입니다."));
    }

    @Test
    @DisplayName("질문 타입이 없을 때 400 Bad Request")
    void createQuestionSet_Fail_NoQuestionType() throws Exception {
        CreateQuestionRequest question = buildQuestion(TEST_QUESTION_CONTENT, null);
        CreateNewQuestionSetRequest requestDto = buildRequest(TEST_TITLE, of(question));

        given(questionSetService.createQuestionSetWithInitialQuestions(
                anyLong(),
                any(CreateNewQuestionSetRequest.class),
                anyString()
        )).willThrow(QuestionSetException.noQuestionType());

        setAuthentication();

        mockMvc.perform(post(API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("질문 타입은 필수입니다."));
    }

    @Test
    @DisplayName("서비스 예외 발생 시 500 Internal Server Error")
    void createQuestionSet_Fail_ServiceException() throws Exception {
        CreateQuestionRequest question = buildQuestion(TEST_QUESTION_CONTENT, TEST_QUESTION_TYPE);
        CreateNewQuestionSetRequest requestDto = buildRequest(TEST_TITLE, of(question));

        given(questionSetService.createQuestionSetWithInitialQuestions(
                anyLong(),
                any(CreateNewQuestionSetRequest.class),
                anyString()
        )).willThrow(QuestionSetException.internalError());

        setAuthentication();

        mockMvc.perform(post(API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf())
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("질문 세트 처리 중 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("로그인 사용자 정보가 없을 때 401 Unauthorized")
    void createQuestionSet_Fail_NoLoginUser() throws Exception {
        CreateQuestionRequest question = buildQuestion(TEST_QUESTION_CONTENT, TEST_QUESTION_TYPE);
        CreateNewQuestionSetRequest requestDto = buildRequest(TEST_TITLE, of(question));

        // 로그인 없이 요청
        mockMvc.perform(post(API_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    private CreateQuestionRequest buildQuestion(String content, Integer questionType) {
        return CreateQuestionRequest.builder()
                .content(content)
                .questionType(questionType)
                .expectedAnswer(TEST_EXPECTED_ANSWER)
                .build();
    }

    private CreateNewQuestionSetRequest buildRequest(String title, List<CreateQuestionRequest> questions) {
        return CreateNewQuestionSetRequest.builder()
                .title(title)
                .description(TEST_DESCRIPTION)
                .questions(questions)
                .build();
    }

    // ===== GET API 테스트들 =====

    @Test
    @DisplayName("질문세트 상세 조회 성공")
    void getQuestionSet_Success() throws Exception {
        // Given
        Long questionSetId = 100L;
        QuestionSetResponse response = createQuestionSetResponse(questionSetId, TEST_TITLE, "테스트사용자");

        given(questionSetService.getQuestionSetDetail(TEST_USER_ID, questionSetId))
                .willReturn(response);

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL + "/{questionSetId}", questionSetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(questionSetId))
                .andExpect(jsonPath("$.data.title").value(TEST_TITLE))
                .andExpect(jsonPath("$.data.ownerNickname").value("테스트사용자"));

        verify(questionSetService).getQuestionSetDetail(TEST_USER_ID, questionSetId);
    }

    @Test
    @DisplayName("존재하지 않는 질문세트 조회시 404")
    void getQuestionSet_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;

        given(questionSetService.getQuestionSetDetail(TEST_USER_ID, nonExistentId))
                .willThrow(QuestionSetException.questionSetNotFound());

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL + "/{questionSetId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(questionSetService).getQuestionSetDetail(TEST_USER_ID, nonExistentId);
    }

    @Test
    @DisplayName("내 질문세트 목록 조회 성공")
    void getMyQuestionSets_Success() throws Exception {
        // Given
        QuestionSetResponse response1 = createQuestionSetResponse(1L, "질문세트1", "테스트사용자");
        QuestionSetResponse response2 = createQuestionSetResponse(2L, "질문세트2", "테스트사용자");

        given(questionSetService.getUserQuestionSets(TEST_USER_ID, 0, 10))
                .willReturn(Arrays.asList(response1, response2));

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL + "/my")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("질문세트1"))
                .andExpect(jsonPath("$.data[1].title").value("질문세트2"));

        verify(questionSetService).getUserQuestionSets(TEST_USER_ID, 0, 10);
    }

    @Test
    @DisplayName("공유 질문세트 목록 조회 성공")
    void getSharedQuestionSets_Success() throws Exception {
        // Given
        QuestionSetResponse sharedResponse = createQuestionSetResponse(100L, "공유된 질문세트", "다른사용자");
        sharedResponse = QuestionSetResponse.builder()
                .id(sharedResponse.getId())
                .title(sharedResponse.getTitle())
                .description(sharedResponse.getDescription())
                .versionNumber(sharedResponse.getVersionNumber())
                .isShared(true) // 공유됨으로 설정
                .ownerNickname("다른사용자")
                .questions(sharedResponse.getQuestions())
                .tags(sharedResponse.getTags())
                .createdAt(sharedResponse.getCreatedAt())
                .build();

        given(questionSetService.getSharedQuestionSets(TEST_USER_ID, 0, 10))
                .willReturn(Arrays.asList(sharedResponse));

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL + "/shared")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("공유된 질문세트"))
                .andExpect(jsonPath("$.data[0].isShared").value(true))
                .andExpect(jsonPath("$.data[0].ownerNickname").value("다른사용자"));

        verify(questionSetService).getSharedQuestionSets(TEST_USER_ID, 0, 10);
    }

    @Test
    @DisplayName("질문세트 버전 히스토리 조회 성공")
    void getQuestionSetVersions_Success() throws Exception {
        // Given
        Long questionSetId = 100L;
        QuestionSetResponse version1 = createVersionResponse(questionSetId, 1, "버전 1");
        QuestionSetResponse version2 = createVersionResponse(questionSetId + 1, 2, "버전 2");

        given(questionSetService.getQuestionSetVersions(TEST_USER_ID, questionSetId))
                .willReturn(Arrays.asList(version1, version2));

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL + "/{questionSetId}/versions", questionSetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].versionNumber").value(1))
                .andExpect(jsonPath("$.data[1].versionNumber").value(2));

        verify(questionSetService).getQuestionSetVersions(TEST_USER_ID, questionSetId);
    }

    private QuestionSetResponse createQuestionSetResponse(Long id, String title, String ownerNickname) {
        QuestionSummaryResponse question = QuestionSummaryResponse.builder()
                .id(1L)
                .content("테스트 질문")
                .questionType(1)
                .displayOrder(1)
                .build();

        TagResponse tag = TagResponse.builder()
                .id(1L)
                .tag("Java")
                .build();

        return QuestionSetResponse.builder()
                .id(id)
                .title(title)
                .description(TEST_DESCRIPTION)
                .versionNumber(1)
                .isShared(false)
                .ownerNickname(ownerNickname)
                .questions(Arrays.asList(question))
                .tags(Arrays.asList(tag))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private QuestionSetResponse createVersionResponse(Long id, int versionNumber, String title) {
        return QuestionSetResponse.builder()
                .id(id)
                .title(title)
                .description(TEST_DESCRIPTION)
                .versionNumber(versionNumber)
                .isShared(false)
                .ownerNickname("테스트사용자")
                .questions(Arrays.asList())
                .tags(Arrays.asList())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
