package com.kkori.dto.question.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionSetResponse {

    private Long id;
    private String title;
    private int versionNumber;

}
