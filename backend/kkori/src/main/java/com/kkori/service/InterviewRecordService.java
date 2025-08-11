package com.kkori.service;

import com.kkori.dto.interview.response.InterviewRecordDetailResponse;
import com.kkori.dto.interview.response.InterviewRecordListResponse;
import org.springframework.data.domain.Page;

public interface InterviewRecordService {
    
    /**
     * 사용자의 면접 기록 목록을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param role 역할 필터 ("interviewer", "interviewee", null=전체)
     * @return 면접 기록 목록
     */
    Page<InterviewRecordListResponse> getInterviewRecords(Long userId, int page, int size, String role);
    
    /**
     * 특정 면접의 상세 기록을 조회합니다.
     * 
     * @param userId 사용자 ID (권한 검증용)
     * @param interviewId 면접 ID
     * @return 면접 상세 기록
     */
    InterviewRecordDetailResponse getInterviewRecordDetail(Long userId, Long interviewId);
}