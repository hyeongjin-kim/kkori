package com.kkori.service;

import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;

public interface QuestionSetService {

    Long createQuestionSetWithInitialQuestions(Long userId, CreateNewQuestionSetRequest request, String title);

    Long createNewQuestionSet(Long userId, String title, String description);

    Long addQuestionToQuestionSet(Long userId, Long questionSetId, CreateQuestionRequest request);

}
