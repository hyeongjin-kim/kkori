package com.kkori.dto.interview.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자의 면접 기록 목록 조회 응답 DTO
 */
@Getter
@Builder
public class InterviewRecordListResponse {
    
    private Long interviewId;
    private String roomId;
    private String interviewerNickname;
    private String intervieweeNickname;
    private String questionSetTitle;
    private Integer totalQuestionCount;
    private LocalDateTime completedAt;
    
    /**
     * 현재 사용자가 면접관이었는지 면접자였는지 구분
     */
    private String userRole; // "INTERVIEWER" or "INTERVIEWEE"
}