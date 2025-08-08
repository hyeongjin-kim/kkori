package com.kkori.dto.question.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionDetailResponse {

    private Long id;
    private String content;
    private Integer questionType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Builder
    public QuestionDetailResponse(Long id, String content, Integer questionType, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.questionType = questionType;
        this.createdAt = createdAt;
    }
}