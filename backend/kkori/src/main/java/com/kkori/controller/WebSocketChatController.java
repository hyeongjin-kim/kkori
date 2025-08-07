package com.kkori.controller;

import com.kkori.dto.websocket.WebSocketChatMessage;
import com.kkori.exception.interview.InterviewRoomException;
import com.kkori.service.InterviewSessionService;
import com.kkori.util.WebSocketHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {
    
    private final InterviewSessionService interviewSessionService;
    private final WebSocketHelper webSocketHelper;

    @MessageMapping("/chat")
    public void chatReceive(@Payload WebSocketChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String roomId = chatMessage.roomId();
        Long userId = webSocketHelper.getAuthenticatedUserId(headerAccessor);

        try {
            interviewSessionService.canSendChatMessage(roomId, userId);
        } catch (InterviewRoomException e) {
            webSocketHelper.sendErrorToUser(userId, e.getExceptionCode());
            return;
        }
        webSocketHelper.broadcastToRoom(roomId, "chat", chatMessage);
    }
}
