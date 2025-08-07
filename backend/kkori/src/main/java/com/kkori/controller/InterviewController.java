package com.kkori.controller;

import com.kkori.annotation.LoginUser;
import com.kkori.common.CommonApiResponse;
import com.kkori.dto.interview.request.SubmitTailQuestionAnswerRequest;
import com.kkori.dto.interview.response.InterviewTailQuestionResponse;
import com.kkori.dto.interview.response.SubmitTailQuestionAnswerResponse;
import com.kkori.service.InterviewTailQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewTailQuestionService interviewTailQuestionService;

    @PostMapping("/tail-questions/{tailQuestionId}/answers")
    public ResponseEntity<CommonApiResponse<SubmitTailQuestionAnswerResponse>> submitTailQuestionAnswer(
            @PathVariable Long tailQuestionId,
            @RequestBody @Valid SubmitTailQuestionAnswerRequest request
    ) {
        SubmitTailQuestionAnswerResponse response = interviewTailQuestionService
            .submitTailQuestionAnswer(request.getTailQuestionId(), request.getUserAnswer());

        return ResponseEntity.ok(CommonApiResponse.ok(response, "꼬리 질문 답변이 성공적으로 제출되었습니다."));
    }

    @GetMapping("/{interviewId}/tail-questions")
    public ResponseEntity<CommonApiResponse<List<InterviewTailQuestionResponse>>> getInterviewTailQuestions(
            @LoginUser Long userId,
            @PathVariable Long interviewId
    ) {
        List<InterviewTailQuestionResponse> responses = interviewTailQuestionService
            .getInterviewTailQuestions(userId, interviewId);

        return ResponseEntity.ok(CommonApiResponse.ok(responses, "면접 꼬리질문 목록 조회가 완료되었습니다."));
    }

    @GetMapping("/{interviewId}/tail-questions/unanswered")
    public ResponseEntity<CommonApiResponse<List<InterviewTailQuestionResponse>>> getUnansweredTailQuestions(
            @LoginUser Long userId,
            @PathVariable Long interviewId
    ) {
        List<InterviewTailQuestionResponse> responses = interviewTailQuestionService
            .getUnansweredTailQuestions(userId, interviewId);

        return ResponseEntity.ok(CommonApiResponse.ok(responses, "미답변 꼬리질문 목록 조회가 완료되었습니다."));
    }

    @GetMapping("/{interviewId}/questions/{originalQuestionId}/tail-questions")
    public ResponseEntity<CommonApiResponse<List<InterviewTailQuestionResponse>>> getTailQuestionsByOriginalQuestion(
            @LoginUser Long userId,
            @PathVariable Long interviewId,
            @PathVariable Long originalQuestionId
    ) {
        List<InterviewTailQuestionResponse> responses = interviewTailQuestionService
            .getTailQuestionsByOriginalQuestion(userId, interviewId, originalQuestionId);

        return ResponseEntity.ok(CommonApiResponse.ok(responses, "원본 질문별 꼬리질문 목록 조회가 완료되었습니다."));
    }

    @GetMapping("/my/tail-questions/recent")
    public ResponseEntity<CommonApiResponse<List<InterviewTailQuestionResponse>>> getRecentLearningData(
            @LoginUser Long userId,
            @RequestParam(required = false) LocalDateTime fromDate
    ) {
        LocalDateTime searchFrom = fromDate != null ? fromDate : LocalDateTime.now().minusDays(30);
        
        List<InterviewTailQuestionResponse> responses = interviewTailQuestionService
            .getRecentUserLearningData(userId, searchFrom);

        return ResponseEntity.ok(CommonApiResponse.ok(responses, "최근 학습 데이터 조회가 완료되었습니다."));
    }
}