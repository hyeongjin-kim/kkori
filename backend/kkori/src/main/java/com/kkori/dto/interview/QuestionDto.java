package com.kkori.dto.interview;

import com.kkori.component.interview.QuestionForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private String questionType;
    private int questionId;
    private String questionText;
    
    /**
     * QuestionForm -> QuestionDto 생성
     */
    public static QuestionDto from(QuestionForm questionForm) {
        return new QuestionDto(
                questionForm.getQuestionType().name(),
                questionForm.getQuestionId(),
                questionForm.getQuestionText()
        );
    }
}
