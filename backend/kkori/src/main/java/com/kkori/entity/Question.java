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

    // 기본 질문 생성자
    private Question(String content, String expectedAnswer) {
        this.content = content;
        this.expectedAnswer = expectedAnswer;
        this.questionType = DEFAULT;
    }

    // 커스텀 질문 생성자
    private Question(String content) {
        this.content = content;
        this.questionType = CUSTOM;
    }

    // 꼬리 질문 생성자
    private Question(String content, Question parent) {
        this.content = content;
        this.questionType = TAIL;
        this.parent = parent;
    }

    // 정적 팩토리 메서드들
    public static Question createDefault(String content, String expectedAnswer) {
        return new Question(content, expectedAnswer);
    }

    public static Question createCustom(String content) {
        return new Question(content);
    }

    public static Question createTail(String content, Question parent) {
        return new Question(content, parent);
    }

}