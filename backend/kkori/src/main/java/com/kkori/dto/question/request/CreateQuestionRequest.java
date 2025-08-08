package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * DEFAULT 전용
 */
@Getter
public class CreateQuestionRequest {

    @NotBlank(message = "질문 내용은 필수입니다.")
    private final String content;

    private final String expectedAnswer;

    @Builder
    public CreateQuestionRequest(String content, String expectedAnswer) {
        this.content = content;
        this.expectedAnswer = expectedAnswer;
    }

}
