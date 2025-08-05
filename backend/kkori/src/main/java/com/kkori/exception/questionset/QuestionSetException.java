package com.kkori.exception.questionset;

import com.kkori.exception.CustomRuntimeException;
import com.kkori.exception.ExceptionCode;

public class QuestionSetException extends CustomRuntimeException {

    public QuestionSetException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static QuestionSetException emptyQuestions() {
        return new QuestionSetException(ExceptionCode.EMPTY_QUESTIONS);
    }

    public static QuestionSetException noTitle() {
        return new QuestionSetException(ExceptionCode.NO_TITLE);
    }

    public static QuestionSetException blankTitle() {
        return new QuestionSetException(ExceptionCode.BLANK_TITLE);
    }

    public static QuestionSetException noQuestionContent() {
        return new QuestionSetException(ExceptionCode.NO_QUESTION_CONTENT);
    }

    public static QuestionSetException noQuestionType() {
        return new QuestionSetException(ExceptionCode.NO_QUESTION_TYPE);
    }

    public static QuestionSetException unauthorized() {
        return new QuestionSetException(ExceptionCode.NOT_LOGGED_IN);
    }

    public static QuestionSetException internalError() {
        return new QuestionSetException(ExceptionCode.QUESTION_SET_INTERNAL_ERROR);
    }

}
