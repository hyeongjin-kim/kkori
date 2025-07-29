package com.kkori.component;

import com.kkori.component.interview.InterviewRoom;
import com.kkori.component.interview.InterviewSession;
import com.kkori.component.interview.Permission;
import com.kkori.component.interview.UserRole;
import com.kkori.exception.interview.InterviewRoomException;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class InterviewRoomManager {

    private static final int MAX_USERS = 2;

    private final Map<String, InterviewRoom> rooms = new ConcurrentHashMap<>();

    /**
     * 혼자 연습하기 방 생성
     */
    public String createSoloRoom(Long questionSetId, Long creatorId, InterviewSession session) {
        String roomId = generateRoomId();
        InterviewRoom room = InterviewRoom.createSoloRoom(roomId, questionSetId, creatorId, session);
        rooms.put(roomId, room);

        return roomId;
    }

    /**
     * 함께 연습하기 방 생성
     */
    public String createPairRoom(Long questionSetId, Long creatorId, InterviewSession session) {
        String roomId = generateRoomId();
        InterviewRoom room = InterviewRoom.createPairRoom(roomId, questionSetId, creatorId, session);
        rooms.put(roomId, room);

        return roomId;
    }

    /**
     * 방 참여 (함께 연습하기 모드에서만 사용)
     */
    public void joinRoom(String roomId, Long userId) {
        InterviewRoom room = getRoom(roomId);

        // 이미 방에 해당 사용자가 있는지 확인
        if (room.getUserIds().contains(userId)) {
            return;
        }

        room.addUser(userId);
    }

    /**
     * 방 나가기
     */
    public void exitRoom(String roomId, Long userId) {
        InterviewRoom room = getRoom(roomId);

        // 방에 없는 사용자인 경우 무시
        if (!room.getUserIds().contains(userId)) {
            return;
        }

        room.removeUser(userId);
        checkAndCleanupRoom(room, roomId);
    }

    /**
     * 방 정리 (비어있거나 완료된 경우)
     */
    private void checkAndCleanupRoom(InterviewRoom room, String roomId) {
        if (shouldDeleteRoom(room)) {
            removeRoom(roomId);
        }
    }

    /**
     * 방 삭제 조건 확인
     */
    private boolean shouldDeleteRoom(InterviewRoom room) {
        return room.isEmpty() || room.isCompleted();
    }

    /**
     * 면접 시작
     */
    public void startInterview(String roomId, Long interviewId) {
        InterviewRoom room = getRoom(roomId);
        room.startInterview(interviewId);
    }

    /**
     * 면접 완료
     */
    public void completeInterview(String roomId) {
        InterviewRoom room = getRoom(roomId);
        room.completeInterview();

        // 면접 완료 후 방 정리
        checkAndCleanupRoom(room, roomId);
    }

    /**
     * 방 조회
     */
    public InterviewRoom getRoom(String roomId) {
        InterviewRoom room = rooms.get(roomId);
        if (room == null) {
            throw InterviewRoomException.roomNotFound();
        }
        return room;
    }

    /**
     * 세션 조회 (항상 사용 가능)
     */
    public InterviewSession getSession(String roomId) {
        InterviewRoom room = getRoom(roomId);
        return room.getSession();
    }

    /**
     * 방 존재 여부 확인
     */
    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }

    /**
     * 방 삭제
     */
    private void removeRoom(String roomId) {
        rooms.remove(roomId);
    }

    /**
     * 룸 ID 생성
     */
    private String generateRoomId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    /**
     * 역할 변경 (함께 연습하기 모드에서만 사용)
     */
    public void swapRoles(String roomId) {
        InterviewRoom room = getRoom(roomId);
        room.swapRoles();
    }

    /**
     * 사용자 권한 확인
     */
    public boolean hasPermission(String roomId, Long userId, Permission permission) {
        InterviewRoom room = getRoom(roomId);
        return room.hasPermission(userId, permission);
    }

    /**
     * 사용자 역할 조회
     */
    public UserRole getUserRole(String roomId, Long userId) {
        InterviewRoom room = getRoom(roomId);
        return room.getUserRole(userId);
    }

    /**
     * 활성 방 개수 조회 (모니터링용)
     */
    public int getActiveRoomCount() {
        return rooms.size();
    }

    /**
     * 특정 사용자가 참여 중인 방 조회
     */
    public String findRoomByUser(Long userId) {
        return rooms.entrySet().stream()
                .filter(entry -> entry.getValue().getUserIds().contains(userId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * 방 참여 가능 여부 확인
     */
    public boolean canJoinRoom(String roomId) {
        try {
            InterviewRoom room = getRoom(roomId);
            return room.getStatus().canJoin() &&
                    !room.isSoloPractice() &&
                    room.getUserIds().size() < MAX_USERS;
        } catch (InterviewRoomException e) {
            return false;
        }
    }

    /**
     * 면접 시작 가능 여부 확인
     */
    public boolean canStartInterview(String roomId) {
        try {
            InterviewRoom room = getRoom(roomId);

            if (!room.getStatus().canStart()) {
                return false;
            }

            // 혼자 연습하기: 방장만 있으면 OK
            if (room.isSoloPractice()) {
                return room.getUserIds().size() == 1;
            }

            // 함께 연습하기: 2명 모두 있어야 함
            return room.getUserIds().size() == MAX_USERS;

        } catch (InterviewRoomException e) {
            return false;
        }
    }

    /**
     * 방 강제 종료 (관리자용)
     */
    public void forceCompleteRoom(String roomId) {
        InterviewRoom room = getRoom(roomId);
        room.completeInterview();

        checkAndCleanupRoom(room, roomId);
    }

    /**
     * 사용자가 방장인지 확인
     */
    public boolean isCreator(String roomId, Long userId) {
        InterviewRoom room = getRoom(roomId);
        return room.getCreatorId().equals(userId);
    }
}