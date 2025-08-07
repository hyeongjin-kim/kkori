package com.kkori.dto.question.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateQuestionWithAnswerRequest {

    @NotBlank(message = "질문 내용은 필수입니다.")
    private String content;

    @NotNull(message = "질문 타입은 필수입니다.")
    private Integer questionType;

    @NotBlank(message = "예상 답변은 필수입니다.")
    private String expectedAnswer;

    @Valid
    private List<CreateTailQuestionRequest> tailQuestions;

    @Builder
    public CreateQuestionWithAnswerRequest(String content, Integer questionType, 
                                         String expectedAnswer, List<CreateTailQuestionRequest> tailQuestions) {
        this.content = content;
        this.questionType = questionType;
        this.expectedAnswer = expectedAnswer;
        this.tailQuestions = tailQuestions;
    }
}