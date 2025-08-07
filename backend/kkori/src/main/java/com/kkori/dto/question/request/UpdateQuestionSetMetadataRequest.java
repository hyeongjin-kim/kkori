package com.kkori.dto.question.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateQuestionSetMetadataRequest {

    private String title;
    private String description;
    private Boolean isShared;

    @Builder
    public UpdateQuestionSetMetadataRequest(String title, String description, Boolean isShared) {
        this.title = title;
        this.description = description;
        this.isShared = isShared;
    }
}