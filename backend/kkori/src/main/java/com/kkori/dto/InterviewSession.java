package com.kkori.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InterviewSession {

    private int newQuestionId = 1;

    private QuestionForm currentQuestion;
    private QuestionForm parentQuestion;

    private List<QuestionForm> defaultQuestions;

    private Map<QuestionForm, String> questionAnswer = new LinkedHashMap<>();

    public InterviewSession(List<QuestionForm> defaultQuestions) {
        this.defaultQuestions = defaultQuestions;
        this.currentQuestion = defaultQuestions.removeFirst();
        this.parentQuestion = this.currentQuestion;
    }

    public List<QuestionForm> getNextQuestions(List<String> tailQuestions) {
        List<QuestionForm> nextQuestions = generateTailQuestions(tailQuestions);
        if (!defaultQuestions.isEmpty()) {
            nextQuestions.add(defaultQuestions.getFirst());
        }

        return nextQuestions;
    }

    public QuestionForm selectQuestion(QuestionType questionType, int questionId, String questionText) {
        QuestionForm questionForm = new QuestionForm(questionType, questionId, questionText);

        setParentQuestion(questionForm);
        setCurrentQuestion(questionForm);

        return currentQuestion;
    }

    private void setParentQuestion(QuestionForm questionForm) {
        if (questionForm.getQuestionType() == QuestionType.TAIL) {
            return;
        }

        checkAndRemoveDefaultQuestion(questionForm);
        parentQuestion = questionForm;
    }

    private void setCurrentQuestion(QuestionForm questionForm) {
        currentQuestion = questionForm;
        if (questionForm.getQuestionType() == QuestionType.TAIL) {
            currentQuestion.setParentQuestion(parentQuestion);
        }
    }

    private void checkAndRemoveDefaultQuestion(QuestionForm questionForm) {
        if (questionForm.getQuestionType() == QuestionType.DEFAULT && !defaultQuestions.isEmpty()) {
            defaultQuestions.removeFirst();
        }
    }

    public QuestionForm createCustomQuestion(String customQuestionText) {
        QuestionForm newCustomQuestion = selectQuestion(QuestionType.CUSTOM, newQuestionId++, customQuestionText);
        return newCustomQuestion;
    }

    public void saveAnswer(String answerText) {
        questionAnswer.put(currentQuestion, answerText);
    }


    private List<QuestionForm> generateTailQuestions(List<String> tailQuestions) {
        List<QuestionForm> newTailQuestions = new ArrayList<>();

        newTailQuestions.add(new QuestionForm(QuestionType.TAIL, newQuestionId++, tailQuestions.getFirst()));
        newTailQuestions.add(new QuestionForm(QuestionType.TAIL, newQuestionId++, tailQuestions.get(1)));

        return newTailQuestions;
    }
}