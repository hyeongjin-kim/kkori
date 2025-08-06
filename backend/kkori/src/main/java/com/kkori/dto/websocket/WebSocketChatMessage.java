package com.kkori.dto.websocket;

public record WebSocketChatMessage(
        String roomId,
        String senderNickname,
        String content,
        long timestamp
) {

}
