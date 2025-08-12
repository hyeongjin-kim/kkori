package com.kkori.config.validator;

import com.kkori.component.InterviewRoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSecurityValidator {
    
    private final InterviewRoomManager interviewRoomManager;
    
    /**
     * 사용자가 특정 방에 속해있는지 확인
     */
    public boolean isUserInRoom(String roomId, Long userId) {
        try {
            return interviewRoomManager.getRoom(roomId).getUserIds().contains(userId);
        } catch (Exception e) {
            log.debug("Failed to check if user {} is in room {}: {}", userId, roomId, e.getMessage());
            return false;
        }
    }
}