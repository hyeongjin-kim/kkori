package com.kkori.controller;

import com.kkori.annotation.LoginUser;
import com.kkori.common.CommonApiResponse;
import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.response.QuestionSetResponse;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.service.QuestionSetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        if (request.getTitle() == null) {
            throw QuestionSetException.noTitle();
        }
        if (request.getTitle().trim().isEmpty()) {
            throw QuestionSetException.blankTitle();
        }
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw QuestionSetException.emptyQuestions();
        }

        Long questionSetId = questionSetService.createNewQuestionSetWithQuestion(userId, request, request.getTitle());

        QuestionSetResponse response = QuestionSetResponse.builder()
                .id(questionSetId)
                .title(request.getTitle())
                .description(request.getDescription())
                .message("질문 세트와 첫 질문이 생성되었습니다")
                .build();

        return ResponseEntity.ok(CommonApiResponse.ok(response, "질문 세트와 첫 질문이 생성되었습니다"));
    }

}
