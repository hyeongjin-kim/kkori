package com.kkori.controller;

import com.kkori.annotation.LoginUser;
import com.kkori.common.CommonApiResponse;
import com.kkori.dto.question.request.*;
import com.kkori.dto.question.response.*;
import com.kkori.service.QuestionSetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questionsets")
public class QuestionSetController {

    private final QuestionSetService questionSetService;

    // ===== CREATE OPERATIONS =====

    @PostMapping
    public ResponseEntity<CommonApiResponse<CreateQuestionSetResponse>> createQuestionSet(
            @LoginUser Long userId,
            @RequestBody @Valid CreateQuestionSetWithQuestionsRequest request
    ) {
        CreateQuestionSetResponse response = questionSetService.createQuestionSetWithQuestions(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonApiResponse.ok(response, "질문세트가 성공적으로 생성되었습니다."));
    }

    @PostMapping("/copy")
    public ResponseEntity<CommonApiResponse<CreateQuestionSetResponse>> copyQuestionSet(
            @LoginUser Long userId,
            @RequestBody @Valid CopyQuestionSetRequest request
    ) {
        CreateQuestionSetResponse response = questionSetService.copyQuestionSet(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonApiResponse.ok(response, "질문세트가 복사되었습니다."));
    }

    @PostMapping("/{questionSetId}/versions/with-new-qa")
    public ResponseEntity<CommonApiResponse<CreateQuestionSetResponse>> createVersionWithNewQA(
            @LoginUser Long userId,
            @PathVariable Long questionSetId,
            @RequestBody @Valid CreateVersionWithNewQARequest request
    ) {
        // PathVariable에서 받은 ID를 request에 설정
        CreateVersionWithNewQARequest updatedRequest = CreateVersionWithNewQARequest.builder()
                .parentQuestionSetId(questionSetId)
                .questions(request.getQuestions())
                .tagIds(request.getTagIds())
                .build();
        
        CreateQuestionSetResponse response = questionSetService.createVersionWithNewQA(userId, updatedRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonApiResponse.ok(response, "새 질문-답변으로 새 버전이 생성되었습니다."));
    }

    @PostMapping("/{questionSetId}/versions/with-answer-modifications")
    public ResponseEntity<CommonApiResponse<CreateQuestionSetResponse>> createVersionWithAnswerModifications(
            @LoginUser Long userId,
            @PathVariable Long questionSetId,
            @RequestBody @Valid CreateVersionWithAnswerModificationsRequest request
    ) {
        // PathVariable에서 받은 ID를 request에 설정
        CreateVersionWithAnswerModificationsRequest updatedRequest = CreateVersionWithAnswerModificationsRequest.builder()
                .parentQuestionSetId(questionSetId)
                .questions(request.getQuestions())
                .tagIds(request.getTagIds())
                .build();
        
        CreateQuestionSetResponse response = questionSetService.createVersionWithAnswerModifications(userId, updatedRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonApiResponse.ok(response, "기존 질문+새 답변으로 새 버전이 생성되었습니다."));
    }

    // ===== READ OPERATIONS =====

    @GetMapping
    public ResponseEntity<CommonApiResponse<Page<QuestionSetListResponse>>> getQuestionSetList(
            @LoginUser Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) Boolean isShared,
            @RequestParam(required = false) List<String> tags
    ) {
        Page<QuestionSetListResponse> responses = questionSetService.getQuestionSetList(
                userId, page, size, sort, createdBy, isShared, tags);
        return ResponseEntity.ok(CommonApiResponse.ok(responses, "질문세트 목록 조회가 완료되었습니다."));
    }

    @GetMapping("/{questionSetId}")
    public ResponseEntity<CommonApiResponse<QuestionSetDetailResponse>> getQuestionSetDetail(
            @LoginUser Long userId,
            @PathVariable Long questionSetId
    ) {
        QuestionSetDetailResponse response = questionSetService.getQuestionSetDetailNew(userId, questionSetId);
        return ResponseEntity.ok(CommonApiResponse.ok(response, "질문세트 상세 조회가 완료되었습니다."));
    }

    @GetMapping("/my")
    public ResponseEntity<CommonApiResponse<Page<QuestionSetListResponse>>> getMyQuestionSets(
            @LoginUser Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<QuestionSetListResponse> responses = questionSetService.getMyQuestionSets(userId, page, size);
        return ResponseEntity.ok(CommonApiResponse.ok(responses, "내 질문세트 목록 조회가 완료되었습니다."));
    }

    @GetMapping("/shared")
    public ResponseEntity<CommonApiResponse<Page<QuestionSetListResponse>>> getSharedQuestionSets(
            @LoginUser Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<QuestionSetListResponse> responses = questionSetService.getSharedQuestionSetsNew(userId, page, size);
        return ResponseEntity.ok(CommonApiResponse.ok(responses, "공유 질문세트 목록 조회가 완료되었습니다."));
    }

    // ===== UPDATE OPERATIONS =====

    @PutMapping("/{questionSetId}/metadata")
    public ResponseEntity<CommonApiResponse<QuestionSetDetailResponse>> updateQuestionSetMetadata(
            @LoginUser Long userId,
            @PathVariable Long questionSetId,
            @RequestBody @Valid UpdateQuestionSetMetadataRequest request
    ) {
        QuestionSetDetailResponse response = questionSetService.updateQuestionSetMetadata(userId, questionSetId, request);
        return ResponseEntity.ok(CommonApiResponse.ok(response, "질문세트 메타데이터가 업데이트되었습니다."));
    }

    @PutMapping("/{questionSetId}/answers")
    public ResponseEntity<CommonApiResponse<QuestionMapResponse>> modifyAnswer(
            @LoginUser Long userId,
            @PathVariable Long questionSetId,
            @RequestBody @Valid ModifyAnswerRequest request
    ) {
        QuestionMapResponse response = questionSetService.modifyAnswer(userId, questionSetId, request);
        return ResponseEntity.ok(CommonApiResponse.ok(response, "답변이 수정되었습니다."));
    }

    // ===== DELETE OPERATIONS =====

    @DeleteMapping("/{questionSetId}")
    public ResponseEntity<CommonApiResponse<QuestionSetDetailResponse>> softDeleteQuestionSet(
            @LoginUser Long userId,
            @PathVariable Long questionSetId
    ) {
        QuestionSetDetailResponse response = questionSetService.softDeleteQuestionSet(userId, questionSetId);
        return ResponseEntity.ok(CommonApiResponse.ok(response, "질문세트가 삭제되었습니다."));
    }

    @DeleteMapping("/{questionSetId}/questions/{mapId}")
    public ResponseEntity<CommonApiResponse<QuestionMapResponse>> removeQuestionFromSet(
            @LoginUser Long userId,
            @PathVariable Long questionSetId,
            @PathVariable Long mapId
    ) {
        QuestionMapResponse response = questionSetService.removeQuestionFromSet(userId, questionSetId, mapId);
        return ResponseEntity.ok(CommonApiResponse.ok(response, "질문이 질문세트에서 제거되었습니다."));
    }
}
