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
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.dto.question.request.UpdateQuestionSetMetadataRequest;
import com.kkori.dto.question.response.QuestionSetResponse;
import com.kkori.dto.question.response.QuestionSetListResponse;
import com.kkori.dto.question.response.QuestionSetDetailResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
    @DisplayName("공개 질문세트 목록 조회 성공")
    void getPublicQuestionSets_Success() throws Exception {
        // Given
        Page<QuestionSetListResponse> publicResponsePage = createMockPage(Arrays.asList(
                createQuestionSetListResponse(100L, "공개된 질문세트", "다른사용자", true)
        ));
        
        given(questionSetService.getSharedQuestionSetsNew(TEST_USER_ID, 0, 10))
                .willReturn(publicResponsePage);

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL + "/public")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].title").value("공개된 질문세트"))
                .andExpect(jsonPath("$.data.content[0].isPublic").value(true))
                .andExpect(jsonPath("$.data.content[0].ownerNickname").value("다른사용자"));

        verify(questionSetService).getSharedQuestionSetsNew(TEST_USER_ID, 0, 10);
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
                .isPublic(false)
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
                .isPublic(false)
                .ownerNickname("테스트사용자")
                .questions(Arrays.asList())
                .tags(Arrays.asList())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("공개 질문세트만 필터링 조회 성공")
    void getQuestionSetList_FilterByPublic_Success() throws Exception {
        // Given
        QuestionSetListResponse publicResponse = createQuestionSetListResponse(100L, "공개된 질문세트", "다른사용자", true);
        Page<QuestionSetListResponse> responsePage = createMockPage(Arrays.asList(publicResponse));

        given(questionSetService.getQuestionSetList(eq(TEST_USER_ID), eq(0), eq(10), 
                anyString(), any(), eq(true), any()))
                .willReturn(responsePage);

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("isPublic", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].isPublic").value(true));

        verify(questionSetService).getQuestionSetList(eq(TEST_USER_ID), eq(0), eq(10), 
                anyString(), any(), eq(true), any());
    }

    @Test
    @DisplayName("비공개 질문세트만 필터링 조회 성공")
    void getQuestionSetList_FilterByPrivate_Success() throws Exception {
        // Given
        QuestionSetListResponse privateResponse = createQuestionSetListResponse(100L, "비공개 질문세트", "테스트사용자", false);
        Page<QuestionSetListResponse> responsePage = createMockPage(Arrays.asList(privateResponse));

        given(questionSetService.getQuestionSetList(eq(TEST_USER_ID), eq(0), eq(10), 
                anyString(), any(), eq(false), any()))
                .willReturn(responsePage);

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("isPublic", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].isPublic").value(false));

        verify(questionSetService).getQuestionSetList(eq(TEST_USER_ID), eq(0), eq(10), 
                anyString(), any(), eq(false), any());
    }

    @Test
    @DisplayName("질문세트 공개 상태 변경 테스트")
    void updateQuestionSetMetadata_ChangePublicStatus() throws Exception {
        // Given
        Long questionSetId = 1L;
        UpdateQuestionSetMetadataRequest request = UpdateQuestionSetMetadataRequest.builder()
                .title("업데이트된 제목")
                .description("업데이트된 설명")
                .isPublic(true)
                .build();

        QuestionSetDetailResponse response = createQuestionSetDetailResponse(questionSetId, "업데이트된 제목", true);

        given(questionSetService.updateQuestionSetMetadata(eq(TEST_USER_ID), eq(questionSetId), any(UpdateQuestionSetMetadataRequest.class)))
                .willReturn(response);

        setAuthentication();

        // When & Then
        mockMvc.perform(put(API_URL + "/" + questionSetId + "/metadata")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isPublic").value(true))
                .andExpect(jsonPath("$.message").value("질문세트 메타데이터가 업데이트되었습니다."));

        verify(questionSetService).updateQuestionSetMetadata(eq(TEST_USER_ID), eq(questionSetId), any(UpdateQuestionSetMetadataRequest.class));
    }

    @Test
    @DisplayName("공개되지 않은 다른 사용자의 비공개 질문세트에는 접근 불가")
    void getQuestionSetDetail_AccessOthersPrivateSet_Forbidden() throws Exception {
        // Given
        Long privateQuestionSetId = 999L;
        
//        given(questionSetService.getQuestionSetDetailNew(TEST_USER_ID, privateQuestionSetId))
//                .willThrow(QuestionSetException.accessDenied());

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL + "/" + privateQuestionSetId))
                .andExpect(status().isForbidden());

        verify(questionSetService).getQuestionSetDetailNew(TEST_USER_ID, privateQuestionSetId);
    }

    @Test
    @DisplayName("공개 질문세트는 소유자가 아니어도 조회 가능")
    void getQuestionSetDetail_AccessPublicSet_Success() throws Exception {
        // Given
        Long publicQuestionSetId = 888L;
        QuestionSetDetailResponse publicResponse = createQuestionSetDetailResponse(publicQuestionSetId, "다른 사용자의 공개 질문세트", true);
        publicResponse = QuestionSetDetailResponse.builder()
                .questionSetId(publicResponse.getQuestionSetId())
                .title(publicResponse.getTitle())
                .description(publicResponse.getDescription())
                .versionNumber(publicResponse.getVersionNumber())
                .parentVersionId(publicResponse.getParentVersionId())
                .isPublic(true)
                .ownerNickname("다른사용자")
                .questionMaps(publicResponse.getQuestionMaps())
                .tags(publicResponse.getTags())
                .createdAt(publicResponse.getCreatedAt())
                .updatedAt(publicResponse.getUpdatedAt())
                .build();

        given(questionSetService.getQuestionSetDetailNew(TEST_USER_ID, publicQuestionSetId))
                .willReturn(publicResponse);

        setAuthentication();

        // When & Then
        mockMvc.perform(get(API_URL + "/" + publicQuestionSetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isPublic").value(true))
                .andExpect(jsonPath("$.data.ownerNickname").value("다른사용자"))
                .andExpect(jsonPath("$.message").value("질문세트 상세 조회가 완료되었습니다."));

        verify(questionSetService).getQuestionSetDetailNew(TEST_USER_ID, publicQuestionSetId);
    }

    // Helper methods for new tests
    private QuestionSetListResponse createQuestionSetListResponse(Long id, String title, String ownerNickname, boolean isPublic) {
        return QuestionSetListResponse.builder()
                .questionSetId(id)
                .title(title)
                .description(TEST_DESCRIPTION)
                .versionNumber(1)
                .isPublic(isPublic)
                .ownerNickname(ownerNickname)
                .tags(Arrays.asList(createTagResponse("Test")))
                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
                .build();
    }

    private QuestionSetDetailResponse createQuestionSetDetailResponse(Long id, String title, boolean isPublic) {
        return QuestionSetDetailResponse.builder()
                .questionSetId(id)
                .title(title)
                .description(TEST_DESCRIPTION)
                .versionNumber(1)
                .parentVersionId(null)
                .isPublic(isPublic)
                .ownerNickname("테스트사용자")
                .questionMaps(Arrays.asList())
                .tags(Arrays.asList(createTagResponse("Test")))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private TagResponse createTagResponse(String tagName) {
        return TagResponse.builder()
                .id(1L)
                .tag(tagName)
                .build();
    }

    private <T> Page<T> createMockPage(List<T> content) {
        return new PageImpl<>(content, PageRequest.of(0, 10), content.size());
    }

}
