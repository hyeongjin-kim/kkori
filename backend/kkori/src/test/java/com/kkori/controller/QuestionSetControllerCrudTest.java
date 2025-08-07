package com.kkori.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.question.request.*;
import com.kkori.dto.question.response.*;
import com.kkori.service.QuestionSetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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

    @MockBean
    private QuestionSetService questionSetService;

    private static final Long TEST_USER_ID = 1L;

    @Nested
    @DisplayName("CREATE API 테스트")
    class CreateApiTest {

        @Test
        @DisplayName("POST /api/questionsets - 질문세트 생성 성공")
        void createQuestionSet_Success() throws Exception {
            // Given
            CreateQuestionWithAnswerRequest questionRequest = CreateQuestionWithAnswerRequest.builder()
                    .content("Spring Boot란 무엇인가요?")
                    .questionType(1)
                    .expectedAnswer("Spring Boot는 스프링 프레임워크 기반의...")
                    .build();

            CreateQuestionSetWithQuestionsRequest request = CreateQuestionSetWithQuestionsRequest.builder()
                    .title("백엔드 개발자 면접 질문세트")
                    .description("Spring Boot와 JPA를 활용한 백엔드 개발 역량 평가")
                    .questions(Arrays.asList(questionRequest))
                    .tagIds(Arrays.asList(1L, 2L, 3L))
                    .build();

            CreateQuestionSetResponse response = CreateQuestionSetResponse.builder()
                    .questionSetId(1L)
                    .title("백엔드 개발자 면접 질문세트")
                    .description("Spring Boot와 JPA를 활용한 백엔드 개발 역량 평가")
                    .versionNumber(1)
                    .parentVersionId(null)
                    .isShared(false)
                    .ownerNickname("김개발")
                    .questionMaps(Arrays.asList(createQuestionMapResponse()))
                    .tags(Arrays.asList(createTagResponse("Spring Boot"), createTagResponse("JPA")))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            given(questionSetService.createQuestionSetWithQuestions(eq(TEST_USER_ID), any(CreateQuestionSetWithQuestionsRequest.class)))
                    .willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/questionsets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User-Id", TEST_USER_ID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.message").value("질문세트가 성공적으로 생성되었습니다."))
                    .andExpect(jsonPath("$.data.questionSetId").value(1))
                    .andExpect(jsonPath("$.data.title").value("백엔드 개발자 면접 질문세트"))
                    .andExpect(jsonPath("$.data.versionNumber").value(1))
                    .andExpect(jsonPath("$.data.isShared").value(false))
                    .andExpect(jsonPath("$.data.questionMaps").isArray())
                    .andExpect(jsonPath("$.data.questionMaps[0].mapId").value(1));

            verify(questionSetService).createQuestionSetWithQuestions(eq(TEST_USER_ID), any(CreateQuestionSetWithQuestionsRequest.class));
        }

        @Test
        @DisplayName("POST /api/questionsets/copy - 질문세트 복사 성공")
        void copyQuestionSet_Success() throws Exception {
            // Given
            CopyQuestionSetRequest request = CopyQuestionSetRequest.builder()
                    .originalQuestionSetId(1L)
                    .title("백엔드 개발자 면접 질문세트 (복사본)")
                    .description("김개발님의 질문세트를 복사하여 수정")
                    .copyTags(true)
                    .build();

            CreateQuestionSetResponse response = CreateQuestionSetResponse.builder()
                    .questionSetId(5L)
                    .title("백엔드 개발자 면접 질문세트 (복사본)")
                    .description("김개발님의 질문세트를 복사하여 수정")
                    .versionNumber(1)
                    .parentVersionId(1L)
                    .isShared(false)
                    .ownerNickname("이신입")
                    .questionMaps(Arrays.asList(createQuestionMapResponse()))
                    .tags(new ArrayList<>())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            given(questionSetService.copyQuestionSet(eq(TEST_USER_ID), any(CopyQuestionSetRequest.class)))
                    .willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/questionsets/copy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User-Id", TEST_USER_ID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("질문세트가 복사되었습니다."))
                    .andExpect(jsonPath("$.data.questionSetId").value(5))
                    .andExpect(jsonPath("$.data.parentVersionId").value(1))
                    .andExpect(jsonPath("$.data.ownerNickname").value("이신입"));

            verify(questionSetService).copyQuestionSet(eq(TEST_USER_ID), any(CopyQuestionSetRequest.class));
        }

        @Test
        @DisplayName("POST /api/questionsets/{id}/versions/with-new-qa - 새 질문-답변으로 새 버전 생성 성공")
        void createVersionWithNewQA_Success() throws Exception {
            // Given
            CreateQuestionWithAnswerRequest questionRequest = CreateQuestionWithAnswerRequest.builder()
                    .content("마이크로서비스 아키텍처에 대해 설명해주세요.")
                    .questionType(1)
                    .expectedAnswer("마이크로서비스 아키텍처는 대규모 애플리케이션을...")
                    .build();

            CreateVersionWithNewQARequest request = CreateVersionWithNewQARequest.builder()
                    .parentQuestionSetId(1L)
                    .questions(Arrays.asList(questionRequest))
                    .tagIds(Arrays.asList(1L, 2L))
                    .build();

            CreateQuestionSetResponse response = CreateQuestionSetResponse.builder()
                    .questionSetId(9L)
                    .title("백엔드 개발자 면접 질문세트")
                    .description("Spring Boot, JPA, MSA를 활용한 백엔드 개발 역량 평가")
                    .versionNumber(2)
                    .parentVersionId(1L)
                    .isShared(false)
                    .ownerNickname("김개발")
                    .questionMaps(Arrays.asList(createQuestionMapResponse()))
                    .tags(new ArrayList<>())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            given(questionSetService.createVersionWithNewQA(eq(TEST_USER_ID), any(CreateVersionWithNewQARequest.class)))
                    .willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/questionsets/1/versions/with-new-qa")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User-Id", TEST_USER_ID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("새 질문-답변으로 새 버전이 생성되었습니다."))
                    .andExpect(jsonPath("$.data.versionNumber").value(2))
                    .andExpect(jsonPath("$.data.parentVersionId").value(1));

            verify(questionSetService).createVersionWithNewQA(eq(TEST_USER_ID), any(CreateVersionWithNewQARequest.class));
        }

        @Test
        @DisplayName("POST /api/questionsets/{id}/versions/with-answer-modifications - 기존 질문+새 답변으로 새 버전 생성 성공")
        void createVersionWithAnswerModifications_Success() throws Exception {
            // Given
            QuestionAnswerModificationRequest questionRequest = QuestionAnswerModificationRequest.builder()
                    .questionId(45L)
                    .newExpectedAnswer("Spring Boot는 자동 설정과 Starter 의존성을 통해...")
                    .displayOrder(1)
                    .build();

            CreateVersionWithAnswerModificationsRequest request = CreateVersionWithAnswerModificationsRequest.builder()
                    .parentQuestionSetId(1L)
                    .questions(Arrays.asList(questionRequest))
                    .tagIds(Arrays.asList(1L, 2L))
                    .build();

            CreateQuestionSetResponse response = CreateQuestionSetResponse.builder()
                    .questionSetId(10L)
                    .title("백엔드 개발자 면접 질문세트")
                    .description("Spring Boot, JPA, MSA를 활용한 백엔드 개발 역량 평가")
                    .versionNumber(3)
                    .parentVersionId(1L)
                    .isShared(false)
                    .ownerNickname("김개발")
                    .questionMaps(Arrays.asList(createQuestionMapResponse()))
                    .tags(new ArrayList<>())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            given(questionSetService.createVersionWithAnswerModifications(eq(TEST_USER_ID), any(CreateVersionWithAnswerModificationsRequest.class)))
                    .willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/questionsets/1/versions/with-answer-modifications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User-Id", TEST_USER_ID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("기존 질문+새 답변으로 새 버전이 생성되었습니다."))
                    .andExpect(jsonPath("$.data.versionNumber").value(3))
                    .andExpect(jsonPath("$.data.parentVersionId").value(1));

            verify(questionSetService).createVersionWithAnswerModifications(eq(TEST_USER_ID), any(CreateVersionWithAnswerModificationsRequest.class));
        }

        // 예외 케이스 테스트 추가
        @Test
        @DisplayName("POST /api/questionsets/{id}/versions/with-new-qa - 빈 질문 리스트로 실패")
        void createVersionWithNewQA_EmptyQuestions_Fail() throws Exception {
            // Given
            CreateVersionWithNewQARequest request = CreateVersionWithNewQARequest.builder()
                    .parentQuestionSetId(1L)
                    .questions(new ArrayList<>()) // 빈 리스트
                    .tagIds(Arrays.asList(1L, 2L))
                    .build();

            // When & Then
            mockMvc.perform(post("/api/questionsets/1/versions/with-new-qa")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User-Id", TEST_USER_ID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/questionsets/{id}/versions/with-answer-modifications - 유효하지 않은 질문 ID로 실패")
        void createVersionWithAnswerModifications_InvalidQuestionId_Fail() throws Exception {
            // Given
            QuestionAnswerModificationRequest questionRequest = QuestionAnswerModificationRequest.builder()
                    .questionId(null) // null ID
                    .newExpectedAnswer("새로운 답변")
                    .displayOrder(1)
                    .build();

            CreateVersionWithAnswerModificationsRequest request = CreateVersionWithAnswerModificationsRequest.builder()
                    .parentQuestionSetId(1L)
                    .questions(Arrays.asList(questionRequest))
                    .build();

            // When & Then
            mockMvc.perform(post("/api/questionsets/1/versions/with-answer-modifications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User-Id", TEST_USER_ID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("READ API 테스트")
    class ReadApiTest {

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
                    .isShared(true)
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

            // When & Then
            mockMvc.perform(get("/api/questionsets")
                            .header("X-User-Id", TEST_USER_ID)
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
                    .andExpect(jsonPath("$.data.content[0].isShared").value(true));

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
                    .isShared(true)
                    .ownerNickname("김개발")
                    .questionMaps(Arrays.asList(createQuestionMapResponse()))
                    .tags(Arrays.asList(createTagResponse("Spring Boot")))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            given(questionSetService.getQuestionSetDetailNew(TEST_USER_ID, 1L)).willReturn(response);

            // When & Then
            mockMvc.perform(get("/api/questionsets/1")
                            .header("X-User-Id", TEST_USER_ID))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.questionSetId").value(1))
                    .andExpect(jsonPath("$.data.questionMaps").isArray());

            verify(questionSetService).getQuestionSetDetailNew(TEST_USER_ID, 1L);
        }

        @Test
        @DisplayName("GET /api/questionsets/{id}/versions - 버전 히스토리 조회 성공")
        void getQuestionSetVersions_Success() throws Exception {
            // Given
            QuestionSetListResponse version1 = createQuestionSetListResponse(1L, "v1", 1);
            QuestionSetListResponse version2 = createQuestionSetListResponse(2L, "v2", 2);
            List<QuestionSetListResponse> versions = Arrays.asList(version1, version2);

            given(questionSetService.getQuestionSetVersionsNew(TEST_USER_ID, 1L)).willReturn(versions);

            // When & Then
            mockMvc.perform(get("/api/questionsets/1/versions")
                            .header("X-User-Id", TEST_USER_ID))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].versionNumber").value(1))
                    .andExpect(jsonPath("$.data[1].versionNumber").value(2));

            verify(questionSetService).getQuestionSetVersionsNew(TEST_USER_ID, 1L);
        }
    }

    @Nested
    @DisplayName("UPDATE API 테스트") 
    class UpdateApiTest {

        @Test
        @DisplayName("PUT /api/questionsets/{id}/metadata - 메타데이터 수정 성공")
        void updateQuestionSetMetadata_Success() throws Exception {
            // Given
            UpdateQuestionSetMetadataRequest request = UpdateQuestionSetMetadataRequest.builder()
                    .title("백엔드 개발자 면접 질문세트 (업데이트)")
                    .description("Spring Boot, JPA, MSA를 활용한 백엔드 개발 역량을 평가하는 질문들")
                    .isShared(true)
                    .build();

            QuestionSetDetailResponse response = QuestionSetDetailResponse.builder()
                    .questionSetId(1L)
                    .title("백엔드 개발자 면접 질문세트 (업데이트)")
                    .description("Spring Boot, JPA, MSA를 활용한 백엔드 개발 역량을 평가하는 질문들")
                    .versionNumber(1)
                    .parentVersionId(null)
                    .isShared(true)
                    .ownerNickname("김개발")
                    .questionMaps(new ArrayList<>())
                    .tags(new ArrayList<>())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            given(questionSetService.updateQuestionSetMetadata(eq(TEST_USER_ID), eq(1L), any(UpdateQuestionSetMetadataRequest.class)))
                    .willReturn(response);

            // When & Then
            mockMvc.perform(put("/api/questionsets/1/metadata")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User-Id", TEST_USER_ID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("질문세트 메타데이터가 업데이트되었습니다."))
                    .andExpect(jsonPath("$.data.title").value("백엔드 개발자 면접 질문세트 (업데이트)"))
                    .andExpect(jsonPath("$.data.isShared").value(true));

            verify(questionSetService).updateQuestionSetMetadata(eq(TEST_USER_ID), eq(1L), any(UpdateQuestionSetMetadataRequest.class));
        }

        @Test
        @DisplayName("PUT /api/questionsets/{id}/answers - 답변 수정 성공")
        void modifyAnswer_Success() throws Exception {
            // Given
            ModifyAnswerRequest request = ModifyAnswerRequest.builder()
                    .mapId(1L)
                    .newExpectedAnswer("Spring Boot는 @EnableAutoConfiguration과 자동 설정 클래스들을 통해 개발자가 별도 설정 없이도 필요한 빈들을 자동으로 구성해주는 편리한 기능입니다.")
                    .build();

            QuestionMapResponse response = createQuestionMapResponse();

            given(questionSetService.modifyAnswer(eq(TEST_USER_ID), eq(1L), any(ModifyAnswerRequest.class)))
                    .willReturn(response);

            // When & Then
            mockMvc.perform(put("/api/questionsets/1/answers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User-Id", TEST_USER_ID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("답변이 수정되었습니다."))
                    .andExpect(jsonPath("$.data.mapId").value(1));

            verify(questionSetService).modifyAnswer(eq(TEST_USER_ID), eq(1L), any(ModifyAnswerRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE API 테스트")
    class DeleteApiTest {

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
                    .isShared(false)
                    .ownerNickname("김개발")
                    .questionMaps(new ArrayList<>())
                    .tags(new ArrayList<>())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            given(questionSetService.softDeleteQuestionSet(TEST_USER_ID, 1L)).willReturn(response);

            // When & Then
            mockMvc.perform(delete("/api/questionsets/1")
                            .header("X-User-Id", TEST_USER_ID))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("질문세트가 삭제되었습니다."))
                    .andExpect(jsonPath("$.data.questionSetId").value(1));

            verify(questionSetService).softDeleteQuestionSet(TEST_USER_ID, 1L);
        }

        @Test
        @DisplayName("DELETE /api/questionsets/{id}/questions/{mapId} - 질문 제거 성공")
        void removeQuestionFromSet_Success() throws Exception {
            // Given
            QuestionMapResponse response = createQuestionMapResponse();

            given(questionSetService.removeQuestionFromSet(TEST_USER_ID, 1L, 2L)).willReturn(response);

            // When & Then
            mockMvc.perform(delete("/api/questionsets/1/questions/2")
                            .header("X-User-Id", TEST_USER_ID))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("질문이 질문세트에서 제거되었습니다."))
                    .andExpect(jsonPath("$.data.mapId").value(1));

            verify(questionSetService).removeQuestionFromSet(TEST_USER_ID, 1L, 2L);
        }
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
                        .createdBy("김개발")
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

    private QuestionSetListResponse createQuestionSetListResponse(Long id, String titleSuffix, Integer versionNumber) {
        return QuestionSetListResponse.builder()
                .questionSetId(id)
                .title("질문세트 " + titleSuffix)
                .description("설명")
                .versionNumber(versionNumber)
                .parentVersionId(versionNumber > 1 ? 1L : null)
                .isShared(false)
                .ownerNickname("김개발")
                .questionCount(3)
                .tags(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();
    }

}