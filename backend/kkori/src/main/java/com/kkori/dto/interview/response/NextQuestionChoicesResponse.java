package com.kkori.dto.interview.response;

import com.kkori.dto.interview.QuestionDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextQuestionChoicesResponse {
    private List<QuestionDto> nextQuestionChoices;
}