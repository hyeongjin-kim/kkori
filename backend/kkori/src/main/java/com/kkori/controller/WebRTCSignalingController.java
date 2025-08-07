package com.kkori.controller;

import com.kkori.dto.websocket.SignalingMessage;
import com.kkori.util.WebSocketHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebRTCSignalingController {

    private final WebSocketHelper webSocketHelper;

    @MessageMapping("/create-offer")
    public void handleOffer(@Payload SignalingMessage message, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        String receiverId = initReceivedOfferMessage(authenticatedUserId, message);

        webSocketHelper.sendSDP(receiverId, message);
    }


    @MessageMapping("/create-answer")
    public void handleAnswer(@Payload SignalingMessage message, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        String receiverId = initReceivedAnswerMessage(authenticatedUserId, message);

        webSocketHelper.sendSDP(receiverId, message);
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
}
