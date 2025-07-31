package com.kkori.dto.interview.response;

import com.kkori.dto.interview.QuestionDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmitResponse {
    private String transcribedText;
    private List<QuestionDto> nextQuestionChoices;
}