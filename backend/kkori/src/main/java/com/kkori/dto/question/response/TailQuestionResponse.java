package com.kkori.dto.question.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TailQuestionResponse {

    private Long id;
    private String content;
    private Long questionId;
    private String createdBy;
    private String userAnswer;
    private Integer displayOrder;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime answeredAt;

    @Builder
    public TailQuestionResponse(Long id, String content, Long questionId, String createdBy,
                              String userAnswer, Integer displayOrder, LocalDateTime createdAt,
                              LocalDateTime answeredAt) {
        this.id = id;
        this.content = content;
        this.questionId = questionId;
        this.createdBy = createdBy;
        this.userAnswer = userAnswer;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.answeredAt = answeredAt;
    }
}