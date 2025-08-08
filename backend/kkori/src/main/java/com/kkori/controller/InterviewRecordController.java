package com.kkori.controller;

import com.kkori.annotation.LoginUser;
import com.kkori.common.CommonApiResponse;
import com.kkori.dto.interview.response.InterviewRecordDetailResponse;
import com.kkori.dto.interview.response.InterviewRecordListResponse;
import com.kkori.service.InterviewRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 면접 기록 조회 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview-records")
public class InterviewRecordController {
    
    private final InterviewRecordService interviewRecordService;
    
    /**
     * 사용자의 면접 기록 목록을 조회합니다.
     * 
     * @param userId 인증된 사용자 ID
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @param role 역할 필터 ("interviewer", "interviewee", 전체=필터 없음)
     * @return 면접 기록 목록
     */
    @GetMapping
    public ResponseEntity<CommonApiResponse<Page<InterviewRecordListResponse>>> getInterviewRecords(
            @LoginUser Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role) {
        
        Page<InterviewRecordListResponse> records = interviewRecordService.getInterviewRecords(userId, page, size, role);
        return ResponseEntity.ok(CommonApiResponse.ok(records, "면접 기록 목록 조회가 완료되었습니다."));
    }
    
    /**
     * 특정 면접의 상세 기록을 조회합니다.
     * 
     * @param userId 인증된 사용자 ID
     * @param interviewId 면접 ID
     * @return 면접 상세 기록
     */
    @GetMapping("/{interviewId}")
    public ResponseEntity<CommonApiResponse<InterviewRecordDetailResponse>> getInterviewRecordDetail(
            @LoginUser Long userId,
            @PathVariable Long interviewId) {
        
        InterviewRecordDetailResponse detail = interviewRecordService.getInterviewRecordDetail(userId, interviewId);
        return ResponseEntity.ok(CommonApiResponse.ok(detail, "면접 상세 기록 조회가 완료되었습니다."));
    }
}