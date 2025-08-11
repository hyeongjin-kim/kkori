package com.kkori.component.interview;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class UserLastEventStore {
    private final Map<Long, Object> userLastEventMap = new ConcurrentHashMap<>();

    public void updateEvent(Long userId, Object newEvent) {
        userLastEventMap.put(userId, newEvent);
    }

    public Object getLastEvent(Long userId) {
        return userLastEventMap.get(userId);
    }
}
