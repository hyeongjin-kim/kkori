package com.kkori.service;

import com.kkori.dto.question.request.CreateNewQuestionSetRequest;

public interface QuestionSetService {

    Long createNewQuestionSetWithQuestion(Long userId, CreateNewQuestionSetRequest request, String title);

}
