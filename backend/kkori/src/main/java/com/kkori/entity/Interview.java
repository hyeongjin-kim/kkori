package com.kkori.entity;

import com.kkori.common.BaseEntity;
import com.kkori.component.interview.RoomStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private User interviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewee_id")
    private User interviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_set_id")
    private QuestionSet usedQuestionSet;

    @Column(length = 50, nullable = false)
    private String roomId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private RoomStatus status;

    private LocalDateTime completedAt;

    @Builder
    public Interview(User interviewer, User interviewee, QuestionSet usedQuestionSet, String roomId) {
        this.interviewer = interviewer;
        this.interviewee = interviewee;
        this.usedQuestionSet = usedQuestionSet;
        this.roomId = roomId;
        this.status = RoomStatus.STARTED;
    }

    public void complete() {
        this.status = RoomStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return status.isCompleted();
    }
}