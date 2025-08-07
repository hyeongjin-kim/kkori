package com.kkori.dto.question.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateQuestionSetResponse {

    private Long questionSetId;
    private String title;
    private String description;
    private Integer versionNumber;
    private Long parentVersionId;
    private Boolean isShared;
    private String ownerNickname;
    private List<QuestionMapResponse> questionMaps;
    private List<TailQuestionResponse> tailQuestions;
    private List<TagResponse> tags;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Builder
    public CreateQuestionSetResponse(Long questionSetId, String title, String description,
                                   Integer versionNumber, Long parentVersionId, Boolean isShared,
                                   String ownerNickname, List<QuestionMapResponse> questionMaps,
                                   List<TailQuestionResponse> tailQuestions, List<TagResponse> tags,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.questionSetId = questionSetId;
        this.title = title;
        this.description = description;
        this.versionNumber = versionNumber;
        this.parentVersionId = parentVersionId;
        this.isShared = isShared;
        this.ownerNickname = ownerNickname;
        this.questionMaps = questionMaps;
        this.tailQuestions = tailQuestions;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}