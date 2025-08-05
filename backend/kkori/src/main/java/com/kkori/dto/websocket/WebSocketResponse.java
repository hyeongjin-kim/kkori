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
     * WebSocket 응답 생성
     */
    public static WebSocketResponse of(String type, Object data) {
        return new WebSocketResponse(type, data, System.currentTimeMillis());
    }
}