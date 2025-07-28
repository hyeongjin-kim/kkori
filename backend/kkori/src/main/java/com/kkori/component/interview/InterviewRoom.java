package com.kkori.component.interview;

import static com.kkori.component.interview.Permission.*;
import com.kkori.exception.interview.InterviewRoomException;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewRoom {

    private final String roomId;
    private final Long questionSetId;
    private final Long creatorId;
    private final LocalDateTime createdAt;
    private final InterviewSession session; // 방 생성 시 설정
    private final InterviewMode mode; // 면접 모드

    private static final int MAX_USERS = 2;

    private RoomStatus status = RoomStatus.WAITING;

    private Long interviewId;

    private Long interviewerId;
    private Long intervieweeId;

    /**
     * 방 생성 공통 로직
     */
    private static InterviewRoom createRoom(String roomId, Long questionSetId, Long creatorId,
                                            InterviewSession session, InterviewMode mode) {
        return new InterviewRoom(roomId, questionSetId, creatorId, LocalDateTime.now(), session, mode);
    }

    /**
     * 혼자 연습하기 방 생성
     */
    public static InterviewRoom createSoloRoom(String roomId, Long questionSetId,
                                               Long creatorId, InterviewSession session) {
        InterviewRoom room = createRoom(roomId, questionSetId, creatorId, session, InterviewMode.SOLO_PRACTICE);
        room.intervieweeId = creatorId; // 방장은 면접자
        room.interviewerId = creatorId; // 방장은 면접관
        return room;
    }

    /**
     * 함께 연습하기 방 생성
     */
    public static InterviewRoom createPairRoom(String roomId, Long questionSetId,
                                               Long creatorId, InterviewSession session) {
        InterviewRoom room = createRoom(roomId, questionSetId, creatorId, session, InterviewMode.PAIR_INTERVIEW);
        room.intervieweeId = creatorId; // 방장은 기본적으로 면접자
        return room;
    }

    /**
     * 사용자 참여 (함께 연습하기 모드에서만 사용)
     */
    public void addUser(@NonNull Long userId) {
        validateCanJoin();

        if (mode == InterviewMode.SOLO_PRACTICE) {
            throw InterviewRoomException.cannotJoinRoom();
        }

        // 이미 2명이면 참여 불가
        if (getUserIds().size() >= MAX_USERS) {
            throw InterviewRoomException.cannotJoinRoom();
        }

        // 빈 자리에 배정 (면접관 우선)
        if (interviewerId == null) {
            interviewerId = userId;
            return;
        }

        if (intervieweeId == null) {
            intervieweeId = userId;
            return;
        }

        // 이론적으로는 여기 도달하면 안됨 (위에서 사이즈 체크했으므로)
        throw InterviewRoomException.cannotJoinRoom();
    }

    /**
     * 참여 가능 여부 검증
     */
    private void validateCanJoin() {
        if (!status.canJoin()) {
            throw InterviewRoomException.cannotJoinRoom();
        }
    }

    /**
     * 사용자 퇴장
     * 면접이 종료되어도, 사용자 정보는 보존해야함. DB 저장 용도
     */
    public void removeUser(@NonNull Long userId) {
        // 방장이 나가면 무조건 방 종료
        if (isCreator(userId)) {
            this.status = RoomStatus.COMPLETED;
            return;
        }

        // 면접 시작 전: 실제로 사용자 제거
        if (!isStarted()) {
            if (userId.equals(interviewerId)) {
                this.interviewerId = null;
            }
            if (userId.equals(intervieweeId)) {
                this.intervieweeId = null;
            }
            return;
        }

        // 면접 진행 중: 사용자 정보 유지하고 면접만 완료 처리
        this.status = RoomStatus.COMPLETED;
    }

    /**
     * 면접 시작
     */
    public void startInterview(@NonNull Long interviewId) {
        validateCanStart();
        validateUsersForStart();

        this.interviewId = interviewId;
        this.status = RoomStatus.STARTED;
    }

    /**
     * 면접 시작 가능 여부 검증
     */
    private void validateCanStart() {
        if (!status.canStart()) {
            throw InterviewRoomException.cannotStartInterview();
        }
    }

    /**
     * 면접 시작을 위한 사용자 검증 (모드별로 다름)
     */
    private void validateUsersForStart() {
        if (mode == InterviewMode.SOLO_PRACTICE) {
            validateSoloModeUsers();
        } else {
            validatePairModeUsers();
        }
    }

    /**
     * 혼자 연습하기 모드 사용자 검증
     */
    private void validateSoloModeUsers() {
        if (!isCreator(interviewerId) || !isCreator(intervieweeId)) {
            throw InterviewRoomException.bothRolesRequired();
        }
    }

    /**
     * 함께 연습하기 모드 사용자 검증
     */
    private void validatePairModeUsers() {
        if (interviewerId == null || intervieweeId == null) {
            throw InterviewRoomException.bothRolesRequired();
        }
    }

    /**
     * 면접 완료
     */
    public void completeInterview() {
        if (!isStarted()) {
            throw InterviewRoomException.interviewNotInProgress();
        }
        this.status = RoomStatus.COMPLETED;
    }

    /**
     * 특정 사용자의 역할 조회
     */
    public UserRole getUserRole(Long userId) {
        if (userId == null) {
            return null;
        }

        if (userId.equals(interviewerId)) {
            return UserRole.INTERVIEWER;
        }

        if (userId.equals(intervieweeId)) {
            return UserRole.INTERVIEWEE;
        }

        return null;
    }

    /**
     * 사용자가 특정 권한을 가지는지 확인
     */
    public boolean hasPermission(Long userId, @NonNull Permission permission) {
        // 면접 시작/종료는 방장만 가능
        if (isCreatorOnlyPermission(permission)) {
            return isCreator(userId);
        }

        UserRole role = getUserRole(userId);
        return role != null && checkPermissionByRole(role, permission);
    }

    /**
     * 방장만 가능한 권한인지 확인
     */
    private boolean isCreatorOnlyPermission(Permission permission) {
        return START_INTERVIEW.equals(permission) || END_INTERVIEW.equals(permission);
    }

    /**
     * 역할별 권한 확인 (면접 시작/종료 제외)
     */
    private boolean checkPermissionByRole(@NonNull UserRole role, @NonNull Permission permission) {
        return switch (permission) {
            case SUBMIT_ANSWER -> role == UserRole.INTERVIEWEE;
            case CREATE_CUSTOM_QUESTION, CHOOSE_NEXT_QUESTION, CHANGE_ROLES -> role == UserRole.INTERVIEWER;
            default -> false;
        };
    }

    /**
     * 사용자 목록 조회
     */
    public Set<Long> getUserIds() {
        Set<Long> userIds = new HashSet<>();
        addIfNotNull(userIds, interviewerId);
        addIfNotNull(userIds, intervieweeId);
        return userIds;
    }

    /**
     * null이 아닌 경우 Set에 추가
     */
    private void addIfNotNull(@NonNull Set<Long> userIds, Long userId) {
        if (userId != null) {
            userIds.add(userId);
        }
    }

    /**
     * 방이 비어있는지 확인
     */
    public boolean isEmpty() {
        return interviewerId == null && intervieweeId == null;
    }

    /**
     * 방장이 나갔는지 확인
     */
    public boolean isCreatorLeft() {
        return !isCreatorPresent();
    }

    /**
     * 방장이 방에 있는지 확인
     */
    private boolean isCreatorPresent() {
        return isCreator(interviewerId) || isCreator(intervieweeId);
    }

    /**
     * 특정 사용자가 방장인지 확인
     */
    private boolean isCreator(Long userId) {
        return userId != null && userId.equals(creatorId);
    }

    /**
     * 면접이 시작되었는지 확인 (진행 중인지)
     */
    public boolean isStarted() {
        return status.isInProgress();
    }

    /**
     * 면접이 완료되었는지 확인
     */
    public boolean isCompleted() {
        return status.isCompleted();
    }

    /**
     * 혼자 연습하기인지 확인
     */
    public boolean isSoloPractice() {
        return mode == InterviewMode.SOLO_PRACTICE;
    }

    /**
     * 역할 변경 (모드에 따라 다르게 동작)
     */
    public void swapRoles() {
        validateCanChangeRoles();

        if (mode == InterviewMode.SOLO_PRACTICE) {
            // 혼자 연습: UI에서 역할 모드만 전환
            return;
        }

        // 함께 연습: 두 사용자의 역할 교체
        swapInterviewerAndInterviewee();
    }

    /**
     * 면접관과 면접자 역할 교체
     */
    private void swapInterviewerAndInterviewee() {
        Long temp = interviewerId;
        this.interviewerId = intervieweeId;
        this.intervieweeId = temp;
    }

    /**
     * 역할 변경 가능 여부 검증
     */
    private void validateCanChangeRoles() {
        if (!status.canJoin()) {
            throw InterviewRoomException.cannotChangeRolesAfterStart();
        }
    }
}