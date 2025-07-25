package com.kkori.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // ==== Interview Room 관련 ====
    ROOM_NOT_FOUND(1001, "면접 방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, null),
    CANNOT_JOIN_ROOM(1002, "면접 방에 참여할 수 없습니다.", HttpStatus.BAD_REQUEST, null),
    CANNOT_START_INTERVIEW(1003, "면접을 시작할 수 없습니다.", HttpStatus.BAD_REQUEST, null),
    INTERVIEW_NOT_STARTED(1004, "면접이 시작되지 않았습니다.", HttpStatus.BAD_REQUEST, null),
    INTERVIEW_NOT_IN_PROGRESS(1005, "진행 중인 면접이 없습니다.", HttpStatus.BAD_REQUEST, null),
    BOTH_ROLES_REQUIRED(1006, "면접관과 면접자가 모두 필요합니다.", HttpStatus.BAD_REQUEST, null),
    CANNOT_CHANGE_ROLES_AFTER_START(1007, "면접 시작 후에는 역할을 변경할 수 없습니다.", HttpStatus.BAD_REQUEST, null),
    USER_NOT_FOUND_IN_ROOM(1008, "방에서 해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, null),

    // ==== Permission 관련 ====
    ONLY_INTERVIEWEE_CAN_SUBMIT_ANSWER(2001, "면접자만 답변을 제출할 수 있습니다.", HttpStatus.FORBIDDEN, null),
    PERMISSION_DENIED(2002, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN, null),

    // ==== Question 관련 ====
    QUESTION_SET_NOT_FOUND(3001, "질문 세트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, null),
    INTERVIEWER_NOT_FOUND(3002, "면접관 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, null),
    INTERVIEWEE_NOT_FOUND(3003, "면접자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, null),
    INTERVIEW_NOT_FOUND(3004, "면접 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, null),
    DEFAULT_QUESTION_NOT_FOUND(3005, "기본 질문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, null),
    TAIL_QUESTION_REQUIRES_PARENT(3006, "꼬리질문은 부모 질문이 필요합니다.", HttpStatus.BAD_REQUEST, null),

    // ==== Audio Processing 관련 ====
    AUDIO_PROCESSING_FAILED(4001, "음성 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null),
    AUDIO_TRANSCRIPTION_FAILED(4002, "음성 변환에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null),
    API_CALL_FAILED(4003, "API 호출에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null),

    // ==== Tail Question Generation 관련 ====
    TAIL_QUESTION_GENERATION_FAILED(5001, "꼬리 질문 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null),
    JSON_CONVERSION_FAILED(5002, "JSON 변환에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null);

    private final int code;
    private final String message;
    private final HttpStatus status;
    private final String redirectPath;

    ExceptionCode(int code, String message, HttpStatus status, String redirectPath) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.redirectPath = redirectPath;
    }
}