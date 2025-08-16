package com.kkori.controller;

import com.kkori.component.interview.InterviewRoom;
import com.kkori.component.interview.QuestionForm;
import com.kkori.dto.interview.QuestionDto;
import com.kkori.dto.interview.request.AnswerSubmitRequest;
import com.kkori.dto.interview.request.CommonRoomRequest;
import com.kkori.dto.interview.request.CustomQuestionCreateRequest;
import com.kkori.dto.interview.request.NextQuestionSelectRequest;
import com.kkori.dto.interview.request.RoomCreateRequest;
import com.kkori.dto.interview.response.ExistingUserResponse;
import com.kkori.dto.interview.response.InterviewStartResponse;
import com.kkori.dto.interview.response.JoinedUserResponse;
import com.kkori.dto.interview.response.NextQuestionChoicesResponse;
import com.kkori.dto.interview.response.RoomCreateResponse;
import com.kkori.dto.interview.response.RoomReconnectionResponse;
import com.kkori.dto.interview.response.RoomStatusResponse;
import com.kkori.dto.interview.response.STTResultResponse;
import com.kkori.dto.interview.response.SuccessResponse;
import com.kkori.entity.User;
import com.kkori.exception.ExceptionCode;
import com.kkori.exception.interview.InterviewRoomException;
import com.kkori.message.InterviewMessages;
import com.kkori.service.InterviewSessionService;
import com.kkori.service.UserService;
import com.kkori.util.WebSocketHelper;
import com.kkori.component.interview.UserLastEventStore;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * 면접 실시간 WebSocket 이벤트 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class InterviewWebSocketController {

    private final InterviewSessionService interviewSessionService;
    private final UserService userService;
    private final WebSocketHelper webSocketHelper;
    private final UserLastEventStore userLastEventStore;

    // ==================== 방 관리 ====================

    @MessageMapping("/room-create")
    public void handleRoomCreate(@Payload RoomCreateRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = createRoom(request, authenticatedUserId);
            RoomCreateResponse response = new RoomCreateResponse(roomId);
            webSocketHelper.sendPersonalMessage(authenticatedUserId, "room-created", response);
        } catch (Exception e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, ExceptionCode.ROOM_CREATE_FAILED, e.getMessage());
        }
    }

    @MessageMapping("/room-join")
    public void handleRoomJoin(@Payload CommonRoomRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = request.getRoomId();

            if (!isReconnection(roomId, authenticatedUserId)) {

                interviewSessionService.joinRoom(roomId, authenticatedUserId);

                InterviewRoom interviewRoom = interviewSessionService.getRoom(roomId);

                sendJoinMessage(interviewRoom.getCreatorId(), authenticatedUserId);
            }

        } catch (Exception e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, ExceptionCode.ROOM_JOIN_FAILED, e.getMessage());
        }
    }

    @MessageMapping("/room-exit")
    public void handleRoomExit(@Payload CommonRoomRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = request.getRoomId();
            interviewSessionService.exitRoom(roomId, authenticatedUserId);

            SuccessResponse broadcastResponse = new SuccessResponse(
                    InterviewMessages.USER_EXITED
            );
            webSocketHelper.broadcastToRoom(roomId, "user-exited", broadcastResponse);

        } catch (Exception e) {
            // 방 나가기는 항상 성공으로 처리
        }
    }

    @MessageMapping("/room-status")
    public void handleRoomStatus(@Payload CommonRoomRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = request.getRoomId();

            // TODO: 실제 방 상태 조회 로직 구현 (필요하다면)
            RoomStatusResponse response = new RoomStatusResponse("WAITING", 1, 2);

            webSocketHelper.sendPersonalMessage(authenticatedUserId, "room-status", response);

        } catch (Exception e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, ExceptionCode.ROOM_STATUS_FAILED, e.getMessage());
        }
    }

    @MessageMapping("/roles-swap")
    public void handleRolesSwap(@Payload CommonRoomRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = request.getRoomId();
            interviewSessionService.swapRoles(roomId);

            SuccessResponse response = new SuccessResponse(
                    InterviewMessages.ROLES_SWAPPED
            );
            webSocketHelper.broadcastToRoom(roomId, "roles-swapped", response);

        } catch (Exception e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, ExceptionCode.ROLE_SWAP_FAILED, e.getMessage());
        }
    }

    // ==================== 면접 진행 ====================

    @MessageMapping("/interview-start")
    public void handleInterviewStart(@Payload CommonRoomRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = request.getRoomId();

            // 면접 시작 및 저장
            interviewSessionService.startInterview(roomId, authenticatedUserId);

            // 첫 번째 질문 조회
            QuestionForm currentQuestion = interviewSessionService.getCurrentQuestion(roomId);
            QuestionDto questionDto = QuestionDto.from(currentQuestion);

            InterviewStartResponse response = new InterviewStartResponse(questionDto);
            webSocketHelper.broadcastToRoom(roomId, "interview-started", response);

        } catch (Exception e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, ExceptionCode.INTERVIEW_START_FAILED, e.getMessage());
        }
    }

    @MessageMapping("/interview-end")
    public void handleInterviewEnd(@Payload CommonRoomRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = request.getRoomId();

            SuccessResponse response = new SuccessResponse(
                    InterviewMessages.INTERVIEW_COMPLETED
            );
            webSocketHelper.broadcastToRoom(roomId, "interview-ended", response);

            interviewSessionService.completeInterview(roomId);

        } catch (Exception e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, ExceptionCode.INTERVIEW_END_FAILED, e.getMessage());
        }
    }

    @MessageMapping("/interview-status")
    public void handleUserStatus(@Payload String newStatus, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            // UserLastEventStore에 상태 저장 (transitional 상태는 자동으로 필터링됨)
            userLastEventStore.updateUserStatus(authenticatedUserId, newStatus);
        } catch (Exception e) {
            // 상태 업데이트 실패는 조용히 처리 (중요하지 않은 기능)
        }
    }

    // ==================== 답변 처리 ====================

    @MessageMapping("/answer-start")
    public void handleAnswerStart(@Payload CommonRoomRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        String roomId = request.getRoomId();
        SuccessResponse response = new SuccessResponse(
                InterviewMessages.ANSWER_RECORDING_STARTED
        );
        webSocketHelper.broadcastToRoom(roomId, "answer-recording-started", response);
    }

    @MessageMapping("/answer-submit")
    public void handleAnswerSubmit(@Payload AnswerSubmitRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = request.getRoomId();
            String audioBase64 = request.getAudioBase64();

            // 1단계: STT 처리 및 답변 저장
            String transcribedText = interviewSessionService.processAudioAnswer(
                    roomId, authenticatedUserId, audioBase64);

            // 1단계: STT 결과를 방 전체에 먼저 브로드캐스트
            STTResultResponse sttResponse = new STTResultResponse(transcribedText);
            webSocketHelper.broadcastToRoom(roomId, "stt-result", sttResponse);

            // 2단계: 방 정보를 가져와서 면접관 확인 후 질문 선택지를 면접관에게만 개인 메시지로 전송
            InterviewRoom room = interviewSessionService.getRoom(roomId);
            Long interviewerId = room.getInterviewerId();

            if (interviewerId != null) {
                List<QuestionForm> nextQuestions = interviewSessionService.getNextQuestions(roomId);
                List<QuestionDto> questionDtoList = nextQuestions.stream()
                        .map(QuestionDto::from)
                        .toList();

                NextQuestionChoicesResponse choicesResponse = new NextQuestionChoicesResponse(questionDtoList);
                webSocketHelper.sendPersonalMessage(interviewerId, "next-question-choices", choicesResponse);
            }

        } catch (Exception e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, ExceptionCode.ANSWER_PROCESSING_FAILED,
                    e.getMessage());
        }
    }

    // ==================== 질문 관리 ====================

    @MessageMapping("/next-question-select")
    public void handleNextQuestionSelect(@Payload NextQuestionSelectRequest request,
                                         SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = request.getRoomId();

            QuestionForm selectedQuestion = interviewSessionService.selectQuestion(
                    roomId,
                    com.kkori.component.interview.QuestionType.valueOf(request.getQuestionType()),
                    request.getQuestionId(),
                    request.getQuestionText());

            QuestionDto questionDto = QuestionDto.from(selectedQuestion);
            webSocketHelper.broadcastToRoom(roomId, "next-question-selected", questionDto);

        } catch (Exception e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, ExceptionCode.QUESTION_SELECT_FAILED, e.getMessage());
        }
    }

    @MessageMapping("/custom-question-start")
    public void handleCustomQuestionStart(@Payload CommonRoomRequest request,
                                          SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        String roomId = request.getRoomId();
        SuccessResponse response = new SuccessResponse(
                InterviewMessages.CUSTOM_QUESTION_RECORDING_STARTED
        );
        webSocketHelper.broadcastToRoom(roomId, "custom-question-recording-started", response);
    }

    @MessageMapping("/custom-question-create")
    public void handleCustomQuestionCreate(@Payload CustomQuestionCreateRequest request,
                                           SimpMessageHeaderAccessor headerAccessor) {
        Long authenticatedUserId = webSocketHelper.requireAuthenticatedUserId(headerAccessor);

        try {
            String roomId = request.getRoomId();
            String audioBase64 = request.getAudioBase64();

            QuestionForm customQuestion = interviewSessionService.createCustomQuestion(
                    roomId, authenticatedUserId, audioBase64);

            QuestionDto questionDto = QuestionDto.from(customQuestion);
            webSocketHelper.broadcastToRoom(roomId, "custom-question-created", questionDto);

        } catch (Exception e) {
            webSocketHelper.sendErrorToUser(authenticatedUserId, ExceptionCode.AUDIO_TRANSCRIPTION_FAILED.getMessage(),
                    e.getMessage());
        }
    }

    // ==================== 헬퍼 메서드 ====================

    private String createRoom(RoomCreateRequest request, Long userId) {
        return switch (request.getMode()) {
            case "SOLO_PRACTICE" -> interviewSessionService.createSoloRoom(request.getQuestionSetId(), userId);
            case "PAIR_INTERVIEW" -> interviewSessionService.createPairRoom(request.getQuestionSetId(), userId);
            default -> throw new IllegalArgumentException("지원하지 않는 모드: " + request.getMode());
        };
    }

    private void sendJoinMessage(Long creatorId, Long participantId) {
        User creator = userService.findById(creatorId);
        User participant = userService.findById(participantId);

        ExistingUserResponse toParticipantResponse = new ExistingUserResponse(creator.getNickname());
        JoinedUserResponse toCreatorResponse = new JoinedUserResponse(participant.getNickname());

        webSocketHelper.sendPersonalMessage(participantId, "existing-user", toParticipantResponse);
        webSocketHelper.sendPersonalMessage(creatorId, "joined-user", toCreatorResponse);
    }

    private boolean isReconnection(String roomId, Long userId) {
        if(interviewSessionService.isReconnection(roomId, userId)){
            // 중복 탭 접근 시 기존 탭에 알림 전송
            webSocketHelper.sendPersonalMessage(userId, "disconnect", "다른 곳에서 접속하여 연결을 해제합니다.");
            handleReconnection(userId);
            return true;
        }

        if (roomId == null) {
            handleReconnection(userId);
            return true;
        }
        
        return false;
    }

    private void handleReconnection(Long userId) {
        try {
            String roomId = interviewSessionService.getRoomIdByUserId(userId);

            RoomReconnectionResponse response = new RoomReconnectionResponse(roomId);
            webSocketHelper.sendPersonalMessage(userId, "room-reconnected", response);

            webSocketHelper.sendLastStatusToUser(userId);
            webSocketHelper.sendLastEventToUser(userId);
        } catch (InterviewRoomException e) {
            webSocketHelper.sendErrorToUser(userId, e.getExceptionCode());
        }
    }

}