package com.kkori.dto.question.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionSetResponse {

    private Long id;
    private String title;
    private String description;
    private Integer versionNumber;
    private Long parentVersionId;
    private List<QuestionSummaryResponse> questions;
    private List<TagResponse> tags;
    private String message;

    @Builder
    public QuestionSetResponse(Long id, String title, String description,
                            Integer versionNumber, Long parentVersionId,
                            List<QuestionSummaryResponse> questions,
                            List<TagResponse> tags,
                            String message) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.versionNumber = versionNumber;
        this.parentVersionId = parentVersionId;
        this.questions = questions;
        this.tags = tags;
        this.message = message;
    }

}
