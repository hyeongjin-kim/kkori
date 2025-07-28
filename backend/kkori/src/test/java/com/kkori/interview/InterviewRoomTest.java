package com.kkori.interview;

import com.kkori.dto.InterviewRoom;
import com.kkori.dto.InterviewSession;
import com.kkori.entity.InterviewMode;
import com.kkori.entity.Permission;
import com.kkori.entity.RoomStatus;
import com.kkori.entity.UserRole;
import com.kkori.exception.interview.InterviewRoomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InterviewRoomTest {

    private InterviewSession mockSession;
    private final String ROOM_ID = "TEST_ROOM_123";
    private final String PAIR_ROOM_ID = "PAIR_ROOM_456";
    private final Long QUESTION_SET_ID = 1L;
    private final String CREATOR_ID = "creator123";
    private final String INTERVIEWER_ID = "interviewer456";

    @BeforeEach
    void setUp() {
        mockSession = new InterviewSession();
    }

    @Nested
    @DisplayName("혼자 연습하기 모드")
    class SoloModeTest {

        private InterviewRoom soloRoom;

        @BeforeEach
        void setUp() {
            soloRoom = InterviewRoom.createSoloRoom(ROOM_ID, QUESTION_SET_ID, CREATOR_ID, mockSession);
        }

        @Test
        @DisplayName("방 생성 시 기본 설정이 올바르게 적용된다")
        void createSoloRoom_Success_WithCorrectDefaults() {
            // then
            assertThat(soloRoom.getRoomId()).isEqualTo(ROOM_ID);
            assertThat(soloRoom.getQuestionSetId()).isEqualTo(QUESTION_SET_ID);
            assertThat(soloRoom.getCreatorId()).isEqualTo(CREATOR_ID);
            assertThat(soloRoom.getStatus()).isEqualTo(RoomStatus.WAITING);
            assertThat(soloRoom.getMode()).isEqualTo(InterviewMode.SOLO_PRACTICE);
            assertThat(soloRoom.getIntervieweeId()).isEqualTo(CREATOR_ID); // 방장은 면접자
            assertThat(soloRoom.getInterviewerId()).isEqualTo(CREATOR_ID); // 방장은 면접관도!
            assertThat(soloRoom.getSession()).isEqualTo(mockSession);
            assertThat(soloRoom.isSoloPractice()).isTrue();
        }

        @Test
        @DisplayName("방장이 두 역할 모두 가지고 면접 시작 성공")
        void startInterview_Success_CreatorHasBothRoles() {
            // given
            Long interviewId = 100L;

            // when
            soloRoom.startInterview(interviewId);

            // then
            assertThat(soloRoom.getStatus()).isEqualTo(RoomStatus.STARTED);
            assertThat(soloRoom.getInterviewId()).isEqualTo(interviewId);
            assertThat(soloRoom.isStarted()).isTrue();
            assertThat(soloRoom.getInterviewerId()).isEqualTo(CREATOR_ID);
            assertThat(soloRoom.getIntervieweeId()).isEqualTo(CREATOR_ID);
        }

        @Test
        @DisplayName("면접 완료 성공")
        void completeInterview_Success() {
            // given
            soloRoom.startInterview(100L);

            // when
            soloRoom.completeInterview();

            // then
            assertThat(soloRoom.getStatus()).isEqualTo(RoomStatus.COMPLETED);
            assertThat(soloRoom.isCompleted()).isTrue();
            assertThat(soloRoom.isStarted()).isFalse();
        }

        @Test
        @DisplayName("대기 중 방장이 나가면 방이 종료된다")
        void removeUser_CreatorLeaves_DuringWaiting() {
            // given - 대기 상태 (기본 상태)
            assertThat(soloRoom.getStatus()).isEqualTo(RoomStatus.WAITING);

            // when
            soloRoom.removeUser(CREATOR_ID);

            // then
            assertThat(soloRoom.getStatus()).isEqualTo(RoomStatus.COMPLETED); // 방 종료
            assertThat(soloRoom.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("면접 진행 중 방장이 나가면 면접이 자동 종료된다")
        void removeUser_CreatorLeaves_DuringInterview() {
            // given
            soloRoom.startInterview(100L); // 면접 시작

            // when
            soloRoom.removeUser(CREATOR_ID);

            // then
            assertThat(soloRoom.getIntervieweeId()).isNull();
            assertThat(soloRoom.getInterviewerId()).isNull();
            assertThat(soloRoom.getStatus()).isEqualTo(RoomStatus.COMPLETED); // 자동 종료
            assertThat(soloRoom.isCompleted()).isTrue();
            assertThat(soloRoom.isStarted()).isFalse();
        }
    }

    @Nested
    @DisplayName("함께 연습하기 모드")
    class PairModeTest {

        private InterviewRoom pairRoom;

        @BeforeEach
        void setUp() {
            pairRoom = InterviewRoom.createPairRoom(PAIR_ROOM_ID, QUESTION_SET_ID, CREATOR_ID, mockSession);
        }

        @Test
        @DisplayName("방 생성 시 기본 설정이 올바르게 적용된다")
        void createPairRoom_Success_WithCorrectDefaults() {
            // then
            assertThat(pairRoom.getRoomId()).isEqualTo(PAIR_ROOM_ID);
            assertThat(pairRoom.getQuestionSetId()).isEqualTo(QUESTION_SET_ID);
            assertThat(pairRoom.getCreatorId()).isEqualTo(CREATOR_ID);
            assertThat(pairRoom.getStatus()).isEqualTo(RoomStatus.WAITING);
            assertThat(pairRoom.getMode()).isEqualTo(InterviewMode.PAIR_INTERVIEW);
            assertThat(pairRoom.getIntervieweeId()).isEqualTo(CREATOR_ID); // 방장은 면접자
            assertThat(pairRoom.getInterviewerId()).isNull(); // 면접관은 아직 없음
            assertThat(pairRoom.getSession()).isEqualTo(mockSession);
            assertThat(pairRoom.isSoloPractice()).isFalse();
        }

        @Test
        @DisplayName("면접관으로 참여 성공")
        void addUser_Success() {
            // when
            pairRoom.addUser(INTERVIEWER_ID);

            // then
            assertThat(pairRoom.getInterviewerId()).isEqualTo(INTERVIEWER_ID);
            assertThat(pairRoom.getUserRole(INTERVIEWER_ID)).isEqualTo(UserRole.INTERVIEWER);
            assertThat(pairRoom.getUserIds()).containsExactlyInAnyOrder(CREATOR_ID, INTERVIEWER_ID);
        }

        @Test
        @DisplayName("방 정원 초과 시 참여 실패 (최대 2명)")
        void addUser_Fail_WhenRoomIsFull() {
            // given - 이미 방장(면접자) + 면접관으로 2명 완성
            pairRoom.addUser(INTERVIEWER_ID);

            // when & then - 3번째 사람 참여 시도
            assertThatThrownBy(() -> pairRoom.addUser("third_user"))
                    .isInstanceOf(InterviewRoomException.class);

            assertThat(pairRoom.getInterviewerId()).isEqualTo(INTERVIEWER_ID);
            assertThat(pairRoom.getUserIds()).hasSize(2); // 여전히 2명
        }

        @Test
        @DisplayName("면접관과 면접자가 모두 있을 때 면접 시작 성공")
        void startInterview_Success_WhenBothRolesPresent() {
            // given
            pairRoom.addUser(INTERVIEWER_ID);
            Long interviewId = 100L;

            // when
            pairRoom.startInterview(interviewId);

            // then
            assertThat(pairRoom.getStatus()).isEqualTo(RoomStatus.STARTED);
            assertThat(pairRoom.getInterviewId()).isEqualTo(interviewId);
            assertThat(pairRoom.isStarted()).isTrue();
        }

        @Test
        @DisplayName("면접관이 없을 때 면접 시작 실패")
        void startInterview_Fail_WhenInterviewerMissing() {
            // given - 면접자만 있고 면접관 없음
            Long interviewId = 100L;

            // when & then
            assertThatThrownBy(() -> pairRoom.startInterview(interviewId))
                    .isInstanceOf(InterviewRoomException.class);

            assertThat(pairRoom.getStatus()).isEqualTo(RoomStatus.WAITING);
            assertThat(pairRoom.isStarted()).isFalse();
        }

        @Test
        @DisplayName("면접 시작 후에는 사용자 참여 불가")
        void addUser_Fail_WhenInterviewStarted() {
            // given
            pairRoom.addUser(INTERVIEWER_ID);
            pairRoom.startInterview(100L);

            // when & then
            assertThatThrownBy(() -> pairRoom.addUser("new_user"))
                    .isInstanceOf(InterviewRoomException.class);
        }

        @Test
        @DisplayName("면접 시작 전에는 역할 변경 가능")
        void swapRoles_Success_BeforeInterviewStart() {
            // given
            pairRoom.addUser(INTERVIEWER_ID);

            // when
            pairRoom.swapRoles();

            // then
            assertThat(pairRoom.getInterviewerId()).isEqualTo(CREATOR_ID);
            assertThat(pairRoom.getIntervieweeId()).isEqualTo(INTERVIEWER_ID);
            assertThat(pairRoom.getUserRole(CREATOR_ID)).isEqualTo(UserRole.INTERVIEWER);
            assertThat(pairRoom.getUserRole(INTERVIEWER_ID)).isEqualTo(UserRole.INTERVIEWEE);
        }

        @Test
        @DisplayName("면접 시작 후에는 역할 변경 불가")
        void swapRoles_Fail_AfterInterviewStart() {
            // given
            pairRoom.addUser(INTERVIEWER_ID);
            pairRoom.startInterview(100L);

            // when & then
            assertThatThrownBy(() -> pairRoom.swapRoles())
                    .isInstanceOf(InterviewRoomException.class);

            // 역할은 그대로 유지
            assertThat(pairRoom.getInterviewerId()).isEqualTo(INTERVIEWER_ID);
            assertThat(pairRoom.getIntervieweeId()).isEqualTo(CREATOR_ID);
        }

        @Test
        @DisplayName("면접관 퇴장 시 면접관 ID가 null로 설정된다")
        void removeUser_InterviewerLeaves() {
            // given
            pairRoom.addUser(INTERVIEWER_ID);

            // when
            pairRoom.removeUser(INTERVIEWER_ID);

            // then
            assertThat(pairRoom.getInterviewerId()).isNull();
            assertThat(pairRoom.getUserRole(INTERVIEWER_ID)).isNull();
            assertThat(pairRoom.getUserIds()).containsExactly(CREATOR_ID);
        }

        @Test
        @DisplayName("대기 중 면접자(방장) 퇴장 시 방이 종료된다")
        void removeUser_CreatorLeaves_DuringWaiting() {
            // given - 대기 상태 (기본 상태)
            assertThat(pairRoom.getStatus()).isEqualTo(RoomStatus.WAITING);

            // when
            pairRoom.removeUser(CREATOR_ID);

            // then
            assertThat(pairRoom.getStatus()).isEqualTo(RoomStatus.COMPLETED); // 방 종료
            assertThat(pairRoom.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("대기 중 면접관 퇴장 시 면접관만 제거됨")
        void removeUser_InterviewerLeaves_DuringWaiting() {
            // given
            pairRoom.addUser(INTERVIEWER_ID);
            assertThat(pairRoom.getStatus()).isEqualTo(RoomStatus.WAITING);

            // when
            pairRoom.removeUser(INTERVIEWER_ID);

            // then
            assertThat(pairRoom.getInterviewerId()).isNull();
            assertThat(pairRoom.getStatus()).isEqualTo(RoomStatus.WAITING); // 대기 상태 유지
            assertThat(pairRoom.getIntervieweeId()).isEqualTo(CREATOR_ID); // 방장은 그대로
        }

        @Test
        @DisplayName("면접 진행 중 면접관 퇴장 시 면접이 자동 종료된다")
        void removeUser_InterviewerLeaves_DuringInterview() {
            // given
            pairRoom.addUser(INTERVIEWER_ID);
            pairRoom.startInterview(100L); // 면접 시작

            // when
            pairRoom.removeUser(INTERVIEWER_ID);

            // then
            assertThat(pairRoom.getInterviewerId()).isNull();
            assertThat(pairRoom.getStatus()).isEqualTo(RoomStatus.COMPLETED); // 자동 종료
            assertThat(pairRoom.isCompleted()).isTrue();
            assertThat(pairRoom.isStarted()).isFalse();
        }

        @Test
        @DisplayName("면접 진행 중 면접자 퇴장 시 면접이 자동 종료된다")
        void removeUser_IntervieweeLeaves_DuringInterview() {
            // given
            pairRoom.addUser(INTERVIEWER_ID);
            pairRoom.startInterview(100L); // 면접 시작

            // when
            pairRoom.removeUser(CREATOR_ID); // 면접자(방장) 퇴장

            // then
            assertThat(pairRoom.getIntervieweeId()).isNull();
            assertThat(pairRoom.getStatus()).isEqualTo(RoomStatus.COMPLETED); // 자동 종료
            assertThat(pairRoom.isCompleted()).isTrue();
            assertThat(pairRoom.isStarted()).isFalse();
        }
    }

    @Nested
    @DisplayName("공통 권한 관리")
    class PermissionTest {

        private InterviewRoom pairRoom;

        @BeforeEach
        void setUp() {
            pairRoom = InterviewRoom.createPairRoom(PAIR_ROOM_ID, QUESTION_SET_ID, CREATOR_ID, mockSession);
            pairRoom.addUser(INTERVIEWER_ID);
        }

        @Test
        @DisplayName("면접자는 답변 제출 권한이 있다")
        void hasPermission_IntervieweeCanSubmitAnswer() {
            assertThat(pairRoom.hasPermission(CREATOR_ID, Permission.SUBMIT_ANSWER)).isTrue();
        }

        @Test
        @DisplayName("면접관은 답변 제출 권한이 없다")
        void hasPermission_InterviewerCannotSubmitAnswer() {
            assertThat(pairRoom.hasPermission(INTERVIEWER_ID, Permission.SUBMIT_ANSWER)).isFalse();
        }

        @Test
        @DisplayName("방장은 면접 시작/종료 권한이 있다")
        void hasPermission_CreatorCanStartAndEndInterview() {
            assertThat(pairRoom.hasPermission(CREATOR_ID, Permission.START_INTERVIEW)).isTrue();
            assertThat(pairRoom.hasPermission(CREATOR_ID, Permission.END_INTERVIEW)).isTrue();
        }

        @Test
        @DisplayName("면접관은 면접 시작/종료 권한이 없다 (방장만 가능)")
        void hasPermission_InterviewerCannotStartOrEndInterview() {
            assertThat(pairRoom.hasPermission(INTERVIEWER_ID, Permission.START_INTERVIEW)).isFalse();
            assertThat(pairRoom.hasPermission(INTERVIEWER_ID, Permission.END_INTERVIEW)).isFalse();
        }

        @Test
        @DisplayName("면접관은 질문 관리 권한이 있다")
        void hasPermission_InterviewerCanManageQuestions() {
            assertThat(pairRoom.hasPermission(INTERVIEWER_ID, Permission.CREATE_CUSTOM_QUESTION)).isTrue();
            assertThat(pairRoom.hasPermission(INTERVIEWER_ID, Permission.CHOOSE_NEXT_QUESTION)).isTrue();
            assertThat(pairRoom.hasPermission(INTERVIEWER_ID, Permission.CHANGE_ROLES)).isTrue();
        }

        @Test
        @DisplayName("방에 없는 사용자는 모든 권한이 없다")
        void hasPermission_UnknownUserHasNoPermission() {
            assertThat(pairRoom.hasPermission("unknown_user", Permission.SUBMIT_ANSWER)).isFalse();
            assertThat(pairRoom.hasPermission("unknown_user", Permission.START_INTERVIEW)).isFalse();
        }
    }
}