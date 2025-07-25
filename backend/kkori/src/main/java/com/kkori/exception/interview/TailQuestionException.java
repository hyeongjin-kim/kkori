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

    public static TailQuestionException jsonConversionFailed() {
        return new TailQuestionException(ExceptionCode.JSON_CONVERSION_FAILED);
    }
}