package com.kkori.controller;

import com.kkori.dto.interview.response.ErrorResponse;
import com.kkori.dto.websocket.WebSocketChatMessage;
import com.kkori.dto.websocket.WebSocketResponse;
import com.kkori.exception.ExceptionCode;
import com.kkori.exception.interview.InterviewRoomException;
import com.kkori.service.InterviewSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final InterviewSessionService interviewSessionService;

    private static final String ROOM_TOPIC_PREFIX = "/topic/interview/";
    private static final String USER_QUEUE = "/queue/interview";

    // chatReceive
    @MessageMapping("/chat")
    public void chatReceive(@Payload WebSocketChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String roomId = chatMessage.roomId();
        Long userId = getAuthenticatedUserId(headerAccessor);

        try {
            interviewSessionService.canSendChatMessage(roomId, userId);
        } catch (InterviewRoomException e) {
            sendErrorToUser(userId, e.getExceptionCode());
            return;
        }
        messagingTemplate.convertAndSend(
                ROOM_TOPIC_PREFIX + roomId,
                WebSocketResponse.of("chat", chatMessage)
        );
    }

    private Long getAuthenticatedUserId(SimpMessageHeaderAccessor headerAccessor) {
        return (Long) headerAccessor.getSessionAttributes().get("userId");
    }


    /**
     * WebSocket 에러 메시지 전송
     */
    private void sendErrorToUser(Long userId, String error, String message) {
        if (userId != null) {
            ErrorResponse errorResponse = new ErrorResponse(
                    error,
                    message,
                    String.valueOf(System.currentTimeMillis())
            );
            sendPersonalMessage(userId, "error", errorResponse);
        }
    }

    /**
     * ExceptionCode를 활용한 Web에러 전송
     */
    private void sendErrorToUser(Long userId, ExceptionCode exceptionCode) {
        sendErrorToUser(userId, exceptionCode.getMessage(), exceptionCode.getMessage());
    }

    private void sendPersonalMessage(Long userId, String messageType, Object responseData) {
        try {
            System.out.println("===sending personal message===");
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    USER_QUEUE,
                    createResponse(messageType, responseData)
            );
        } catch (Exception e) {
            // 개인 메시지 전송 실패 시 무시
        }
    }

    private WebSocketResponse createResponse(String type, Object data) {
        return WebSocketResponse.of(type, data);
    }
}
