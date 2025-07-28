package com.kkori.entity;

import com.kkori.common.BaseEntity;
import com.kkori.component.interview.QuestionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Question parent;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private QuestionType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder(builderMethodName = "defaultBuilder")
    public Question(Long questionId, String content) {
        this.questionId = questionId;
        this.content = content;
        this.type = QuestionType.DEFAULT;
    }

    // 커스텀 질문 생성자
    @Builder(builderMethodName = "customBuilder")
    public Question(String content) {
        this.content = content;
        this.type = QuestionType.CUSTOM;
    }

    // 꼬리 질문 생성자
    @Builder(builderMethodName = "tailBuilder")
    public Question(String content, Question parent) {
        this.content = content;
        this.type = QuestionType.TAIL;
        this.parent = parent;
    }

    public boolean isTailQuestion() {
        return type == QuestionType.TAIL;
    }

    public boolean isCustomQuestion() {
        return type == QuestionType.CUSTOM;
    }
}