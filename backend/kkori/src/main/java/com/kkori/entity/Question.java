package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "set_id")
    private QuestionSet questionSet;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Question parent; // nullable, 상위 질문

    @Column(length = 20, nullable = false)
    private String type; // SET, DIRECT, FOLLOWUP 등

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Integer sortOrder;

}
