package com.kkori.service;

import com.kkori.dto.interview.response.InterviewTailQuestionResponse;
import com.kkori.dto.interview.response.SubmitTailQuestionAnswerResponse;
import com.kkori.entity.Interview;
import com.kkori.entity.InterviewTailQuestion;
import com.kkori.exception.interview.InterviewSessionException;
import com.kkori.exception.interview.TailQuestionException;
import com.kkori.exception.user.UserException;
import com.kkori.repository.InterviewRepository;
import com.kkori.repository.InterviewTailQuestionRepository;
import com.kkori.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 면접 꼬리 질문 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewTailQuestionServiceImpl implements InterviewTailQuestionService {

    private final InterviewTailQuestionRepository interviewTailQuestionRepository;
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<InterviewTailQuestionResponse> getInterviewTailQuestions(Long userId, Long interviewId) {
        log.debug("Fetching tail questions for interview: {} by user: {}", interviewId, userId);
        
        // 권한 검증
        validateInterviewAccess(userId, interviewId);
        
        // N+1 방지 최적화 쿼리로 조회
        List<InterviewTailQuestion> tailQuestions = interviewTailQuestionRepository
                .findByInterviewIdWithDetails(interviewId);
        
        log.debug("Found {} tail questions for interview: {}", tailQuestions.size(), interviewId);
        
        return tailQuestions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewTailQuestionResponse> getTailQuestionsByOriginalQuestion(
            Long userId, Long interviewId, Long originalQuestionId) {
        
        log.debug("Fetching tail questions for interview: {} original question: {} by user: {}", 
                 interviewId, originalQuestionId, userId);
        
        // 권한 검증
        validateInterviewAccess(userId, interviewId);
        
        List<InterviewTailQuestion> tailQuestions = interviewTailQuestionRepository
                .findByInterviewIdAndOriginalQuestionId(interviewId, originalQuestionId);
        
        return tailQuestions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewTailQuestionResponse> getUnansweredTailQuestions(Long userId, Long interviewId) {
        log.debug("Fetching unanswered tail questions for interview: {} by user: {}", interviewId, userId);
        
        // 권한 검증
        validateInterviewAccess(userId, interviewId);
        
        List<InterviewTailQuestion> unansweredQuestions = interviewTailQuestionRepository
                .findUnansweredByInterviewId(interviewId);
        
        log.debug("Found {} unanswered tail questions for interview: {}", unansweredQuestions.size(), interviewId);
        
        return unansweredQuestions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewTailQuestionResponse> getRecentUserLearningData(Long userId, LocalDateTime fromDate) {
        log.debug("Fetching recent learning data for user: {} from: {}", userId, fromDate);
        
        // 사용자 존재 검증
        validateUserExists(userId);
        
        List<InterviewTailQuestion> recentQuestions = interviewTailQuestionRepository
                .findRecentTailQuestionsByUser(userId, fromDate);
        
        log.debug("Found {} recent tail questions for user: {}", recentQuestions.size(), userId);
        
        return recentQuestions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 면접 접근 권한 검증 및 면접 엔티티 반환
     * - 면접 참여자(면접관 또는 면접자)만 접근 가능
     * - 보안: 다른 사용자의 면접 데이터 접근 차단
     */
    private Interview validateInterviewAccess(Long userId, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> {
                    log.warn("Interview not found: {}", interviewId);
                    return InterviewSessionException.interviewNotFound();
                });
        
        boolean isInterviewer = interview.getInterviewer().getUserId().equals(userId);
        boolean isInterviewee = interview.getInterviewee().getUserId().equals(userId);
        
        if (!isInterviewer && !isInterviewee) {
            log.warn("Unauthorized access attempt to interview: {} by user: {}", interviewId, userId);
            throw InterviewSessionException.noPermission();
        }
        
        log.debug("Access granted to interview: {} for user: {} (role: {})", 
                 interviewId, userId, isInterviewer ? "INTERVIEWER" : "INTERVIEWEE");
                 
        return interview;
    }

    /**
     * 사용자 존재 검증
     */
    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User not found: {}", userId);
            throw UserException.userNotFound();
        }
    }

    @Override
    @Transactional(
        isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED,
        rollbackFor = Exception.class,
        timeout = 30
    )
    public SubmitTailQuestionAnswerResponse submitTailQuestionAnswer(Long tailQuestionId, String userAnswer) {
        log.info("TRANSACTION_START: Submitting answer for tail question: {}", tailQuestionId);

        try {
            // 1. 꼬리질문 조회 및 검증
            InterviewTailQuestion tailQuestion = getTailQuestionById(tailQuestionId);
            
            // 2. 면접 완료 상태 검증
            Interview interview = tailQuestion.getInterview();
            if (interview.isFinished()) {
                log.warn("Attempt to answer tail question in completed interview: {}", interview.getInterviewId());
                throw TailQuestionException.interviewAlreadyCompleted();
            }

            // 3. 이미 답변한 질문인지 검증
            if (tailQuestion.isAnswered()) {
                log.warn("Attempt to re-answer tail question: {}", tailQuestionId);
                throw TailQuestionException.tailQuestionAlreadyAnswered();
            }
            
            // 4. 답변 저장 (JPA Dirty Checking 활용)
            tailQuestion.submitAnswer(userAnswer);
            log.debug("Answer saved for tail question: {}", tailQuestionId);

            // 5. 다음 꼬리질문 조회 (트랜잭션 내에서 즉시 반영된 데이터로 조회)
            InterviewTailQuestion nextTailQuestion = getNextUnansweredTailQuestion(
                tailQuestion.getOriginalQuestion().getId(), 
                tailQuestion.getQuestionOrder()
            );

            // 6. 응답 구성
            SubmitTailQuestionAnswerResponse.NextTailQuestionInfo nextQuestionInfo = null;
            if (nextTailQuestion != null) {
                nextQuestionInfo = SubmitTailQuestionAnswerResponse.NextTailQuestionInfo.builder()
                    .id(nextTailQuestion.getId())
                    .content(nextTailQuestion.getContent())
                    .originalQuestionId(nextTailQuestion.getOriginalQuestion().getId())
                    .questionOrder(nextTailQuestion.getQuestionOrder())
                    .build();
                
                log.debug("Next unanswered tail question found: {}", nextTailQuestion.getId());
            } else {
                log.debug("No more unanswered tail questions for original question: {}", 
                         tailQuestion.getOriginalQuestion().getId());
            }

            SubmitTailQuestionAnswerResponse response = SubmitTailQuestionAnswerResponse.builder()
                .tailQuestionId(tailQuestionId)
                .isAnswered(true)
                .nextTailQuestion(nextQuestionInfo)
                .build();

            log.info("TRANSACTION_SUCCESS: Answer submitted successfully for tail question: {}", tailQuestionId);
            return response;

        } catch (Exception e) {
            log.error("TRANSACTION_ERROR: Failed to submit answer for tail question: {}", tailQuestionId, e);
            throw e; // 트랜잭션 롤백 발생
        }
    }

    @Override
    public InterviewTailQuestion getTailQuestionById(Long tailQuestionId) {
        return interviewTailQuestionRepository.findById(tailQuestionId)
            .orElseThrow(() -> {
                log.warn("Tail question not found: {}", tailQuestionId);
                return TailQuestionException.tailQuestionNotFound();
            });
    }

    @Override
    public InterviewTailQuestion getNextUnansweredTailQuestion(Long originalQuestionId, Integer currentQuestionOrder) {
        return interviewTailQuestionRepository
            .findNextUnansweredByOriginalQuestionAndOrder(originalQuestionId, currentQuestionOrder)
            .orElse(null);
    }

    /**
     * InterviewTailQuestion → InterviewTailQuestionResponse 변환
     * - 민감한 정보 필터링 (예: 다른 사용자의 개인정보)
     * - 필요한 정보만 선별적 노출
     */
    private InterviewTailQuestionResponse convertToResponse(InterviewTailQuestion tailQuestion) {
        return InterviewTailQuestionResponse.builder()
                .id(tailQuestion.getId())
                .content(tailQuestion.getContent())
                .userAnswer(tailQuestion.getUserAnswer())
                .originalUserAnswer(tailQuestion.getOriginalUserAnswer())
                .questionOrder(tailQuestion.getQuestionOrder())
                .generationContext(tailQuestion.getGenerationContext())
                .originalQuestionId(tailQuestion.getOriginalQuestion().getId())
                .originalQuestionContent(tailQuestion.getOriginalQuestion().getContent())
                .createdAt(tailQuestion.getCreatedAt())
                .build();
    }
}