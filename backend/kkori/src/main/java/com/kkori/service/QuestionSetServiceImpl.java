package com.kkori.service;

import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class QuestionSetServiceImpl implements QuestionSetService {

    @Override
    public Long createNewQuestionSetWithQuestion(Long userId, CreateNewQuestionSetRequest request, String title) {
        return 0L;
    }
}
