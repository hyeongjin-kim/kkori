package com.kkori.component.interview;

import lombok.Getter;

@Getter
public enum RoomStatus {
    WAITING, STARTED, COMPLETED;

    /**
     * 방에 참여할 수 있는 상태인지 확인
     */
    public boolean canJoin() {
        return this == WAITING;
    }

    /**
     * 면접을 시작할 수 있는 상태인지 확인
     */
    public boolean canStart() {
        return this == WAITING;
    }

    /**
     * 면접이 진행 중인지 확인
     */
    public boolean isInProgress() {
        return this == STARTED;
    }

    /**
     * 면접이 완료되었는지 확인
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
