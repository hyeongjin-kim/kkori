package com.kkori.controller;

import com.kkori.annotation.LoginUser;
import com.kkori.common.CommonApiResponse;
import com.kkori.constant.QuestionSetConstants;
import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.dto.question.response.QuestionSetResponse;
import com.kkori.service.QuestionSetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questionsets")
public class QuestionSetController {

    private final QuestionSetService questionSetService;

    @PostMapping
    public ResponseEntity<CommonApiResponse<QuestionSetResponse>> createQuestionSet(
            @LoginUser Long userId,
            @RequestBody @Valid CreateNewQuestionSetRequest request
    ) {
        Long questionSetId = questionSetService.createQuestionSetWithInitialQuestions(userId, request, request.getTitle());

        String message = request.getParentVersionId() != null 
            ? QuestionSetConstants.ResponseMessage.NEW_VERSION_CREATED 
            : QuestionSetConstants.ResponseMessage.QUESTION_SET_CREATED;

        QuestionSetResponse response = QuestionSetResponse.builder()
                .id(questionSetId)
                .title(request.getTitle())
                .description(request.getDescription())
                .message(message)
                .build();

        return ResponseEntity.ok(CommonApiResponse.ok(response, message));
    }

    @PostMapping("/{questionSetId}/questions")
    public ResponseEntity<CommonApiResponse<Long>> addQuestionToQuestionSet(
            @LoginUser Long userId,
            @PathVariable Long questionSetId,
            @RequestBody @Valid CreateQuestionRequest request
    ) {
        Long questionId = questionSetService.addQuestionToQuestionSet(userId, questionSetId, request);

        return ResponseEntity.ok(CommonApiResponse.ok(questionId, QuestionSetConstants.ResponseMessage.QUESTION_ADDED));
    }

}
