package com.kkori.dto.interview.response;

import com.kkori.entity.QuestionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 면접 중 주고받은 질문-답변 기록 DTO
 */
@Getter
@Builder
public class QuestionAnswerRecord {
    private Long recordId;
    private Integer orderNum;
    private Long questionId;
    private String questionContent;
    private QuestionType questionType;
    private String expectedAnswer; // DEFAULT 질문인 경우에만
    private Long parentQuestionId; // TAIL 질문인 경우 부모 질문 ID
    private String parentQuestionContent; // TAIL 질문인 경우 부모 질문 내용
    private Long answerId;
    private String answerContent;
    private LocalDateTime answeredAt;
}