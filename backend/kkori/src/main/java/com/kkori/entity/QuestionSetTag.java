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
import jakarta.persistence.Index;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "question_set_tag",
    indexes = {
        @Index(name = "idx_questionset_tag_questionset", columnList = "question_set_id"),
        @Index(name = "idx_questionset_tag_tag", columnList = "tag_id"),
        @Index(name = "idx_questionset_tag_composite", columnList = "question_set_id, tag_id")
    })
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
