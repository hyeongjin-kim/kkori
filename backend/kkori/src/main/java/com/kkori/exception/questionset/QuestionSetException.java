package com.kkori.exception.questionset;

import static com.kkori.exception.ExceptionCode.*;
import static com.kkori.exception.ExceptionCode.NO_QUESTION_CONTENT;

import com.kkori.exception.CustomRuntimeException;
import com.kkori.exception.ExceptionCode;

public class QuestionSetException extends CustomRuntimeException {

    public QuestionSetException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static QuestionSetException emptyQuestions() {
        return new QuestionSetException(EMPTY_QUESTIONS);
    }

    public static QuestionSetException noTitle() {
        return new QuestionSetException(NO_TITLE);
    }

    public static QuestionSetException blankTitle() {
        return new QuestionSetException(BLANK_TITLE);
    }

    public static QuestionSetException noQuestionContent() {
        return new QuestionSetException(NO_QUESTION_CONTENT);
    }

    public static QuestionSetException noQuestionType() {
        return new QuestionSetException(NO_QUESTION_TYPE);
    }

    public static QuestionSetException unauthorized() {
        return new QuestionSetException(NOT_LOGGED_IN);
    }

    public static QuestionSetException internalError() {
        return new QuestionSetException(QUESTION_SET_INTERNAL_ERROR);
    }

    public static QuestionSetException questionSetNotFound() {
        return new QuestionSetException(QUESTION_SET_NOT_FOUND_EXCEPTION);
    }

    public static QuestionSetException noPermission() {
        return new QuestionSetException(NO_PERMISSION);
    }

    public static QuestionSetException accessDenied() {
        return new QuestionSetException(ACCESS_DENIED);
    }
}
