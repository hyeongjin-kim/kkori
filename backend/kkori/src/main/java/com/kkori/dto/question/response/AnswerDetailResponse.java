package com.kkori.dto.question.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerDetailResponse {

    private Long id;
    private String content;
    private String createdByNickname;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Builder
    public AnswerDetailResponse(Long id, String content, String createdByNickname, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdByNickname = createdByNickname;
        this.createdAt = createdAt;
    }
}