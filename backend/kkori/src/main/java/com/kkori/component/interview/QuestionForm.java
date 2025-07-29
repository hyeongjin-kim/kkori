package com.kkori.component.interview;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionForm {
    private QuestionType questionType;
    private int questionId;
    private String questionText;

    private QuestionType parentQuestionType;
    private int parentQuestionId;

    public QuestionForm(QuestionType questionType, int questionId, String questionText) {
        this.questionType = questionType;
        this.questionId = questionId;
        this.questionText = questionText;
        this.parentQuestionType = questionType;
        this.parentQuestionId = questionId;
    }

    public void setParentQuestion(QuestionForm parentQuestion) {
        this.parentQuestionType = parentQuestion.questionType;
        this.parentQuestionId = parentQuestion.questionId;
    }
}
