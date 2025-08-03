package com.kkori.dto.interview.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomQuestionCreateRequest {
    private String roomId;
    private String audioBase64;
}