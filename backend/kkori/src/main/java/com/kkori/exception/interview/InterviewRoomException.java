package com.kkori.exception.interview;

import com.kkori.exception.CustomRuntimeException;
import com.kkori.exception.ExceptionCode;

public class InterviewRoomException extends CustomRuntimeException {
    public InterviewRoomException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static InterviewRoomException roomNotFound() {
        return new InterviewRoomException(ExceptionCode.ROOM_NOT_FOUND);
    }

    public static InterviewRoomException cannotJoinRoom() {
        return new InterviewRoomException(ExceptionCode.CANNOT_JOIN_ROOM);
    }

    public static InterviewRoomException cannotStartInterview() {
        return new InterviewRoomException(ExceptionCode.CANNOT_START_INTERVIEW);
    }

    public static InterviewRoomException interviewNotStarted() {
        return new InterviewRoomException(ExceptionCode.INTERVIEW_NOT_STARTED);
    }

    public static InterviewRoomException interviewNotInProgress() {
        return new InterviewRoomException(ExceptionCode.INTERVIEW_NOT_IN_PROGRESS);
    }

    public static InterviewRoomException bothRolesRequired() {
        return new InterviewRoomException(ExceptionCode.BOTH_ROLES_REQUIRED);
    }

    public static InterviewRoomException cannotChangeRolesAfterStart() {
        return new InterviewRoomException(ExceptionCode.CANNOT_CHANGE_ROLES_AFTER_START);
    }

    public static InterviewRoomException userNotFoundInRoom() {
        return new InterviewRoomException(ExceptionCode.USER_NOT_FOUND_IN_ROOM);
    }
}