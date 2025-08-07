package com.kkori.exception;

import com.kkori.common.CommonApiResponse;
import com.kkori.dto.common.ValidationErrorDetail;
import com.kkori.dto.common.ValidationErrorResponse;
import com.kkori.exception.audio.AudioProcessingException;
import com.kkori.exception.interview.InterviewRoomException;
import com.kkori.exception.interview.InterviewSessionException;
import com.kkori.exception.interview.TailQuestionException;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.exception.user.UnsupportedPrincipalException;
import com.kkori.exception.user.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = ex.getParameterName() + " parameter is missing";
        log.warn("Missing parameter: {}", ex.getParameterName());
        return ResponseEntity.badRequest().body(CommonApiResponse.fail(400, message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(CommonApiResponse.fail(400, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonApiResponse<Void>> handleServerError(Exception ex) {
        log.error("Internal server error", ex);
        return ResponseEntity.internalServerError()
                .body(CommonApiResponse.fail(500, "Internal Server Error"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleRuntime(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(CommonApiResponse.fail(500, ex.getMessage()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleUserException(UserException e) {
        log.warn("User exception: {}", e.getMessage());
        return buildCommonResponse(e.getExceptionCode());
    }

    @ExceptionHandler(InterviewRoomException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleInterviewRoomException(InterviewRoomException e) {
        log.warn("Interview room exception: {}", e.getMessage());
        return buildCommonResponse(e.getExceptionCode());
    }

    @ExceptionHandler(InterviewSessionException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleInterviewSessionException(InterviewSessionException e) {
        log.warn("Interview session exception: {}", e.getMessage());
        return buildCommonResponse(e.getExceptionCode());
    }

    @ExceptionHandler(AudioProcessingException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleAudioProcessingException(AudioProcessingException e) {
        log.warn("Audio processing exception: {}", e.getMessage());
        return buildCommonResponse(e.getExceptionCode());
    }

    @ExceptionHandler(TailQuestionException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleTailQuestionException(TailQuestionException e) {
        log.warn("Tail question exception: {}", e.getMessage());
        return buildCommonResponse(e.getExceptionCode());
    }

    @ExceptionHandler(UnsupportedPrincipalException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleUnsupportedPrincipalException(UnsupportedPrincipalException ex) {
        log.warn("Unsupported principal: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonApiResponse.fail(400, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonApiResponse<ValidationErrorResponse>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorDetail(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(errors);
        
        log.warn("Validation failed with {} errors", errors.size());
        
        return ResponseEntity.badRequest()
                .body(CommonApiResponse.fail(400, "입력 데이터 검증에 실패했습니다.", errorResponse));
    }

    @ExceptionHandler(QuestionSetException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleQuestionSetException(QuestionSetException ex) {
        log.warn("Question set exception: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(CommonApiResponse.fail(ex.getStatus().value(), ex.getMessage()));
    }

    // 공통 응답 생성 메서드 (CommonApiResponse 사용)
    private ResponseEntity<CommonApiResponse<Void>> buildCommonResponse(ExceptionCode code) {
        return ResponseEntity.status(code.getStatus())
                .body(CommonApiResponse.fail(code.getStatus().value(), code.getMessage()));
    }
}
