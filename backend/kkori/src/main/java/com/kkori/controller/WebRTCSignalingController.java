package com.kkori.controller;

import com.kkori.dto.SignalingMessage;
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

    @MessageMapping("/offer")
    public void handleOffer(@Payload SignalingMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // 송신자 ID를 인증된 사용자 ID로 설정
        Long authenticatedUserId = getAuthenticatedUserId(headerAccessor);
        if (authenticatedUserId == null) {
            return;
        }

        message.setSenderId(authenticatedUserId);

        // 수신자에게 offer 전송
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(),
                "/queue/interview",
                message
        );
    }

    @MessageMapping("/answer")
    public void handleAnswer(@Payload SignalingMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // 송신자 ID를 인증된 사용자 ID로 설정
        Long authenticatedUserId = getAuthenticatedUserId(headerAccessor);
        if (authenticatedUserId == null) {
            return;
        }

        message.setSenderId(authenticatedUserId);

        // 수신자에게 answer 전송
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(),
                "/queue/interview",
                message
        );
    }

    private Long getAuthenticatedUserId(SimpMessageHeaderAccessor headerAccessor) {
        return (Long) headerAccessor.getSessionAttributes().get("userId");
    }
}
