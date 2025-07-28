package com.kkori.exception.interview;

import com.kkori.exception.CustomRuntimeException;
import com.kkori.exception.ExceptionCode;

public class InterviewSessionException extends CustomRuntimeException {
    public InterviewSessionException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static InterviewSessionException questionSetNotFound() {
        return new InterviewSessionException(ExceptionCode.QUESTION_SET_NOT_FOUND);
    }

    public static InterviewSessionException interviewerNotFound() {
        return new InterviewSessionException(ExceptionCode.INTERVIEWER_NOT_FOUND);
    }

    public static InterviewSessionException intervieweeNotFound() {
        return new InterviewSessionException(ExceptionCode.INTERVIEWEE_NOT_FOUND);
    }

    public static InterviewSessionException interviewNotFound() {
        return new InterviewSessionException(ExceptionCode.INTERVIEW_NOT_FOUND);
    }

    public static InterviewSessionException defaultQuestionNotFound() {
        return new InterviewSessionException(ExceptionCode.DEFAULT_QUESTION_NOT_FOUND);
    }

    public static InterviewSessionException tailQuestionRequiresParent() {
        return new InterviewSessionException(ExceptionCode.TAIL_QUESTION_REQUIRES_PARENT);
    }

    public static InterviewSessionException onlyIntervieweeCanSubmitAnswer() {
        return new InterviewSessionException(ExceptionCode.ONLY_INTERVIEWEE_CAN_SUBMIT_ANSWER);
    }

    public static InterviewSessionException permissionDenied() {
        return new InterviewSessionException(ExceptionCode.PERMISSION_DENIED);
    }
    public static InterviewSessionException parentQuestionNotFound() {
        return new InterviewSessionException(ExceptionCode.PARENT_QUESTION_NOT_FOUND);
    }
}