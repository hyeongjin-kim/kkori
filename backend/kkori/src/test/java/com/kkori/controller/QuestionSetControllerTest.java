package com.kkori.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.service.QuestionSetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionSetController.class)
class QuestionSetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestionSetService questionSetService;

    @Test
    @DisplayName("새 질문 세트 생성 API 성공 테스트")
    void createQuestionSet_Success() throws Exception {

        Long userId = 123L;
        String title = "테스트 질문 세트";

        CreateQuestionRequest createQuestion = new CreateQuestionRequest("질문 내용", 1, "예상 답변");

        CreateNewQuestionSetRequest requestDto = new CreateNewQuestionSetRequest();

        BDDMockito.given(questionSetService.createNewQuestionSetWithQuestion(
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(CreateNewQuestionSetRequest.class),
                ArgumentMatchers.anyString()
        )).willReturn(1L);

        mockMvc.perform(post("/api/questionsets")
                        .param("title", title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .requestAttr("LoginUser", userId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.message").value("질문 세트와 첫 질문이 생성되었습니다"));
    }

}
