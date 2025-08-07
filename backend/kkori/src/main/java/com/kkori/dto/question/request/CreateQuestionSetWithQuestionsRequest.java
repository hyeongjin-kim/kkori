package com.kkori.dto.question.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateQuestionSetWithQuestionsRequest {

    @NotBlank(message = "질문 세트 제목은 필수입니다.")
    private String title;

    private String description;

    private List<Long> tagIds;

    @Valid
    @NotEmpty(message = "질문 리스트는 필수입니다.")
    private List<CreateQuestionWithAnswerRequest> questions;

    @Builder
    public CreateQuestionSetWithQuestionsRequest(String title, String description, 
                                               List<Long> tagIds, List<CreateQuestionWithAnswerRequest> questions) {
        this.title = title;
        this.description = description;
        this.tagIds = tagIds;
        this.questions = questions;
    }
}