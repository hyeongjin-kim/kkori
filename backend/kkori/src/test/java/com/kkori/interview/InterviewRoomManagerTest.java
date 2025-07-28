package com.kkori.interview;

import com.kkori.component.InterviewRoomManager;
import com.kkori.component.interview.InterviewMode;
import com.kkori.component.interview.InterviewRoom;
import com.kkori.component.interview.InterviewSession;
import com.kkori.component.interview.Permission;
import com.kkori.component.interview.RoomStatus;
import com.kkori.component.interview.UserRole;
import com.kkori.exception.interview.InterviewRoomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterviewRoomManager 테스트")
class InterviewRoomManagerTest {

    @InjectMocks
    private InterviewRoomManager roomManager;

    @Mock
    private InterviewSession mockSession;

    private final Long QUESTION_SET_ID = 1L;
    private final Long CREATOR_ID = 1L;
    private final Long USER_ID = 2L;

    @Nested
    @DisplayName("방 생성")
    class CreateRoomTest {

        @Test
        @DisplayName("혼자 연습하기 방 생성")
        void createSoloRoom() {
            // when
            String roomId = roomManager.createSoloRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);

            // then
            assertThat(roomId).hasSize(12);
            InterviewRoom room = roomManager.getRoom(roomId);
            assertThat(room.getMode()).isEqualTo(InterviewMode.SOLO_PRACTICE);
            assertThat(room.getCreatorId()).isEqualTo(CREATOR_ID);
        }

        @Test
        @DisplayName("함께 연습하기 방 생성")
        void createPairRoom() {
            // when
            String roomId = roomManager.createPairRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);

