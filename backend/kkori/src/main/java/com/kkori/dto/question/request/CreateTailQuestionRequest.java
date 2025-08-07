package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateTailQuestionRequest {

    @NotBlank(message = "꼬리 질문 내용은 필수입니다.")
    private String content;

    @Builder
    public CreateTailQuestionRequest(String content) {
        this.content = content;
    }
}