package com.kkori.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.service.QuestionSetService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    @DisplayName("새 질문 세트 생성 API 성공 테스트")
    void createQuestionSet_Success() throws Exception {

        CreateQuestionRequest question = CreateQuestionRequest.builder()
                .content(TEST_QUESTION_CONTENT)
                .questionType(TEST_QUESTION_TYPE)
                .expectedAnswer(TEST_EXPECTED_ANSWER)
                .build();

        CreateNewQuestionSetRequest requestDto = CreateNewQuestionSetRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .questions(List.of(question))
                .build();

        BDDMockito.given(questionSetService.createNewQuestionSetWithQuestion(
                anyLong(),
                any(CreateNewQuestionSetRequest.class),
                anyString()
        )).willReturn(MOCK_QUESTION_SET_ID);

        mockMvc.perform(post(API_URL)
                        .param("title", TEST_TITLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .requestAttr("LoginUser", TEST_USER_ID)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(MOCK_QUESTION_SET_ID))
                .andExpect(jsonPath("$.data.message").value(SUCCESS_MESSAGE));
    }

}
