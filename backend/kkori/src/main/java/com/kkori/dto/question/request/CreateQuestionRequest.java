package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateQuestionRequest {

    @NotBlank
    private String content;

    @NotNull
    private Integer questionType;

    private String expectedAnswer;

    @Builder
    public CreateQuestionRequest(String content, Integer questionType, String expectedAnswer) {
        this.content = content;
        this.questionType = questionType;
        this.expectedAnswer = expectedAnswer;
    }

}
