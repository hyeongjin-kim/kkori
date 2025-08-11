package com.kkori.dto.question.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionMapResponse {

    private Long mapId;
    private Long questionId;
    private Long answerId;
    private Integer displayOrder;
    private QuestionDetailResponse question;
    private AnswerDetailResponse answer;

    @Builder
    public QuestionMapResponse(Long mapId, Long questionId, Long answerId, 
                             Integer displayOrder, QuestionDetailResponse question, 
                             AnswerDetailResponse answer) {
        this.mapId = mapId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.displayOrder = displayOrder;
        this.question = question;
        this.answer = answer;
    }
}