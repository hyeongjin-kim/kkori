package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateQuestionRequest {

    @NotBlank(message = "질문 내용은 필수입니다.")
    private final String content;

    @NotNull(message = "질문 타입은 필수입니다.")
    private final Integer questionType;

    private final String expectedAnswer;

    @Builder
    public CreateQuestionRequest(String content, Integer questionType, String expectedAnswer) {
        this.content = content;
        this.questionType = questionType;
        this.expectedAnswer = expectedAnswer;
    }

}
