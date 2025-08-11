package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @Column(nullable = false)
    private Integer orderNum;

    @Builder
    public InterviewRecord(Interview interview, Question question, Answer answer, Integer orderNum) {
        this.interview = interview;
        this.question = question;
        this.answer = answer;
        this.orderNum = orderNum;
    }

    public boolean hasAnswer() {
        return answer != null;
    }
    
    public boolean isGeneralQuestion() {
        return question != null;
    }
}