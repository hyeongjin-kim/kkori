package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionAnswerModificationRequest {

    @NotNull(message = "질문 ID는 필수입니다.")
    private Long questionId;

    @NotBlank(message = "새 답변은 필수입니다.")
    private String newExpectedAnswer;

    @NotNull(message = "표시 순서는 필수입니다.")
    private Integer displayOrder;

    @Builder
    public QuestionAnswerModificationRequest(Long questionId, String newExpectedAnswer, Integer displayOrder) {
        this.questionId = questionId;
        this.newExpectedAnswer = newExpectedAnswer;
        this.displayOrder = displayOrder;
    }
}