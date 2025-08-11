package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionVersionRequest {

    // 재사용하는 경우
    private Long questionId;
    private Long answerId;

    // 새로 생성하는 경우
    private String content;
    private Integer questionType;
    private String expectedAnswer;

    // 답변만 수정하는 경우
    private String newExpectedAnswer;

    @NotNull(message = "표시 순서는 필수입니다.")
    private Integer displayOrder;

    // 액션 타입 (REUSE, CREATE, MODIFY_ANSWER)
    private String action;

    @Builder
    public QuestionVersionRequest(Long questionId, Long answerId, String content, 
                                Integer questionType, String expectedAnswer, 
                                String newExpectedAnswer, Integer displayOrder, String action) {
        this.questionId = questionId;
        this.answerId = answerId;
        this.content = content;
        this.questionType = questionType;
        this.expectedAnswer = expectedAnswer;
        this.newExpectedAnswer = newExpectedAnswer;
        this.displayOrder = displayOrder;
        this.action = action;
    }

    public boolean isReuse() {
        return questionId != null && answerId != null;
    }

    public boolean isNewQuestion() {
        return content != null && questionType != null && expectedAnswer != null;
    }

    public boolean isModifyAnswer() {
        return questionId != null && newExpectedAnswer != null;
    }
}