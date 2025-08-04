package com.kkori.service;

import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import jakarta.validation.Valid;

public interface QuestionSetService {
    Long createNewQuestionSetWithQuestion(Long userId, @Valid CreateNewQuestionSetRequest request, String title);
}
