package com.kkori.dto.websocket;

/**
 * WebSocket 메시지 공통 응답 형식
 */
public record WebSocketResponse(
    String type,
    Object data,
    long timestamp
) {
    
    /**
     * 일반 응답 생성 (현업에서 가장 많이 사용)
     */
    public static WebSocketResponse of(String type, Object data) {
        return new WebSocketResponse(type, data, System.currentTimeMillis());
    }
    
    /**
     * 성공 응답 생성
     */
    public static WebSocketResponse success(String type, Object data) {
        return of(type, data);
    }
    
    /**
     * 에러 응답 생성
     */
    public static WebSocketResponse error(String error, String message) {
        ErrorData errorData = new ErrorData(error, message, String.valueOf(System.currentTimeMillis()));
        return of("error", errorData);
    }
    
    /**
     * 데이터 없는 단순 메시지
     */
    public static WebSocketResponse message(String type, String message) {
        return of(type, new SimpleMessage(message, String.valueOf(System.currentTimeMillis())));
    }
    
    /**
     * 에러 데이터 레코드
     */
    public record ErrorData(
        String error,
        String message,
        String timestamp
    ) {}
    
    /**
     * 단순 메시지 레코드
     */
    public record SimpleMessage(
        String message,
        String timestamp
    ) {}
}