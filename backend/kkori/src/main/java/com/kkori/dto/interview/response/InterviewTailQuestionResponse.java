package com.kkori.dto.interview.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 면접 꼬리 질문 응답 DTO
 * 
 * 특징:
 * - 불변 객체로 설계 (Builder 패턴)
 * - 민감한 정보 필터링 적용
 * - API 응답 최적화
 * - 프론트엔드 친화적 필드명
 */
@Getter
@Builder
public class InterviewTailQuestionResponse {
    
    /**
     * 꼬리 질문 고유 ID
     */
    private final Long id;
    
    /**
     * AI가 생성한 꼬리 질문 내용
     */
    private final String content;
    
    /**
     * 사용자의 꼬리 질문 답변
     * null이면 아직 답변하지 않은 상태
     */
    private final String userAnswer;
    
    /**
     * AI 생성 시 참고한 원본 답변 내용
     * AI 품질 분석 및 컨텍스트 이해를 위해 포함
     */
    private final String originalUserAnswer;
    
    /**
     * 동일한 원본 질문에 대한 꼬리 질문 순서
     */
    private final Integer questionOrder;
    
    /**
     * AI 생성 컨텍스트 정보
     * 예: "GPT-4, temperature=0.7, based on confused answer"
     */
    private final String generationContext;
    
    /**
     * 참조한 원본 질문 ID
     */
    private final Long originalQuestionId;
    
    /**
     * 참조한 원본 질문 내용
     * 프론트엔드에서 컨텍스트 표시용
     */
    private final String originalQuestionContent;
    
    /**
     * 꼬리 질문 생성 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    
    /**
     * 답변 완료 여부 판단
     * 
     * @return 답변이 존재하고 비어있지 않으면 true
     */
    public boolean isAnswered() {
        return userAnswer != null && !userAnswer.trim().isEmpty();
    }
    
    /**
     * 답변 길이 반환
     * 
     * @return 답변이 있으면 길이, 없으면 0
     */
    public int getAnswerLength() {
        return userAnswer != null ? userAnswer.length() : 0;
    }
    
    /**
     * 답변 품질 점수 계산 (간단한 휴리스틱)
     * 
     * 계산 기준:
     * - 답변 길이 (최소 10자 이상)
     * - 키워드 포함 여부
     * - 문장 구조 (마침표 포함 여부)
     * 
     * @return 0-100 점수
     */
    public int getAnswerQualityScore() {
        if (!isAnswered()) {
            return 0;
        }
        
        int score = 0;
        String answer = userAnswer.trim();
        
        // 길이 점수 (최대 50점)
        if (answer.length() >= 10) score += 20;
        if (answer.length() >= 50) score += 15;
        if (answer.length() >= 100) score += 15;
        
        // 구조 점수 (최대 30점)
        if (answer.contains(".") || answer.contains("다") || answer.contains("습니다")) score += 15;
        if (answer.split("\\s+").length >= 5) score += 15; // 5단어 이상
        
        // 기술 키워드 점수 (최대 20점)
        String[] techKeywords = {"스프링", "데이터베이스", "API", "트랜잭션", "React", "JavaScript"};
        for (String keyword : techKeywords) {
            if (answer.contains(keyword)) {
                score += 5;
                break;
            }
        }
        
        return Math.min(score, 100);
    }
}