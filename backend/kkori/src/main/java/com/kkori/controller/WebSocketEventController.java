package com.kkori.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.component.interview.QuestionForm;
import com.kkori.dto.QuestionDto;
import com.kkori.dto.RoomCreateDto;
import com.kkori.dto.STOMPErrorResponse;
import com.kkori.dto.STOMPMessage;
import com.kkori.dto.STTSuccessDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketEventController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private static final String INTERVIEW_ROOM_PATH = "/topic/interview/";

    private STOMPErrorResponse errorMessageCreator(String endpoint, String errorMessage) {
        return new STOMPErrorResponse(endpoint, errorMessage, System.currentTimeMillis());
    }

//    @MessageMapping("/room-create")
//    public STOMPMessage roomCreate(STOMPMessage message) {
//        System.out.println("[+] room-create : " + message);
//        RoomCreateDto roomCreateDto;
//        try {
//            roomCreateDto = objectMapper.readValue(message.getPayload(), RoomCreateDto.class);
//        } catch (JsonProcessingException e) {
//            //throw e;
//            e.printStackTrace();
//            STOMPMessage errorMessage = errorMessageCreator("/room-create", message, "wrong roomCreate payload");
//            simpMessagingTemplate.convertAndSendToUser(message.getUserId(), "/queue/interview", errorMessage);
//            return errorMessage;
//        }
//
//        RoomInfoDto roomInfoDto = new RoomInfoDto(); // service call with userInfo
//
//        String payload;
//        try {
//            payload = objectMapper.writeValueAsString(roomInfoDto);
//        } catch (JsonProcessingException e) {
//            //throw e;
//            e.printStackTrace();
//            STOMPMessage errorMessage = errorMessageCreator("/room-create", message, "wrong roomInfo payload");
//            simpMessagingTemplate.convertAndSendToUser(message.getUserId(), "/queue/interview", errorMessage);
//            return errorMessage;
//        }
//        message.setPayload(payload);
//        message.setType("room-created");
//        message.setTimestamp(String.valueOf(System.currentTimeMillis()));
//
//        simpMessagingTemplate.convertAndSendToUser(message.getUserId(), "/queue/interview", message);
//
//        return message;
//    }

    @MessageMapping("/room-create")
    public STOMPMessage roomCreate(@Payload RoomCreateDto roomCreate,
                                   SimpMessageHeaderAccessor accessor) { // @Header("Authorization") String token

        Long auth = (Long) accessor.getSessionAttributes().get("userId");
        String sessionId = accessor.getSessionId();
        System.out.println("[+] roomCreate : auth : " + auth);
//        accessor.getSessionAttributes()
//                .forEach((key, value) -> System.out.println("[+] room-create : key: " + key + " value: " + value));
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        simpMessagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/interview",
                roomCreate,
                headerAccessor.getMessageHeaders()
        );
        //simpMessagingTemplate.convertAndSend("/topic/broadcast", roomCreate);

        return new STOMPMessage();
    }


    @MessageMapping("/room-join")
    public STOMPMessage roomJoin(STOMPMessage message) {
        String sessionId = message.getSessionId();

        // service call

        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;
    }


    @MessageMapping("/interview-start")
    public STOMPMessage interviewStart(STOMPMessage message) {
        String sessionId = message.getSessionId();

        QuestionForm questionForm = null; // service call

        QuestionDto questionDto = objectMapper.convertValue(questionForm, QuestionDto.class);

        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(questionDto);
        } catch (JsonProcessingException e) {
            //return errorMessageCreator("interview-start", message, "wrong ")
            // 핸들링 고려 불필요할 듯(무조건 questionForm으로 리턴해주므로)
        }
        message.setType("interview-started");
        message.setPayload(payload);
        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;

    }


    @MessageMapping("/answer-start")
    public STOMPMessage answerStart(STOMPMessage message) {
        String sessionId = message.getSessionId();

        // start recording via native websocket

        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;
    }


    @MessageMapping("/answer-end")
    public STOMPMessage answerEnd(STOMPMessage message) {
        String sessionId = message.getSessionId();

        // end recording

        sttSuccessResponse(sessionId, message);

        nextQuestionsChoiceResponse(sessionId, message);

        return message;
    }


    private STOMPMessage nextQuestionsChoiceResponse(String sessionId, STOMPMessage message) {
        List<QuestionForm> nextQuestions = null; // service call - next questions

        List<QuestionDto> nextQuestionsDto = objectMapper.convertValue(
                nextQuestions,
                new TypeReference<List<QuestionDto>>() {
                }
        );

        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(nextQuestionsDto);
        } catch (JsonProcessingException e) {
            //return errorMessageCreator("interview-start", message, "wrong ")
            // 핸들링 고려 불필요할 듯(무조건 questionForm으로 리턴해주므로)
        }
        message.setType("next-questions-choice");
        message.setPayload(payload);
        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;
    }


    private STOMPMessage sttSuccessResponse(String sessionId, STOMPMessage message) {
        String answerText = null; // service call - stt request

        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(new STTSuccessDto(answerText));
        } catch (JsonProcessingException e) {
            //return errorMessageCreator("interview-start", message, "wrong ")
            // 핸들링 고려 불필요할 듯(무조건 questionForm으로 리턴해주므로)
        }
        message.setType("stt-success");
        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;
    }


    @MessageMapping("/next-question-chosen")
    public STOMPMessage nextQuestionChosen(STOMPMessage message) {
        String sessionId = message.getSessionId();

        // service call - set question

        message.setType("next-question");
        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;
    }


    @MessageMapping("/custom-question-start")
    public STOMPMessage customQuestionStart(STOMPMessage message) {
        String sessionId = message.getSessionId();

        // start recording

        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;
    }


    @MessageMapping("/custom-question-end")
    public STOMPMessage customQuestionEnd(STOMPMessage message) {
        String sessionId = message.getSessionId();

        // end recording

        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;
    }


    @MessageMapping("/interview-end")
    public STOMPMessage interviewEnd(STOMPMessage message) {
        String sessionId = message.getSessionId();

        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;
    }


    @MessageMapping("/exit")
    public STOMPMessage exitRoom(STOMPMessage message) {
        String sessionId = message.getSessionId();

        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSend(INTERVIEW_ROOM_PATH + sessionId, message);

        return message;
    }


    @MessageMapping("/room-status")
    public STOMPMessage roomStatus(STOMPMessage message) {
        String sessionId = message.getSessionId();

        String payload = null; // service call

        message.setPayload(payload);
        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        simpMessagingTemplate.convertAndSendToUser(message.getUserId(), "/queue/interview", message);

        return message;
    }

    //@MessageMapping("")

//    @MessageMapping("/chat") // 클라이언트 -> /app/event
//    //@SendTo("/topic/interview/{roomId}")   // 서버 -> 구독중인 모든 클라이언트
//    public String handleChat(@DestinationVariable String roomId,
//                             String message) {
//        // 동적으로 방 ID에 따라 브로커 경로 지정!
//        simpMessagingTemplate.convertAndSend("/topic/interview/" + roomId, message);
//        return message;
//    }
}
