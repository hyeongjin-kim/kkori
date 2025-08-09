package com.kkori.test.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.dto.interview.request.RoomCreateRequest;
import com.kkori.dto.interview.response.RoomCreateResponse;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@Component
public class WebSocketTestHelper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 실제 JWT 쿠키와 함께 WebSocket 세션 생성
     */
    public StompSession createRealTestSession(int port, String jwtToken, Long expectedUserId) throws Exception {
        System.out.println("=== Starting REAL WebSocket Connection Test ===");
        System.out.println("📋 JWT Token: " + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
        System.out.println("📋 Expected User ID: " + expectedUserId);

        // SockJS 클라이언트 설정
        List<Transport> transports = Arrays.asList(
                new WebSocketTransport(new StandardWebSocketClient()),
                new RestTemplateXhrTransport()
        );

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        // 메시지 변환기 설정
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(messageConverter);

        String url = "http://localhost:" + port + "/ws";
        System.out.println("Connecting to: " + url);

        // ✅ JWT 쿠키가 포함된 헤더 설정
        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        httpHeaders.add("Cookie", "accessToken=" + jwtToken);

        StompHeaders stompHeaders = new StompHeaders();

        System.out.println("📤 연결 헤더 설정 완료");
        System.out.println(
                "📤 Cookie 헤더: accessToken=" + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");

        StompSessionHandler sessionHandler = new RealTestStompSessionHandler();

        try {
            StompSession session = stompClient.connect(url, httpHeaders, stompHeaders, sessionHandler)
                    .get(15, TimeUnit.SECONDS);

            System.out.println("✅ REAL WebSocket connection successful!");
            System.out.println("📋 Session ID: " + session.getSessionId());

            return session;
        } catch (Exception e) {
            System.err.println("❌ REAL WebSocket connection failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 실제 운영환경과 동일한 개인 메시지 구독
     */
    public MessageSubscriber subscribeToRealPersonalQueue(StompSession session, Long userId) throws Exception {
        BlockingQueue<Map<String, Object>> messages = new LinkedBlockingQueue<>();

        String destination = "/user/queue/interview";
        System.out.println("📬 실제 개인 큐 구독: " + destination + " (사용자: " + userId + ")");

        StompSession.Subscription subscription = session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Object.class;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("📨🎯 === 실제 개인 메시지 수신 ===");
                System.out.println("📨 사용자 ID: " + userId);
                System.out.println("📨 Payload type: " + (payload != null ? payload.getClass() : "null"));

                try {
                    Map<String, Object> message = null;

                    if (payload instanceof Map) {
                        message = (Map<String, Object>) payload;
                        System.out.println("📨 ✅ Direct Map: " + message);
                    } else if (payload instanceof String) {
                        message = objectMapper.readValue((String) payload, Map.class);
                        System.out.println("📨 ✅ Parsed from String: " + message);
                    } else if (payload instanceof byte[]) {
                        // ✅ 바이트 배열 처리 추가
                        byte[] bytes = (byte[]) payload;
                        String jsonString = new String(bytes, StandardCharsets.UTF_8);
                        System.out.println("📨 🔄 Decoded from bytes: " + jsonString);

                        // Base64 디코딩이 필요한지 확인
                        if (isBase64(jsonString)) {
                            byte[] decodedBytes = Base64.getDecoder().decode(jsonString);
                            jsonString = new String(decodedBytes, StandardCharsets.UTF_8);
                            System.out.println("📨 🔓 Base64 decoded: " + jsonString);
                        }

                        message = objectMapper.readValue(jsonString, Map.class);
                        System.out.println("📨 ✅ Parsed from bytes: " + message);
                    } else {
                        String jsonString = objectMapper.writeValueAsString(payload);
                        message = objectMapper.readValue(jsonString, Map.class);
                        System.out.println("📨 ✅ Converted from Object: " + message);
                    }

                    if (message != null) {
                        messages.offer(message);
                        System.out.println("📨 ✅ Message added to queue. Queue size: " + messages.size());
                    }

                } catch (Exception e) {
                    System.err.println("📨 ❌ Failed to parse personal message: " + e.getMessage());
                    e.printStackTrace();
                }
                System.out.println("📨🎯 === 실제 개인 메시지 수신 완료 ===");
            }
        });

        System.out.println("✅ 실제 개인 큐 구독 완료: " + destination + " (ID: " + subscription.getSubscriptionId() + ")");
        Thread.sleep(500);

        return new MessageSubscriber(messages, subscription);
    }

    /**
     * 실제 방 토픽 구독
     */
    public MessageSubscriber subscribeToRealRoomTopic(StompSession session, String roomId) throws Exception {
        BlockingQueue<Map<String, Object>> messages = new LinkedBlockingQueue<>();

        String destination = "/topic/interview/" + roomId;
        System.out.println("📬 실제 방 토픽 구독: " + destination);

        StompSession.Subscription subscription = session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Object.class;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("📨 Received room message for " + roomId + ": " + payload);
                try {
                    Map<String, Object> message = null;

                    if (payload instanceof Map) {
                        message = (Map<String, Object>) payload;
                    } else if (payload instanceof String) {
                        message = objectMapper.readValue((String) payload, Map.class);
                    } else if (payload instanceof byte[]) {
                        // ✅ 바이트 배열 처리 추가
                        byte[] bytes = (byte[]) payload;
                        String jsonString = new String(bytes, StandardCharsets.UTF_8);
                        System.out.println("📨 🔄 Room decoded from bytes: " + jsonString);

                        // Base64 디코딩이 필요한지 확인
                        if (isBase64(jsonString)) {
                            byte[] decodedBytes = Base64.getDecoder().decode(jsonString);
                            jsonString = new String(decodedBytes, StandardCharsets.UTF_8);
                            System.out.println("📨 🔓 Room Base64 decoded: " + jsonString);
                        }

                        message = objectMapper.readValue(jsonString, Map.class);
                    } else {
                        String jsonString = objectMapper.writeValueAsString(payload);
                        message = objectMapper.readValue(jsonString, Map.class);
                    }

                    if (message != null) {
                        System.out.println("📨 Parsed room message: " + message);
                        messages.offer(message);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Failed to parse room message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        System.out.println("✅ 실제 방 토픽 구독 완료: " + destination + " (ID: " + subscription.getSubscriptionId() + ")");
        return new MessageSubscriber(messages, subscription);
    }

    /**
     * Base64 문자열인지 확인하는 헬퍼 메서드
     */
    private boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 메시지 구독자 - 공통 사용
     */
    public static class MessageSubscriber {
        private final BlockingQueue<Map<String, Object>> messages;
        private final StompSession.Subscription subscription;

        public MessageSubscriber(BlockingQueue<Map<String, Object>> messages, StompSession.Subscription subscription) {
            this.messages = messages;
            this.subscription = subscription;
        }

        public Map<String, Object> waitForMessage(String expectedType, int timeoutSeconds) throws Exception {
            long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);

            while (System.currentTimeMillis() < endTime) {
                Map<String, Object> message = messages.poll(1, TimeUnit.SECONDS);
                if (message != null && expectedType.equals(message.get("type"))) {
                    return message;
                }
                if (message != null) {
                    messages.offer(message); // 다시 큐에 넣기
                }
            }

            throw new AssertionError(
                    "Expected message type '" + expectedType + "' not received within " + timeoutSeconds + " seconds");
        }

        public BlockingQueue<Map<String, Object>> getMessages() {
            return messages;
        }

        public void unsubscribe() {
            if (subscription != null) {
                subscription.unsubscribe();
                System.out.println("🔌 Unsubscribed from: " + subscription.getSubscriptionId());
            }
        }
    }

    /**
     * 실제 운영환경 테스트용 STOMP 세션 핸들러
     */
    private static class RealTestStompSessionHandler implements StompSessionHandler {
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("🔗🎯 REAL STOMP session connected: " + session.getSessionId());
        }

        @Override
        public void handleException(StompSession session, StompCommand command,
                                    StompHeaders headers, byte[] payload, Throwable exception) {
            System.err.println("❌🎯 REAL STOMP exception - Command: " + command);
            System.err.println("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Object.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            System.out.println("🔗🎯 REAL Frame received");
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            System.err.println("❌🎯 REAL STOMP transport error: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * 실제 방 생성 후 roomId 반환
     */
    public String createRoomAndGetId(StompSession creatorSession,
                                     WebSocketTestHelper.MessageSubscriber creatorSubscriber, String mode,
                                     Long questionSetId) throws Exception {

        RoomCreateRequest roomCreateRequest = new RoomCreateRequest(mode, questionSetId);
        creatorSession.send("/app/room-create", roomCreateRequest);

        Map<String, Object> response = creatorSubscriber.waitForMessage("room-created", 3);
        Map<String, Object> responsePayload = (Map<String, Object>) response.get("data");
        RoomCreateResponse roomCreateResponse = objectMapper.convertValue(responsePayload, RoomCreateResponse.class);

        return roomCreateResponse.getRoomId();
    }
}