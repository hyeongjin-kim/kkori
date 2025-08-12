package com.kkori.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.question.request.*;
import com.kkori.dto.question.response.*;
import com.kkori.security.CustomUserDetails;
import com.kkori.service.QuestionSetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QuestionSetController의 CRUD API에 대한 통합 테스트
 * 
 * 테스트 범위:
 * - HTTP 요청/응답 처리
 * - JSON 직렬화/역직렬화
 * - 서비스 계층과의 연동
 * - 예외 처리 및 응답 코드 검증
 */
@WebMvcTest(QuestionSetController.class)
class QuestionSetControllerCrudTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestionSetService questionSetService;

    private static final Long TEST_USER_ID = 1L;

    private void setAuthentication() {
        CustomUserDetails principal = new CustomUserDetails(
                TEST_USER_ID,
                "testUserSub",
                false,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("POST /api/questionsets - 질문세트 생성 성공")
    void createQuestionSet_Success() throws Exception {
        // Given
        CreateQuestionWithAnswerRequest questionRequest = CreateQuestionWithAnswerRequest.builder()
                .content("Spring Boot란 무엇인가요?")
                .expectedAnswer("Spring Boot는 스프링 프레임워크 기반의...")
                .build();

        CreateQuestionSetWithQuestionsRequest request = CreateQuestionSetWithQuestionsRequest.builder()
                .title("백엔드 개발자 면접 질문세트")
                .description("Spring Boot와 JPA를 활용한 백엔드 개발 역량 평가")
                .questions(Arrays.asList(questionRequest))
                .tags(Arrays.asList("하나", "둘", "셋"))
                .build();

        CreateQuestionSetResponse response = CreateQuestionSetResponse.builder()
                .questionSetId(1L)
                .title("백엔드 개발자 면접 질문세트")
                .description("Spring Boot와 JPA를 활용한 백엔드 개발 역량 평가")
                .versionNumber(1)
                .parentVersionId(null)
                .isPublic(false)
                .ownerNickname("김개발")
                .questionMaps(Arrays.asList(createQuestionMapResponse()))
                .tags(Arrays.asList(createTagResponse("Spring Boot"), createTagResponse("JPA")))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(questionSetService.createQuestionSetWithQuestions(eq(TEST_USER_ID), any(CreateQuestionSetWithQuestionsRequest.class)))
                .willReturn(response);

        setAuthentication();

        // When & Then
        mockMvc.perform(post("/api/questionsets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("질문세트가 성공적으로 생성되었습니다."))
                .andExpect(jsonPath("$.data.questionSetId").value(1))
                .andExpect(jsonPath("$.data.title").value("백엔드 개발자 면접 질문세트"))
                .andExpect(jsonPath("$.data.versionNumber").value(1))
                .andExpect(jsonPath("$.data.isPublic").value(false))
                .andExpect(jsonPath("$.data.questionMaps").isArray())
                .andExpect(jsonPath("$.data.questionMaps[0].mapId").value(1));

        verify(questionSetService).createQuestionSetWithQuestions(eq(TEST_USER_ID), any(CreateQuestionSetWithQuestionsRequest.class));
    }

    @Test
    @DisplayName("GET /api/questionsets - 질문세트 목록 조회 성공")
    void getQuestionSetList_Success() throws Exception {
        // Given
        QuestionSetListResponse listResponse = QuestionSetListResponse.builder()
                .questionSetId(1L)
                .title("백엔드 개발자 면접 질문세트")
                .description("Spring Boot와 JPA를 활용한 백엔드 개발 역량을 평가하는 질문들")
                .versionNumber(3)
                .parentVersionId(2L)
                .isPublic(true)
                .ownerNickname("김개발")
                .questionCount(5)
                .tags(Arrays.asList(createTagResponse("Spring Boot"), createTagResponse("JPA")))
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        Page<QuestionSetListResponse> pageResponse = new PageImpl<>(Arrays.asList(listResponse));

        given(questionSetService.getQuestionSetList(eq(TEST_USER_ID), eq(0), eq(10), eq("createdAt,desc"), 
                eq("me"), isNull(), isNull()))
                .willReturn(pageResponse);

        setAuthentication();

        // When & Then
        mockMvc.perform(get("/api/questionsets")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .param("createdBy", "me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].questionSetId").value(1))
                .andExpect(jsonPath("$.data.content[0].questionCount").value(5))
                .andExpect(jsonPath("$.data.content[0].isPublic").value(true));

        verify(questionSetService).getQuestionSetList(eq(TEST_USER_ID), eq(0), eq(10), 
                eq("createdAt,desc"), eq("me"), isNull(), isNull());
    }

    @Test
    @DisplayName("GET /api/questionsets/{id} - 질문세트 상세 조회 성공")
    void getQuestionSetDetail_Success() throws Exception {
        // Given
        QuestionSetDetailResponse response = QuestionSetDetailResponse.builder()
                .questionSetId(1L)
                .title("백엔드 개발자 면접 질문세트")
                .description("Spring Boot와 JPA를 활용한 백엔드 개발 역량을 평가하는 질문들")
                .versionNumber(3)
                .parentVersionId(2L)
                .isPublic(true)
                .ownerNickname("김개발")
                .questionMaps(Arrays.asList(createQuestionMapResponse()))
                .tags(Arrays.asList(createTagResponse("Spring Boot")))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(questionSetService.getQuestionSetDetailNew(TEST_USER_ID, 1L)).willReturn(response);

        setAuthentication();

        // When & Then
        mockMvc.perform(get("/api/questionsets/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.questionSetId").value(1))
                .andExpect(jsonPath("$.data.questionMaps").isArray());

        verify(questionSetService).getQuestionSetDetailNew(TEST_USER_ID, 1L);
    }

    @Test
    @DisplayName("GET /api/questionsets/my - 내 질문세트 목록 조회 성공")
    void getMyQuestionSets_Success() throws Exception {
        // Given
        QuestionSetListResponse listResponse = QuestionSetListResponse.builder()
                .questionSetId(1L)
                .title("내 질문세트")
                .description("내가 만든 질문세트")
                .versionNumber(1)
                .parentVersionId(null)
                .isPublic(false)
                .ownerNickname("김개발")
                .questionCount(3)
                .tags(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        Page<QuestionSetListResponse> pageResponse = new PageImpl<>(Arrays.asList(listResponse));
        given(questionSetService.getMyQuestionSets(TEST_USER_ID, 0, 10)).willReturn(pageResponse);

        setAuthentication();

        // When & Then
        mockMvc.perform(get("/api/questionsets/my")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("내 질문세트 목록 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].questionSetId").value(1));

        verify(questionSetService).getMyQuestionSets(TEST_USER_ID, 0, 10);
    }

    @Test
    @DisplayName("DELETE /api/questionsets/{id} - 질문세트 소프트 삭제 성공")
    void softDeleteQuestionSet_Success() throws Exception {
        // Given
        QuestionSetDetailResponse response = QuestionSetDetailResponse.builder()
                .questionSetId(1L)
                .title("삭제된 질문세트")
                .description("설명")
                .versionNumber(1)
                .parentVersionId(null)
                .isPublic(false)
                .ownerNickname("김개발")
                .questionMaps(new ArrayList<>())
                .tags(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(questionSetService.softDeleteQuestionSet(TEST_USER_ID, 1L)).willReturn(response);

        setAuthentication();

        // When & Then
        mockMvc.perform(delete("/api/questionsets/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("질문세트가 삭제되었습니다."))
                .andExpect(jsonPath("$.data.questionSetId").value(1));

        verify(questionSetService).softDeleteQuestionSet(TEST_USER_ID, 1L);
    }

    // ================== 테스트 헬퍼 메서드들 ==================

    private QuestionMapResponse createQuestionMapResponse() {
        return QuestionMapResponse.builder()
                .mapId(1L)
                .questionId(45L)
                .answerId(67L)
                .displayOrder(1)
                .question(QuestionDetailResponse.builder()
                        .id(45L)
                        .content("Spring Boot의 자동 설정(Auto Configuration) 원리에 대해 설명해주세요.")
                        .questionType(1)
                        .createdAt(LocalDateTime.now())
                        .build())
                .answer(AnswerDetailResponse.builder()
                        .id(67L)
                        .content("Spring Boot는 @EnableAutoConfiguration을 통해...")
                        .createdByNickname("김개발")
                        .createdAt(LocalDateTime.now())
                        .build())
                .build();
    }

    private TagResponse createTagResponse(String tagName) {
        return TagResponse.builder()
                .id(1L)
                .tag(tagName)
                .build();
    }
}