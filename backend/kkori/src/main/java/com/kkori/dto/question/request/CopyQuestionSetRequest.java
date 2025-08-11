package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CopyQuestionSetRequest {

    @NotNull(message = "복사할 질문 세트 ID는 필수입니다.")
    private Long originalQuestionSetId;

    private String title;

    private String description;

    private Boolean copyTags = true;

    @Builder
    public CopyQuestionSetRequest(Long originalQuestionSetId, String title, 
                                String description, Boolean copyTags) {
        this.originalQuestionSetId = originalQuestionSetId;
        this.title = title;
        this.description = description;
        this.copyTags = copyTags != null ? copyTags : true;
    }
}