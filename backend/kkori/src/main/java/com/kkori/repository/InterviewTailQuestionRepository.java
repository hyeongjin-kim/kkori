package com.kkori.repository;

import com.kkori.entity.InterviewTailQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 면접 세션별 꼬리 질문 Repository
 * 주요 기능:
 * - 세션별 꼬리 질문 조회
 * - 원본 질문별 꼬리 질문 조회  
 * - 답변 완료 상태별 필터링
 * - 성능 최적화를 위한 Fetch Join 쿼리
 */
public interface InterviewTailQuestionRepository extends JpaRepository<InterviewTailQuestion, Long> {

    /**
     * 특정 면접 세션의 모든 꼬리 질문 조회 (성능 최적화)
     * Fetch Join으로 N+1 문제 방지
     */
    @Query("""
        SELECT itq FROM InterviewTailQuestion itq
        LEFT JOIN FETCH itq.interview i
        LEFT JOIN FETCH itq.originalQuestion oq
        WHERE itq.interview.interviewId = :interviewId
        ORDER BY itq.originalQuestion.id, itq.questionOrder
        """)
    List<InterviewTailQuestion> findByInterviewIdWithDetails(@Param("interviewId") Long interviewId);

    /**
     * 특정 면접 세션에서 특정 원본 질문에 대한 꼬리 질문들 조회
     */
    @Query("""
        SELECT itq FROM InterviewTailQuestion itq
        WHERE itq.interview.interviewId = :interviewId 
        AND itq.originalQuestion.id = :originalQuestionId
        ORDER BY itq.questionOrder
        """)
    List<InterviewTailQuestion> findByInterviewIdAndOriginalQuestionId(
            @Param("interviewId") Long interviewId, 
            @Param("originalQuestionId") Long originalQuestionId);

    /**
     * 답변 완료되지 않은 꼬리 질문들 조회
     */
    @Query("""
        SELECT itq FROM InterviewTailQuestion itq
        WHERE itq.interview.interviewId = :interviewId 
        AND (itq.userAnswer IS NULL OR itq.userAnswer = '')
        ORDER BY itq.createdAt
        """)
    List<InterviewTailQuestion> findUnansweredByInterviewId(@Param("interviewId") Long interviewId);

    /**
     * 특정 원본 질문에 대한 모든 꼬리 질문 통계 (데이터 분석용)
     */
    @Query("""
        SELECT itq.content, COUNT(itq.id) as usage_count
        FROM InterviewTailQuestion itq
        WHERE itq.originalQuestion.id = :originalQuestionId
        GROUP BY itq.content
        ORDER BY usage_count DESC
        """)
    List<Object[]> findTailQuestionStatsByOriginalQuestion(@Param("originalQuestionId") Long originalQuestionId);

    /**
     * 면접 세션별 꼬리 질문 생성 통계
     */
    @Query("""
        SELECT i.interviewId, COUNT(itq.id) as tail_question_count,
               AVG(CASE WHEN itq.userAnswer IS NOT NULL THEN LENGTH(itq.userAnswer) END) as avg_answer_length
        FROM InterviewTailQuestion itq
        RIGHT JOIN itq.interview i
        WHERE i.interviewId = :interviewId
        GROUP BY i.interviewId
        """)
    List<Object[]> findInterviewTailQuestionStats(@Param("interviewId") Long interviewId);

    /**
     * 특정 사용자의 최근 면접에서 생성된 꼬리 질문들 (학습 데이터 분석용)
     */
    @Query("""
        SELECT itq FROM InterviewTailQuestion itq
        JOIN itq.interview i
        WHERE i.interviewee.userId = :userId
        AND i.completedAt >= :fromDate
        ORDER BY i.completedAt DESC, itq.questionOrder
        """)
    List<InterviewTailQuestion> findRecentTailQuestionsByUser(
            @Param("userId") Long userId, 
            @Param("fromDate") LocalDateTime fromDate);

    /**
     * 특정 원본 질문에서 현재 순서 이후의 첫 번째 미답변 꼬리질문 조회
     */
    @Query("""
        SELECT itq FROM InterviewTailQuestion itq
        WHERE itq.originalQuestion.id = :originalQuestionId 
        AND itq.questionOrder > :currentOrder
        AND (itq.userAnswer IS NULL OR itq.userAnswer = '')
        ORDER BY itq.questionOrder
        LIMIT 1
        """)
    Optional<InterviewTailQuestion> findNextUnansweredByOriginalQuestionAndOrder(
            @Param("originalQuestionId") Long originalQuestionId, 
            @Param("currentOrder") Integer currentOrder);
}