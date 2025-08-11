package com.kkori.dto.interview.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SubmitTailQuestionAnswerResponse {

    private Long tailQuestionId;
    private Boolean isAnswered;
    private NextTailQuestionInfo nextTailQuestion;

    @Builder
    public SubmitTailQuestionAnswerResponse(Long tailQuestionId, Boolean isAnswered, NextTailQuestionInfo nextTailQuestion) {
        this.tailQuestionId = tailQuestionId;
        this.isAnswered = isAnswered;
        this.nextTailQuestion = nextTailQuestion;
    }

    @Getter
    public static class NextTailQuestionInfo {
        private Long id;
        private String content;
        private Long originalQuestionId;
        private Integer questionOrder;

        @Builder
        public NextTailQuestionInfo(Long id, String content, Long originalQuestionId, Integer questionOrder) {
            this.id = id;
            this.content = content;
            this.originalQuestionId = originalQuestionId;
            this.questionOrder = questionOrder;
        }
    }
}