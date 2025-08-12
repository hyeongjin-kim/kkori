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
    private Boolean isPublic;

    @Builder
    public UpdateQuestionSetMetadataRequest(String title, String description, Boolean isPublic) {
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
    }
}