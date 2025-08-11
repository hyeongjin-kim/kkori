package com.kkori.dto.question.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;

@Getter
public class CreateNewQuestionSetRequest {

    @NotBlank(message = "질문 세트 제목은 필수입니다.")
    private final String title;

    private final String description;

    private final List<String> tags;

    @Valid
    @NotEmpty(message = "질문 리스트는 필수입니다.")
    private final List<CreateQuestionRequest> questions;

    private final Long parentVersionId;

    @Builder
    public CreateNewQuestionSetRequest(String title, String description,
                             List<String> tags, List<CreateQuestionRequest> questions,
                             Long parentVersionId) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.questions = questions;
        this.parentVersionId = parentVersionId;
    }

}
