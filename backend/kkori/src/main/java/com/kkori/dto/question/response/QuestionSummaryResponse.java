package com.kkori.dto.question.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionSummaryResponse {

    private Long id;
    private String content;
    private Integer questionType;
    private Integer displayOrder;

    @Builder
    public QuestionSummaryResponse(Long id, String content, Integer questionType, Integer displayOrder) {
        this.id = id;
        this.content = content;
        this.questionType = questionType;
        this.displayOrder = displayOrder;
    }

}
