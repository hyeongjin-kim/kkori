package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_set_question_maps",
    indexes = {
        @Index(name = "idx_questionset_question_map_set", columnList = "question_set_id"),
        @Index(name = "idx_questionset_question_map_question", columnList = "question_id"),
        @Index(name = "idx_questionset_question_map_display_order", columnList = "display_order"),
        @Index(name = "idx_questionset_question_map_set_order", columnList = "question_set_id, display_order")
    })
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


    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Version
    @Column(name = "version")
    private Long version;

    @Builder
    public QuestionSetQuestionMap(QuestionSet questionSet, Question question, Integer displayOrder) {
        this.questionSet = questionSet;
        this.question = question;
        this.displayOrder = displayOrder;
    }

    public static QuestionSetQuestionMap create(QuestionSet questionSet, Question question, Integer displayOrder) {
        return QuestionSetQuestionMap.builder()
                .questionSet(questionSet)
                .question(question)
                .displayOrder(displayOrder)
                .build();
    }

    public void updateDisplayOrder(Integer newOrder) {
        this.displayOrder = newOrder;
    }
}
