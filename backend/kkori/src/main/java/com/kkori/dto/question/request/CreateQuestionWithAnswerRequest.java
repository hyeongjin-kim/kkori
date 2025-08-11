package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DEFAULT 질문
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateQuestionWithAnswerRequest {

    @NotBlank(message = "질문 내용은 필수입니다.")
    private String content;

    @NotBlank(message = "예상 답변은 필수입니다.")
    private String expectedAnswer;

    @Builder
    public CreateQuestionWithAnswerRequest(String content, String expectedAnswer) {
        this.content = content;
        this.expectedAnswer = expectedAnswer;
    }
}