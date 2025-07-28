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
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 255)
    private String videoUrl;

    @Builder
    public Answer(Question question, User user, String content, String videoUrl) {
        this.question = question;
        this.user = user;
        this.content = content;
        this.videoUrl = videoUrl;
    }

    public boolean hasVideo() {
        return videoUrl != null && !videoUrl.trim().isEmpty();
    }

    public boolean hasContent() {
        return content != null && !content.trim().isEmpty();
    }
}