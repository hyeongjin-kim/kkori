package com.kkori.controller;


import com.kkori.annotation.LoginUser;
import com.kkori.common.CommonApiResponse;
import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.response.QuestionSetResponse;
import com.kkori.service.QuestionSetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questionsets")
public class QuestionSetController {

    private final QuestionSetService questionSetService;

    @PostMapping("")
    public ResponseEntity<CommonApiResponse<QuestionSetResponse>> createQuestionSet(
            @LoginUser Long userId,
            @RequestParam String title,
            @RequestBody @Valid CreateNewQuestionSetRequest request
    ) {
        return null;
    }

}
