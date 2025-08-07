package com.kkori.dto.interview.response;

import com.kkori.dto.interview.QuestionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewStartResponse {
    private QuestionDto firstQuestion;
}