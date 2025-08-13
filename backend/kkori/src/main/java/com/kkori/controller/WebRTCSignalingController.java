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
        Long receiverId = getReceiverId(iceCandidate, headerAccessor);
        webSocketHelper.sendPersonalMessage(receiverId, "received-ice-candidate", iceCandidate);
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

        return getReceiverId(roomId, authenticatedUserId);
    }

    private Long getReceiverId(IceCandidate iceCandidate, SimpMessageHeaderAccessor headerAccessor) {

        Long userId = Long.parseLong(iceCandidate.getUserId());

        String roomId = iceCandidate.getRoomId();

        return getReceiverId(roomId, userId);
    }

    private Long getReceiverId(String roomId, Long userId) {
        try {
            return interviewSessionService.getOpponentId(roomId, userId);
        } catch (InterviewRoomException e) {
            webSocketHelper.sendErrorToUser(userId, e.getExceptionCode());
            return null;
        }
    }
}
