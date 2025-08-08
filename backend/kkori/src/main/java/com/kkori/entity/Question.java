package com.kkori.entity;

import static com.kkori.entity.QuestionType.*;

import com.kkori.common.BaseEntity;
import com.kkori.common.jpa.converter.QuestionTypeConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Convert(converter = QuestionTypeConverter.class)
    private QuestionType questionType;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Question parent; // nullable, 상위 질문

    private String expectedAnswer;

    @Builder(builderMethodName = "defaultBuilder")
    public Question(String content, String expectedAnswer) {
        this.content = content;
        this.expectedAnswer = expectedAnswer;
        this.questionType = DEFAULT;
    }

    @Builder(builderMethodName = "customBuilder")
    public Question(String content) {
        this.content = content;
        this.questionType = CUSTOM;
    }

    @Builder(builderMethodName = "tailBuilder")
    public Question(String content, Question parent) {
        this.content = content;
        this.questionType = TAIL;
        this.parent = parent;
    }

}