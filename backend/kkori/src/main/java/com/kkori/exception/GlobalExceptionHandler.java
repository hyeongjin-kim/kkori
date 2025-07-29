package com.kkori.exception;

import com.kkori.exception.ExceptionResponse;
import com.kkori.exception.audio.AudioProcessingException;
import com.kkori.exception.interview.InterviewRoomException;
import com.kkori.exception.interview.InterviewSessionException;
import com.kkori.exception.interview.TailQuestionException;
import com.kkori.exception.user.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse> handleUserException(UserException e) {
        return buildResponse(e.getExceptionCode());
    }

    @ExceptionHandler(InterviewRoomException.class)
    public ResponseEntity<ExceptionResponse> handleInterviewRoomException(InterviewRoomException e) {
        return buildResponse(e.getExceptionCode());
    }

    @ExceptionHandler(InterviewSessionException.class)
    public ResponseEntity<ExceptionResponse> handleInterviewSessionException(InterviewSessionException e) {
        return buildResponse(e.getExceptionCode());
    }

    @ExceptionHandler(AudioProcessingException.class)
    public ResponseEntity<ExceptionResponse> handleAudioProcessingException(AudioProcessingException e) {
        return buildResponse(e.getExceptionCode());
    }

    @ExceptionHandler(TailQuestionException.class)
    public ResponseEntity<ExceptionResponse> handleTailQuestionException(TailQuestionException e) {
        return buildResponse(e.getExceptionCode());
    }

    // 공통 응답 생성 메서드
    private ResponseEntity<ExceptionResponse> buildResponse(ExceptionCode code) {
        ExceptionResponse response = new ExceptionResponse(
                code.getCode(),
                code.getMessage()
        );
        return ResponseEntity.status(code.getStatus()).body(response);
    }
}