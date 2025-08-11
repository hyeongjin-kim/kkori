package com.kkori.dto.interview.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 면접 기록 상세 조회 응답 DTO
 */
@Getter
@Builder
public class InterviewRecordDetailResponse {
    
    private Long interviewId;
    private String roomId;
    private String interviewerNickname;
    private String intervieweeNickname;
    private String questionSetTitle;
    private LocalDateTime completedAt;
    private String userRole; // "INTERVIEWER" or "INTERVIEWEE"
    
    /**
     * 면접 중 주고받은 모든 질문-답변 기록
     */
    private List<QuestionAnswerRecord> questionAnswers;
}