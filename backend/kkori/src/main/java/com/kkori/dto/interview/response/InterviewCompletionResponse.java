package com.kkori.dto.interview.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InterviewCompletionResponse {

    private Long interviewId;
    private String status;
    private LocalDateTime completedAt;
    private Integer totalQuestions;
    private Integer totalTailQuestions;
    private Integer answeredTailQuestions;
    private String interviewDuration;

    @Builder
    public InterviewCompletionResponse(Long interviewId, String status, LocalDateTime completedAt,
                                     Integer totalQuestions, Integer totalTailQuestions,
                                     Integer answeredTailQuestions, String interviewDuration) {
        this.interviewId = interviewId;
        this.status = status;
        this.completedAt = completedAt;
        this.totalQuestions = totalQuestions;
        this.totalTailQuestions = totalTailQuestions;
        this.answeredTailQuestions = answeredTailQuestions;
        this.interviewDuration = interviewDuration;
    }
}