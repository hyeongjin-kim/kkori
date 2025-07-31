package com.kkori.dto.interview.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextQuestionSelectRequest {
    private String roomId;
    private String questionType;
    private int questionId;
    private String questionText;
}