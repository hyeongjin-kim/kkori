package com.kkori.dto.question.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateVersionWithAnswerModificationsRequest {

    @NotNull(message = "부모 질문 세트 ID는 필수입니다.")
    private Long parentQuestionSetId;

    @Valid
    @NotEmpty(message = "질문 리스트는 필수입니다.")
    private List<QuestionAnswerModificationRequest> questions;

    private List<Long> tagIds;

    @Builder
    public CreateVersionWithAnswerModificationsRequest(Long parentQuestionSetId, List<QuestionAnswerModificationRequest> questions, List<Long> tagIds) {
        this.parentQuestionSetId = parentQuestionSetId;
        this.questions = questions;
        this.tagIds = tagIds;
    }
}