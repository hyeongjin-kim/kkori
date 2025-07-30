package com.kkori;

import static org.assertj.core.api.Assertions.assertThat;

import com.kkori.entity.User;
import com.kkori.jwt.Token;
import com.kkori.jwt.TokenProvider;
import com.kkori.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class EventControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    private WebSocketStompClient stompClient;
    private StompSession session;
    private Token jwt;
    private String TEST_USER_SUB = "test-user-sub";
    private String TEST_USER_NICK = "test-user-nick";
    private User testUser;

    @BeforeEach
    void setup() throws Exception {

        testUser = User.builder()
                .sub(TEST_USER_SUB)
                .nickname(TEST_USER_NICK)
                .build();

        System.out.println(testUser);

        // DB에 저장하고 ID 할당받기
        testUser = userRepository.save(testUser);

        // 2. 실제 TokenProvider로 JWT 토큰 생성
        Token accessToken = tokenProvider.generateAccessToken(testUser);
        String tokenValue = accessToken.getToken();

        Transport wsTransport = new WebSocketTransport(new StandardWebSocketClient());
        stompClient = new WebSocketStompClient(new SockJsClient(Arrays.asList(wsTransport)));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String url = "ws://localhost:" + port + "/ws";
        WebSocketHttpHeaders wsHeaders = new WebSocketHttpHeaders();
        wsHeaders.add("Cookie", "accessToken=" + tokenValue);
        StompHeaders connectHeaders = new StompHeaders();

// connect()의 반환 타입이 CompletableFuture<StompSession>으로 대체
        CompletableFuture<StompSession> f = stompClient.connectAsync(
                url,
                wsHeaders,
                connectHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession stompSession, StompHeaders headers) {
                        // 연결 성공
                    }

                    @Override
                    public void handleTransportError(StompSession session, Throwable exception) {
                        // 에러 처리
                    }
                }
        );
        session = f.get(5, TimeUnit.SECONDS);
    }

    @Test
    void sendQuestionMessage() throws Exception {
        // 1) 메시지 수신용 Future
        CompletableFuture<Map> received = new CompletableFuture<>();

        // 2) 수신 채널 구독 (예: /topic/question-result)
        session.subscribe("/user/queue/interview", new StompFrameHandler() {
            //session.subscribe("/topic/broadcast", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void handleFrame(StompHeaders headers, Object payload) {
                received.complete((Map<String, String>) payload);
            }
        });

        // 3) payload 생성
        Map<String, String> payload = Map.of(
                "questionSetId", "1",
                "userName", "kjk"
        );

        // 4) 메시지 전송 (/app/question 으로 @MessageMapping("/room-create") 이 있다고 가정)
        session.send("/app/room-create", payload);

        // 5) 결과 검증 (선택적)
        Map<String, String> result = received.get(5, TimeUnit.SECONDS);
        assertThat(result.get("questionSetId")).isEqualTo("1");
        assertThat(result.get("userName")).isEqualTo("kjk");
    }
}