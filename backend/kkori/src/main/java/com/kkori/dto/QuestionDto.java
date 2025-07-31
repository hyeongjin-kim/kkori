package com.kkori.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDto {
    private String questionType;
    private String questionId;
    private String questionText;
    private String parentQuestionType;
    private String parentQuestionId;
}
