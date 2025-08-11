package com.kkori.exception.interview;

import com.kkori.exception.CustomRuntimeException;
import com.kkori.exception.ExceptionCode;

public class TailQuestionException extends CustomRuntimeException {
    public TailQuestionException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static TailQuestionException tailQuestionGenerationFailed() {
        return new TailQuestionException(ExceptionCode.TAIL_QUESTION_GENERATION_FAILED);
    }

    public static TailQuestionException tailQuestionApiCallFailed() {
        return new TailQuestionException(ExceptionCode.TAIL_QUESTION_API_CALL_FAILED);
    }

    public static TailQuestionException jsonConversionFailed() {
        return new TailQuestionException(ExceptionCode.JSON_CONVERSION_FAILED);
    }

    public static TailQuestionException tailQuestionNotFound() {
        return new TailQuestionException(ExceptionCode.TAIL_QUESTION_NOT_FOUND);
    }
    
    public static TailQuestionException tailQuestionAlreadyAnswered() {
        return new TailQuestionException(ExceptionCode.TAIL_QUESTION_ALREADY_ANSWERED);
    }

    public static TailQuestionException interviewAlreadyCompleted() {
        return new TailQuestionException(ExceptionCode.INTERVIEW_ALREADY_COMPLETED);
    }

    public static TailQuestionException interviewConcurrencyError() {
        return new TailQuestionException(ExceptionCode.INTERVIEW_CONCURRENCY_ERROR);
    }
}