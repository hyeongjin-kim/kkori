package com.kkori.controller;

import com.kkori.annotation.LoginUser;
import com.kkori.common.CommonApiResponse;
import com.kkori.component.interview.InterviewRoom;
import com.kkori.component.interview.QuestionForm;
import com.kkori.dto.interview.QuestionDto;
import com.kkori.dto.interview.response.NextQuestionChoicesResponse;
import com.kkori.dto.interview.response.STTResultResponse;
import com.kkori.service.InterviewSessionService;
import com.kkori.util.WebSocketHelper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewHttpController {

    private final InterviewSessionService interviewSessionService;
    private final WebSocketHelper webSocketHelper;

    /**
     * HTTP를 통한 답변 제출 (파일 업로드)
     *
     * @param userId    인증된 사용자 ID
     * @param roomId    방 ID
     * @param audioFile 오디오 파일
     * @return 처리 결과
     */
    @PostMapping("/answer-submit")
    public ResponseEntity<CommonApiResponse<String>> submitAnswer(
            @LoginUser Long userId,
            @RequestParam String roomId,
            @RequestParam("audioFile") MultipartFile audioFile) {

        try {
            // 파일 바이트 배열로 직접 처리 (Base64 변환 불필요)
            byte[] audioBytes = audioFile.getBytes();

            // STT 처리 및 답변 저장
            String transcribedText = interviewSessionService.processAudioAnswer(
                    roomId, userId, audioBytes);

            // WebSocket을 통해 STT 결과 브로드캐스트
            STTResultResponse sttResponse = new STTResultResponse(transcribedText);
            webSocketHelper.broadcastToRoom(roomId, "stt-result", sttResponse);

            // 면접관에게 다음 질문 선택지 전송
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

            System.out.println(transcribedText);

            return ResponseEntity.ok(CommonApiResponse.ok(transcribedText, "답변이 성공적으로 처리되었습니다."));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonApiResponse.fail(400, "답변 처리에 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/api/custom-question-create")
    public ResponseEntity<CommonApiResponse<String>> createCustomQuestion(
            @LoginUser Long userId,
            @RequestParam String roomId,
            @RequestParam("audioFile") MultipartFile audioFile
    ) {
        try {
            // 파일 바이트 배열로 직접 처리 (Base64 변환 불필요)
            byte[] audioBytes = audioFile.getBytes();

            // STT 처리 및 답변 저장
            QuestionForm customQuestion = interviewSessionService.createCustomQuestion(
                    roomId, userId, audioBytes);

            QuestionDto questionDto = QuestionDto.from(customQuestion);
            webSocketHelper.broadcastToRoom(roomId, "custom-question-created", questionDto);

            System.out.println("===Custom Question Created===");

            return ResponseEntity.ok(CommonApiResponse.ok(questionDto.getQuestionText(), "답변이 성공적으로 처리되었습니다."));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonApiResponse.fail(400, "커스텀 질문 생성에 실패했습니다: " + e.getMessage()));
        }
    }
}