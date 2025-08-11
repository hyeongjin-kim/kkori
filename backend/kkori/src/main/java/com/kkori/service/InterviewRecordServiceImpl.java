package com.kkori.service;

import com.kkori.dto.interview.response.InterviewRecordDetailResponse;
import com.kkori.dto.interview.response.InterviewRecordListResponse;
import com.kkori.dto.interview.response.QuestionAnswerRecord;
import com.kkori.entity.Interview;
import com.kkori.entity.InterviewRecord;
import com.kkori.entity.Question;
import com.kkori.exception.interview.InterviewRecordException;
import com.kkori.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewRecordServiceImpl implements InterviewRecordService {
    
    private final InterviewRepository interviewRepository;
    
    @Override
    public Page<InterviewRecordListResponse> getInterviewRecords(Long userId, int page, int size, String role) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            
            Page<Interview> interviews = switch (role) {
                case "interviewer" -> interviewRepository.findByInterviewerIdAndCompleted(userId, pageable);
                case "interviewee" -> interviewRepository.findByIntervieweeIdAndCompleted(userId, pageable);
                case null -> interviewRepository.findByUserIdAndCompleted(userId, pageable);
                default -> interviewRepository.findByUserIdAndCompleted(userId, pageable);
            };
            
            return interviews.map(interview -> createListResponse(interview, userId));
        } catch (Exception e) {
            throw InterviewRecordException.listFetchFailed();
        }
    }
    
    @Override
    public InterviewRecordDetailResponse getInterviewRecordDetail(Long userId, Long interviewId) {
        try {
            Interview interview = interviewRepository.findByIdAndUserIdWithRecords(interviewId, userId)
                    .orElseThrow(InterviewRecordException::notFound);
            
            List<QuestionAnswerRecord> questionAnswers = interview.getInterviewRecords().stream()
                    .sorted(Comparator.comparing(InterviewRecord::getOrderNum))
                    .map(this::createQuestionAnswerRecord)
                    .collect(Collectors.toList());
            
            return createDetailResponse(interview, userId, questionAnswers);
        } catch (InterviewRecordException e) {
            throw e; // InterviewRecordException은 그대로 전달
        } catch (Exception e) {
            throw InterviewRecordException.detailFetchFailed();
        }
    }
    
    // ============== 헬퍼 메서드들 ==============
    
    private InterviewRecordListResponse createListResponse(Interview interview, Long userId) {
        return InterviewRecordListResponse.builder()
                .interviewId(interview.getInterviewId())
                .roomId(interview.getRoomId())
                .interviewerNickname(interview.getInterviewer().getNickname())
                .intervieweeNickname(interview.getInterviewee().getNickname())
                .questionSetTitle(interview.getUsedQuestionSet().getTitle())
                .totalQuestionCount(interview.getInterviewRecords().size())
                .completedAt(interview.getCompletedAt())
                .userRole(determineUserRole(interview, userId))
                .build();
    }
    
    private InterviewRecordDetailResponse createDetailResponse(Interview interview, Long userId, List<QuestionAnswerRecord> questionAnswers) {
        return InterviewRecordDetailResponse.builder()
                .interviewId(interview.getInterviewId())
                .roomId(interview.getRoomId())
                .interviewerNickname(interview.getInterviewer().getNickname())
                .intervieweeNickname(interview.getInterviewee().getNickname())
                .questionSetTitle(interview.getUsedQuestionSet().getTitle())
                .completedAt(interview.getCompletedAt())
                .userRole(determineUserRole(interview, userId))
                .questionAnswers(questionAnswers)
                .build();
    }
    
    private QuestionAnswerRecord createQuestionAnswerRecord(InterviewRecord record) {
        Question question = record.getQuestion();
        Question parentQuestion = question.getParent();
        
        return QuestionAnswerRecord.builder()
                .recordId(record.getRecordId())
                .orderNum(record.getOrderNum())
                .questionId(question.getId())
                .questionContent(question.getContent())
                .questionType(question.getQuestionType())
                .expectedAnswer(question.getExpectedAnswer())
                .parentQuestionId(parentQuestion != null ? parentQuestion.getId() : null)
                .parentQuestionContent(parentQuestion != null ? parentQuestion.getContent() : null)
                .answerId(record.getAnswer().getId())
                .answerContent(record.getAnswer().getContent())
                .answeredAt(record.getAnswer().getCreatedAt())
                .build();
    }
    
    private String determineUserRole(Interview interview, Long userId) {
        if (interview.getInterviewer().getUserId().equals(userId)) {
            return "INTERVIEWER";
        } else if (interview.getInterviewee().getUserId().equals(userId)) {
            return "INTERVIEWEE";
        } else {
            throw InterviewRecordException.accessDenied();
        }
    }
}