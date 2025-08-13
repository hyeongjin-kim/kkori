package com.kkori.controller;

import com.kkori.dto.websocket.IceCandidate;
import com.kkori.dto.websocket.SignalingMessage;
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
public class WebRTCSignalingController {

    private final InterviewSessionService interviewSessionService;
    private final WebSocketHelper webSocketHelper;

    @MessageMapping("/create-offer")
    public void handleOffer(@Payload SignalingMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleSignaling(message, headerAccessor, "received-offer");
    }

    @MessageMapping("/create-answer")
    public void handleAnswer(@Payload SignalingMessage message, SimpMessageHeaderAccessor headerAccessor) {
        handleSignaling(message, headerAccessor, "received-answer");
    }

    @MessageMapping("/new-ice-candidate")
    public void handleIceCandidate(@Payload IceCandidate iceCandidate, SimpMessageHeaderAccessor headerAccessor) {
        webSocketHelper.sendPersonalMessage(iceCandidate.getUserId(), "received-ice-candidate", iceCandidate);
    }
    // ==================== 헬퍼 메서드 ====================

    private void handleSignaling(SignalingMessage message, SimpMessageHeaderAccessor headerAccessor,
                                 String messageType) {
        Long receiverId = getReceiverId(message, headerAccessor);

        if (receiverId == null) {
            return;
        }

        webSocketHelper.sendPersonalMessage(receiverId, messageType, message.getSdp());
    }

    private Long getReceiverId(SignalingMessage message, SimpMessageHeaderAccessor headerAccessor) {

        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        String roomId = message.getRoomId();

        try {
            return interviewSessionService.getOpponentId(roomId, authenticatedUserId);
        } catch (InterviewRoomException e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, e.getExceptionCode());
            return null;
        }
    }
}
