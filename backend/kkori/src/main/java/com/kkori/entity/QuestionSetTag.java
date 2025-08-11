package com.kkori.entity;

import com.kkori.common.BaseEntity;
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
@Table(name = "question_set_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionSetTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_set_id")
    private QuestionSet questionSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public QuestionSetTag(QuestionSet questionSet, Tag tag) {
        this.questionSet = questionSet;
        this.tag = tag;
    }

    public static QuestionSetTag of(QuestionSet questionSet, Tag tag) {
        QuestionSetTag questionSetTag = new QuestionSetTag();
        questionSetTag.questionSet = questionSet;
        questionSetTag.tag = tag;
        return questionSetTag;
    }

}
