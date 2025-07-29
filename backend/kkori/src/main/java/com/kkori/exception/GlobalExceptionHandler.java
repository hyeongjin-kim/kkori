package com.kkori.exception;

import com.kkori.exception.audio.AudioProcessingException;
import com.kkori.exception.interview.InterviewRoomException;
import com.kkori.exception.interview.InterviewSessionException;
import com.kkori.exception.interview.TailQuestionException;
import com.kkori.exception.user.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = ex.getParameterName() + " parameter is missing";
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleServerError(Exception ex) {
        return ResponseEntity.internalServerError().body("Internal Server Error");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

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
