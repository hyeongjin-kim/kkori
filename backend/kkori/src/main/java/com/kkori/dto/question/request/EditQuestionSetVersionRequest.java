package com.kkori.dto.question.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditQuestionSetVersionRequest {

    @NotNull(message = "부모 질문 세트 ID는 필수입니다.")
    private Long parentQuestionSetId;

    @Valid
    private List<QuestionAnswerModificationRequest> existingQuestions;

    @Valid
    private List<CreateQuestionWithAnswerRequest> newQuestions;

    private List<String> tags;

    @Builder
    public EditQuestionSetVersionRequest(Long parentQuestionSetId, 
                                       List<QuestionAnswerModificationRequest> existingQuestions,
                                       List<CreateQuestionWithAnswerRequest> newQuestions,
                                       List<String> tags) {
        this.parentQuestionSetId = parentQuestionSetId;
        this.existingQuestions = existingQuestions;
        this.newQuestions = newQuestions;
        this.tags = tags;
    }
}