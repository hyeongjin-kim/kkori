package com.kkori.controller;

import com.kkori.dto.SignalingMessage;
import com.kkori.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebRTCSignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String USER_QUEUE = "/queue/interview";

    @MessageMapping("/create-offer")
    public void handleOffer(@Payload SignalingMessage message, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = requireAuthenticatedUserId(headerAccessor);

        String receiverId = initReceivedOfferMessage(authenticatedUserId, message);

        messagingTemplate.convertAndSendToUser(
                receiverId,
                USER_QUEUE,
                message
        );
    }


    @MessageMapping("/create-answer")
    public void handleAnswer(@Payload SignalingMessage message, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = requireAuthenticatedUserId(headerAccessor);

        String receiverId = initReceivedAnswerMessage(authenticatedUserId, message);

        messagingTemplate.convertAndSendToUser(
                receiverId,
                USER_QUEUE,
                message
        );
    }

    // ==================== 헬퍼 메서드 ====================

    private String initReceivedAnswerMessage(Long authenticatedUserId, SignalingMessage message) {
        message.setSenderId(authenticatedUserId);
        message.setTypeReceivedAnswer();

        return message.getReceiverId().toString();
    }

    private String initReceivedOfferMessage(Long authenticatedUserId, SignalingMessage message) {
        message.setSenderId(authenticatedUserId);
        message.setTypeReceivedOffer();

        return message.getReceiverId().toString();
    }

    /**
     * @throws UserException 인증되지 않은 사용자인 경우
     */
    private Long requireAuthenticatedUserId(SimpMessageHeaderAccessor headerAccessor) {
        Long userId = getAuthenticatedUserId(headerAccessor);
        if (userId == null) {
            throw UserException.webSocketAuthenticationFailed();
        }
        return userId;
    }

    private Long getAuthenticatedUserId(SimpMessageHeaderAccessor headerAccessor) {
        return (Long) headerAccessor.getSessionAttributes().get("userId");
    }
}
