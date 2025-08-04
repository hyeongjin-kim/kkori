package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionSetQuestionMap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_set_id")
    private QuestionSet questionSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    private Integer displayOrder;

    @Builder
    public QuestionSetQuestionMap(QuestionSet questionSet, Question question, int displayOrder) {
        this.questionSet = questionSet;
        this.question = question;
        this.displayOrder = displayOrder;
    }

    public static QuestionSetQuestionMap of(QuestionSet questionSet, Question question, int displayOrder) {
        return new QuestionSetQuestionMap(questionSet, question, displayOrder);
    }

}
