package com.kkori.exception.interview;

import com.kkori.exception.CustomRuntimeException;
import com.kkori.exception.ExceptionCode;

public class InterviewRecordException extends CustomRuntimeException {
    
    public InterviewRecordException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
    
    public static InterviewRecordException notFound() {
        return new InterviewRecordException(ExceptionCode.INTERVIEW_RECORD_NOT_FOUND);
    }
    
    public static InterviewRecordException accessDenied() {
        return new InterviewRecordException(ExceptionCode.INTERVIEW_RECORD_ACCESS_DENIED);
    }
    
    public static InterviewRecordException listFetchFailed() {
        return new InterviewRecordException(ExceptionCode.INTERVIEW_RECORD_LIST_FETCH_FAILED);
    }
    
    public static InterviewRecordException detailFetchFailed() {
        return new InterviewRecordException(ExceptionCode.INTERVIEW_RECORD_DETAIL_FETCH_FAILED);
    }
}