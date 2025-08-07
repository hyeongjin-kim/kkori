package com.kkori.entity;

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

    private String expectedAnswer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TailQuestion> tailQuestions = new ArrayList<>();

    @Builder
    public Question(String content, String expectedAnswer, QuestionType questionType) {
        this.content = content;
        this.expectedAnswer = expectedAnswer;
        this.questionType = questionType;
    }

    public static Question of(String content, String expectedAnswer, QuestionType questionType) {
        return new Question(content, expectedAnswer, questionType);
    }

}