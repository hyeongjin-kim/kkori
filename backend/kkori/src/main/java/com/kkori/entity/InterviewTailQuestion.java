package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "interview_tail_question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewTailQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_question_id", nullable = false)
    private Question originalQuestion;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "user_answer", length = 2000)
    private String userAnswer;

    @Column(name = "original_user_answer", nullable = false, length = 2000)
    private String originalUserAnswer;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "generation_context", length = 500)
    private String generationContext;

    @Builder
    public InterviewTailQuestion(Interview interview, Question originalQuestion, String content, 
                                String originalUserAnswer, Integer questionOrder, String generationContext) {
        this.interview = interview;
        this.originalQuestion = originalQuestion;
        this.content = content;
        this.originalUserAnswer = originalUserAnswer;
        this.questionOrder = questionOrder;
        this.generationContext = generationContext;
    }

    public void submitAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isAnswered() {
        return userAnswer != null && !userAnswer.trim().isEmpty();
    }

    public static InterviewTailQuestion createFromAI(Interview interview, Question originalQuestion,
                                                   String aiGeneratedContent, String originalUserAnswer,
                                                   Integer questionOrder, String generationContext) {
        return InterviewTailQuestion.builder()
                .interview(interview)
                .originalQuestion(originalQuestion)
                .content(aiGeneratedContent)
                .originalUserAnswer(originalUserAnswer)
                .questionOrder(questionOrder)
                .generationContext(generationContext)
                .build();
    }

}