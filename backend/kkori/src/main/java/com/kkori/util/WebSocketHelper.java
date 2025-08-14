package com.kkori.util;

import com.kkori.component.interview.UserLastEventStore;
import com.kkori.dto.interview.response.ErrorResponse;
import com.kkori.dto.websocket.WebSocketResponse;
import com.kkori.exception.ExceptionCode;
import com.kkori.exception.user.UserException;
import com.kkori.service.InterviewSessionService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketHelper {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserLastEventStore userLastEventStore;
    private final InterviewSessionService interviewSessionService;

    private static final String ROOM_TOPIC_PREFIX = "/topic/interview/";
    private static final String USER_QUEUE = "/queue/interview";
    private final Set<String> updateIgnoreTypes = Set.of(
            "chat",
            "room-status",
            "roles-swapped",
            "interview-ended",
            "answer-recording-started",
            "custom-question-recording-started"
    );

    // ==================== 메시지 전송 관련 ====================

    public void sendPersonalMessage(Long userId, String messageType, Object responseData) {
        try {
            System.out.println("===sending personal message===");
            Object payload = createResponse(messageType, responseData);
            updateLastEvent(Set.of(userId), messageType, payload);

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    USER_QUEUE,
                    payload
            );
        } catch (Exception e) {
            // 개인 메시지 전송 실패 시 무시
        }
    }

    public void broadcastToRoom(String roomId, String messageType, Object responseData) {
        try {
            Set<Long> userIds = interviewSessionService.getUserIdsInRoom(roomId);
            Object payload = createResponse(messageType, responseData);
            updateLastEvent(userIds, messageType, payload);

            messagingTemplate.convertAndSend(
                    ROOM_TOPIC_PREFIX + roomId,
                    payload
            );
        } catch (Exception e) {
            // 브로드캐스트 전송 실패 시 무시
        }
    }

    public void sendLastEventToUser(Long userId) {
        Object lastEvent = userLastEventStore.getLastEvent(userId);
        if (lastEvent == null) {
            return;
        }
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    USER_QUEUE,
                    lastEvent
            );
        } catch (Exception e) {

        }
    }

    /**
     * WebSocket 에러 메시지 전송
     */
    public void sendErrorToUser(Long userId, String error, String message) {
        if (userId != null) {
            ErrorResponse errorResponse = new ErrorResponse(
                    error,
                    message
            );
            sendPersonalMessage(userId, "error", errorResponse);
        }
    }

    /**
     * ExceptionCode를 활용한 에러 전송
     */
    public void sendErrorToUser(Long userId, ExceptionCode exceptionCode, String detailMessage) {
        sendErrorToUser(userId, exceptionCode.getMessage(), detailMessage);
    }

    public void sendErrorToUser(Long userId, ExceptionCode exceptionCode) {
        sendErrorToUser(userId, exceptionCode.getMessage(), exceptionCode.getMessage());
    }

    public WebSocketResponse createResponse(String type, Object data) {
        return WebSocketResponse.of(type, data);
    }

    // ==================== 인증 관련 ====================

    public Long getAuthenticatedUserId(SimpMessageHeaderAccessor headerAccessor) {
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        if (userId == null) {
            sendErrorToUser(null, ExceptionCode.WEBSOCKET_AUTHENTICATION_FAILED);
        }
        return userId;
    }

    /**
     * @throws UserException 인증되지 않은 사용자인 경우
     */
    public Long requireAuthenticatedUserId(SimpMessageHeaderAccessor headerAccessor) {
        Long userId = getAuthenticatedUserId(headerAccessor);
        if (userId == null) {
            throw UserException.webSocketAuthenticationFailed();
        }
        return userId;
    }

    public void updateLastStatus(Long userId, String newStatus) {
        userLastEventStore.updateStatus(userId, newStatus);
    }

    public String getLastStatus(Long userId) {
        return userLastEventStore.getLastStatus(userId);
    }

    // ==================== 헬퍼 메서드 ====================
    private void updateLastEvent(Set<Long> userIds, String messageType, Object newEvent) {
        if (updateIgnoreTypes.contains(messageType)) {
            return;
        }
        userIds.forEach(userId -> userLastEventStore.updateEvent(userId, newEvent));
    }

}
