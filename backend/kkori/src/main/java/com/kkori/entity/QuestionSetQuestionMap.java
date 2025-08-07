package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_set_question_maps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionSetQuestionMap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "map_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Version
    @Column(name = "version")
    private Long version;

    @Builder
    public QuestionSetQuestionMap(QuestionSet questionSet, Question question, Answer answer, Integer displayOrder) {
        this.questionSet = questionSet;
        this.question = question;
        this.answer = answer;
        this.displayOrder = displayOrder;
    }

    public static QuestionSetQuestionMap create(QuestionSet questionSet, Question question, Answer answer, Integer displayOrder) {
        return QuestionSetQuestionMap.builder()
                .questionSet(questionSet)
                .question(question)
                .answer(answer)
                .displayOrder(displayOrder)
                .build();
    }

    public void updateDisplayOrder(Integer newOrder) {
        this.displayOrder = newOrder;
    }

    public QuestionSetQuestionMap updateAnswer(Answer newAnswer) {
        return QuestionSetQuestionMap.builder()
                .questionSet(this.questionSet)
                .question(this.question)
                .answer(newAnswer)
                .displayOrder(this.displayOrder)
                .build();
    }
}
