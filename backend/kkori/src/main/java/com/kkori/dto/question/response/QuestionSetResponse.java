package com.kkori.dto.question.response;

import java.time.LocalDateTime;
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
    private Boolean isPublic;
    private String ownerNickname;
    private List<QuestionSummaryResponse> questions;
    private List<TagResponse> tags;
    private LocalDateTime createdAt;
    private String message;

    @Builder
    public QuestionSetResponse(Long id, String title, String description,
                            Integer versionNumber, Long parentVersionId,
                            Boolean isPublic, String ownerNickname,
                            List<QuestionSummaryResponse> questions,
                            List<TagResponse> tags,
                            LocalDateTime createdAt,
                            String message) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.versionNumber = versionNumber;
        this.parentVersionId = parentVersionId;
        this.isPublic = isPublic;
        this.ownerNickname = ownerNickname;
        this.questions = questions;
        this.tags = tags;
        this.createdAt = createdAt;
        this.message = message;
    }

}