            // then
            InterviewRoom room = roomManager.getRoom(roomId);
            assertThat(room.getMode()).isEqualTo(InterviewMode.PAIR_INTERVIEW);
            assertThat(room.getCreatorId()).isEqualTo(CREATOR_ID);
        }
    }

    @Nested
    @DisplayName("방 참여/퇴장")
    class JoinExitTest {

        private String pairRoomId;

        @BeforeEach
        void setUp() {
            pairRoomId = roomManager.createPairRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);
        }

        @Test
        @DisplayName("방 참여 성공")
        void joinRoom() {
            // when
            roomManager.joinRoom(pairRoomId, USER_ID);

            // then
            InterviewRoom room = roomManager.getRoom(pairRoomId);
            assertThat(room.getUserIds()).containsExactlyInAnyOrder(CREATOR_ID, USER_ID);
        }

        @Test
        @DisplayName("정원 초과 시 참여 실패")
        void joinRoom_WhenFull() {
            // given
            roomManager.joinRoom(pairRoomId, USER_ID);

            // when & then
            assertThatThrownBy(() -> roomManager.joinRoom(pairRoomId, 3L))
                    .isInstanceOf(InterviewRoomException.class);
        }

        @Test
        @DisplayName("혼자 연습하기 방 참여 실패")
        void joinRoom_SoloMode() {
            // given
            String soloRoomId = roomManager.createSoloRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);

            // when & then
            assertThatThrownBy(() -> roomManager.joinRoom(soloRoomId, USER_ID))
                    .isInstanceOf(InterviewRoomException.class);
        }

        @Test
        @DisplayName("방장 퇴장 시 방 삭제")
        void exitRoom_CreatorLeaves() {
            // when
            roomManager.exitRoom(pairRoomId, CREATOR_ID);

            // then
            assertThat(roomManager.roomExists(pairRoomId)).isFalse();
        }

        @Test
        @DisplayName("면접 진행 중 퇴장 시 방 완료 후 삭제")
        void exitRoom_DuringInterview() {
            // given
            roomManager.joinRoom(pairRoomId, USER_ID);
            roomManager.startInterview(pairRoomId, 100L);

            // when
            roomManager.exitRoom(pairRoomId, USER_ID);

            // then
            assertThat(roomManager.roomExists(pairRoomId)).isFalse();
        }
    }

    @Nested
    @DisplayName("면접 라이프사이클")
    class InterviewLifecycleTest {

        private String pairRoomId;

        @BeforeEach
        void setUp() {
            pairRoomId = roomManager.createPairRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);
            roomManager.joinRoom(pairRoomId, USER_ID);
        }

        @Test
        @DisplayName("면접 시작")
        void startInterview() {
            // when
            roomManager.startInterview(pairRoomId, 100L);

            // then
            InterviewRoom room = roomManager.getRoom(pairRoomId);
            assertThat(room.getStatus()).isEqualTo(RoomStatus.STARTED);
            assertThat(room.getInterviewId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("면접 완료/진행 중 퇴장 → 방 삭제")
        void completeInterview() {
            // given
            roomManager.startInterview(pairRoomId, 100L);

            // when - 면접 완료
            roomManager.completeInterview(pairRoomId);

            // then
            assertThat(roomManager.roomExists(pairRoomId)).isFalse();

            // Note: exitRoom_DuringInterview 테스트에서 퇴장 시나리오도 같은 결과 확인
        }
    }

    @Nested
    @DisplayName("권한 및 역할")
    class PermissionTest {

        private String pairRoomId;

        @BeforeEach
        void setUp() {
            pairRoomId = roomManager.createPairRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);
            roomManager.joinRoom(pairRoomId, USER_ID);
        }

        @Test
        @DisplayName("사용자 역할 확인")
        void getUserRole() {
            // then
            assertThat(roomManager.getUserRole(pairRoomId, CREATOR_ID)).isEqualTo(UserRole.INTERVIEWEE);
            assertThat(roomManager.getUserRole(pairRoomId, USER_ID)).isEqualTo(UserRole.INTERVIEWER);
        }

        @Test
        @DisplayName("권한 확인")
        void hasPermission() {
            // then
            // 방장은 면접 시작 권한
            assertThat(roomManager.hasPermission(pairRoomId, CREATOR_ID, Permission.START_INTERVIEW)).isTrue();
            assertThat(roomManager.hasPermission(pairRoomId, USER_ID, Permission.START_INTERVIEW)).isFalse();

            // 면접자는 답변 제출 권한
            assertThat(roomManager.hasPermission(pairRoomId, CREATOR_ID, Permission.SUBMIT_ANSWER)).isTrue();

            // 면접관은 질문 관리 권한
            assertThat(roomManager.hasPermission(pairRoomId, USER_ID, Permission.CREATE_CUSTOM_QUESTION)).isTrue();
        }

        @Test
        @DisplayName("역할 변경")
        void swapRoles() {
            // when
            roomManager.swapRoles(pairRoomId);

            // then
            assertThat(roomManager.getUserRole(pairRoomId, CREATOR_ID)).isEqualTo(UserRole.INTERVIEWER);
            assertThat(roomManager.getUserRole(pairRoomId, USER_ID)).isEqualTo(UserRole.INTERVIEWEE);
        }
    }

    @Nested
    @DisplayName("조회 기능")
    class QueryTest {

        @Test
        @DisplayName("사용자별 방 찾기")
        void findRoomByUser() {
            // given
            String roomId = roomManager.createSoloRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);

            // when & then
            assertThat(roomManager.findRoomByUser(CREATOR_ID)).isEqualTo(roomId);
            assertThat(roomManager.findRoomByUser(99L)).isNull();
        }

        @Test
        @DisplayName("참여 가능 여부 확인")
        void canJoinRoom() {
            // given
            String pairRoomId = roomManager.createPairRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);

            // then
            assertThat(roomManager.canJoinRoom(pairRoomId)).isTrue();

            // 정원 초과 후
            roomManager.joinRoom(pairRoomId, USER_ID);
            assertThat(roomManager.canJoinRoom(pairRoomId)).isFalse();
        }

        @Test
        @DisplayName("면접 시작 가능 여부 확인")
        void canStartInterview() {
            // given
            String soloRoomId = roomManager.createSoloRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);
            String pairRoomId = roomManager.createPairRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);

            // then
            assertThat(roomManager.canStartInterview(soloRoomId)).isTrue();
            assertThat(roomManager.canStartInterview(pairRoomId)).isFalse(); // 1명만 있음

            roomManager.joinRoom(pairRoomId, USER_ID);
            assertThat(roomManager.canStartInterview(pairRoomId)).isTrue(); // 2명 완성
        }

        @Test
        @DisplayName("활성 방 개수 조회")
        void getActiveRoomCount() {
            // given
            roomManager.createSoloRoom(QUESTION_SET_ID, CREATOR_ID, mockSession);
            roomManager.createPairRoom(QUESTION_SET_ID, USER_ID, mockSession);

            // then
            assertThat(roomManager.getActiveRoomCount()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("존재하지 않는 방 조회 시 예외")
    void getRoom_NotFound() {
        assertThatThrownBy(() -> roomManager.getRoom("INVALID"))
                .isInstanceOf(InterviewRoomException.class);
    }
}