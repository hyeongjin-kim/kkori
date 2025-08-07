package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tail_questions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TailQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tail_question_id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Lob
    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Version
    @Column(name = "version")
    private Long version;

    @Builder
    public TailQuestion(String content, Question question, User createdBy, Integer displayOrder) {
        this.content = content;
        this.question = question;
        this.createdBy = createdBy;
        this.displayOrder = displayOrder;
    }

    public static TailQuestion create(String content, Question question, User createdBy, Integer displayOrder) {
        return TailQuestion.builder()
                .content(content)
                .question(question)
                .createdBy(createdBy)
                .displayOrder(displayOrder)
                .build();
    }

    public void submitAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
        this.answeredAt = LocalDateTime.now();
    }

    public boolean isAnswered() {
        return userAnswer != null && !userAnswer.trim().isEmpty();
    }
}
