package com.kkori.dto.interview.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubmitTailQuestionAnswerRequest {

    @NotNull(message = "꼬리질문 ID는 필수입니다.")
    private Long tailQuestionId;

    @NotBlank(message = "답변은 필수입니다.")
    private String userAnswer;

    public SubmitTailQuestionAnswerRequest(Long tailQuestionId, String userAnswer) {
        this.tailQuestionId = tailQuestionId;
        this.userAnswer = userAnswer;
    }
}