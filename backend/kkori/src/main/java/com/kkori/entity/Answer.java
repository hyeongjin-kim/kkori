package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "answers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Version
    @Column(name = "version")
    private Long version;

    @Builder
    public Answer(String content, User createdBy) {
        this.content = content;
        this.createdBy = createdBy;
    }

    // 불변 객체
    public static Answer create(String content, User createdBy) {
        return Answer.builder()
                .content(content)
                .createdBy(createdBy)
                .build();
    }
}