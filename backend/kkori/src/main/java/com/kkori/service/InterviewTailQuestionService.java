package com.kkori.service;

import com.kkori.dto.interview.response.InterviewTailQuestionResponse;
import com.kkori.dto.interview.response.SubmitTailQuestionAnswerResponse;
import com.kkori.entity.InterviewTailQuestion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 면접 꼬리 질문 서비스
 * 
 * 주요 기능:
 * - 세션별 꼬리 질문 조회 및 관리
 * - AI 생성 꼬리 질문 품질 분석
 * - 사용자 학습 데이터 추적
 * - 면접 성과 통계 제공
 * 
 * 보안 고려사항:
 * - 면접 참여자만 해당 세션의 꼬리 질문 접근 가능
 * - 민감한 개인 학습 데이터 보호
 * - 권한 기반 데이터 접근 제어
 */
public interface InterviewTailQuestionService {

    // ===============================================
    // 기본 조회 기능
    // ===============================================

    /**
     * 특정 면접 세션의 모든 꼬리 질문 조회
     * - 권한 검증: 면접 참여자(면접관/면접자)만 접근 가능
     * - N+1 방지를 위한 최적화된 조회
     */
    List<InterviewTailQuestionResponse> getInterviewTailQuestions(Long userId, Long interviewId);

    /**
     * 특정 원본 질문에 대한 꼬리 질문들 조회
     * - 면접 세션 내에서 특정 질문의 심화 과정 추적
     */
    List<InterviewTailQuestionResponse> getTailQuestionsByOriginalQuestion(
            Long userId, Long interviewId, Long originalQuestionId);

    /**
     * 답변 대기 중인 꼬리 질문들 조회
     * - 진행 중인 면접에서 다음 질문 결정을 위해 사용
     */
    List<InterviewTailQuestionResponse> getUnansweredTailQuestions(Long userId, Long interviewId);

    // ===============================================
    // 데이터 분석 및 통계
    // ===============================================

    /**
     * 사용자의 최근 학습 데이터 조회
     * - 개인화된 학습 추천을 위한 데이터
     * - 성장 패턴 분석
     */
    List<InterviewTailQuestionResponse> getRecentUserLearningData(Long userId, LocalDateTime fromDate);

    // ===============================================
    // 답변 제출 기능
    // ===============================================

    /**
     * 꼬리질문 답변 제출
     * - 답변 저장 및 다음 질문 정보 반환
     */
    SubmitTailQuestionAnswerResponse submitTailQuestionAnswer(Long tailQuestionId, String userAnswer);

    /**
     * 꼬리질문 ID로 조회
     */
    InterviewTailQuestion getTailQuestionById(Long tailQuestionId);

    /**
     * 다음 꼬리질문 조회 (같은 원본질문에서)
     */
    InterviewTailQuestion getNextUnansweredTailQuestion(Long originalQuestionId, Integer currentQuestionOrder);
}