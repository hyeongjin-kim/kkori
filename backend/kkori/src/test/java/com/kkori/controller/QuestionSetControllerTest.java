package com.kkori.controller;

import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.security.CustomUserDetails;
import com.kkori.service.QuestionSetService;
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

}
