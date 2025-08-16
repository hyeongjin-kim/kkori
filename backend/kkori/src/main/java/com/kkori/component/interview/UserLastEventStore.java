package com.kkori.component.interview;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class UserLastEventStore {
    // 기존 코드
    private final Map<Long, Object> userLastEventMap = new ConcurrentHashMap<>();
    private final Map<Long, String> userLastStatusMap = new ConcurrentHashMap<>();

    public void updateEvent(Long userId, Object newEvent) {
        userLastEventMap.put(userId, newEvent);
    }

    public Object getLastEvent(Long userId) {
        return userLastEventMap.get(userId);
    }

    public void updateStatus(Long userId, String newStatus) {
        userLastStatusMap.put(userId, newStatus);
    }

    public String getLastStatus(Long userId) {
        return userLastStatusMap.get(userId);
    }

    public void updateUserStatus(Long userId, String statusName) {
        if (InterviewStatus.isTransitionalStatus(statusName)) {
            return;
        }
        updateStatus(userId, statusName);
    }

    public InterviewStatus getLastInterviewStatus(Long userId) {
        String statusName = getLastStatus(userId);
        if (statusName == null) {
            return null;
        }
        
        try {
            return InterviewStatus.fromStatusName(statusName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
