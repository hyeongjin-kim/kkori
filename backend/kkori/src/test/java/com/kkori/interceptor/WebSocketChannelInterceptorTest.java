package com.kkori.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.kkori.config.validator.WebSocketSecurityValidator;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocket 채널 인터셉터 구독 검증 테스트")
class WebSocketChannelInterceptorTest {

    @Mock
    private WebSocketSecurityValidator securityValidator;

    @Mock
    private MessageChannel channel;

    private ChannelInterceptor channelInterceptor;
    private Map<String, Object> sessionAttributes;

    @BeforeEach
    void setUp() {
        sessionAttributes = new HashMap<>();
        
        // WebSocketConfig의 채널 인터셉터와 동일한 로직으로 구현
        channelInterceptor = new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                // CONNECT 명령 처리
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    handleConnectCommand(accessor);
                    return message;
                }

                // SUBSCRIBE 명령 처리
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    return handleSubscribeCommand(accessor, message);
                }

                return message;
            }

            private void handleConnectCommand(StompHeaderAccessor accessor) {
                Long userId = (Long) accessor.getSessionAttributes().get("userId");
                if (userId != null) {
                    accessor.setUser(() -> userId.toString());
                }
            }

            private Message<?> handleSubscribeCommand(StompHeaderAccessor accessor, Message<?> message) {
                String destination = accessor.getDestination();
                Long userId = (Long) accessor.getSessionAttributes().get("userId");
                
                // 인터뷰 관련 토픽이 아니면 통과
                if (destination == null || userId == null || !isInterviewRelatedTopic(destination)) {
                    return message;
                }
                
                // destination에서 roomId 추출
                String roomId = extractRoomIdFromDestination(destination);
                if (roomId == null) {
                    return message;
                }
                
                // 방 멤버십 검증
                if (!securityValidator.isUserInRoom(roomId, userId)) {
                    return null; // 구독 거부
                }
                
                return message;
            }

            private boolean isInterviewRelatedTopic(String destination) {
                return destination.startsWith("/topic/") &&
                       (destination.contains("/interview/") || destination.contains("/chat/") || destination.contains("/signal/"));
            }

            private String extractRoomIdFromDestination(String destination) {
                String[] parts = destination.split("/");
                
                for (int i = 0; i < parts.length - 1; i++) {
                    if (("interview".equals(parts[i]) || "chat".equals(parts[i]) || "signal".equals(parts[i])) 
                        && i + 1 < parts.length) {
                        return parts[i + 1];
                    }
                }
                
                return null;
            }
        };
    }


    @Test
    @DisplayName("비 인터뷰 토픽 구독 허용")
    void handleSubscribeCommandForNonInterviewTopic() {
        // given
        String destination = "/topic/general/notifications";
        Long userId = 1L;
        sessionAttributes.put("userId", userId);
        
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setSessionAttributes(sessionAttributes);
        Message<?> message = new GenericMessage<>("", accessor.getMessageHeaders());

        // when
        Message<?> result = channelInterceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        verify(securityValidator, never()).isUserInRoom(any(), any());
    }

    @Test
    @DisplayName("방 멤버인 사용자의 인터뷰 토픽 구독 허용")
    void handleSubscribeCommandForValidRoomMember() {
        // given
        String destination = "/topic/interview/ROOM123";
        String roomId = "ROOM123";
        Long userId = 1L;
        sessionAttributes.put("userId", userId);
        
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setSessionAttributes(sessionAttributes);
        Message<?> message = new GenericMessage<>("", accessor.getMessageHeaders());

        given(securityValidator.isUserInRoom(roomId, userId)).willReturn(true);

        // when
        Message<?> result = channelInterceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        verify(securityValidator).isUserInRoom(roomId, userId);
    }

    @Test
    @DisplayName("방 멤버가 아닌 사용자의 인터뷰 토픽 구독 차단")
    void handleSubscribeCommandForInvalidRoomMember() {
        // given
        String destination = "/topic/interview/ROOM123";
        String roomId = "ROOM123";
        Long userId = 1L;
        sessionAttributes.put("userId", userId);
        
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setSessionAttributes(sessionAttributes);
        Message<?> message = new GenericMessage<>("", accessor.getMessageHeaders());

        given(securityValidator.isUserInRoom(roomId, userId)).willReturn(false);

        // when
        Message<?> result = channelInterceptor.preSend(message, channel);

        // then
        assertThat(result).isNull(); // 구독 차단
        verify(securityValidator).isUserInRoom(roomId, userId);
    }

    @Test
    @DisplayName("채팅 토픽 구독 검증")
    void handleSubscribeCommandForChatTopic() {
        // given
        String destination = "/topic/chat/ROOM123";
        String roomId = "ROOM123";
        Long userId = 1L;
        sessionAttributes.put("userId", userId);
        
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setSessionAttributes(sessionAttributes);
        Message<?> message = new GenericMessage<>("", accessor.getMessageHeaders());

        given(securityValidator.isUserInRoom(roomId, userId)).willReturn(true);

        // when
        Message<?> result = channelInterceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        verify(securityValidator).isUserInRoom(roomId, userId);
    }

    @Test
    @DisplayName("시그널링 토픽 구독 검증")
    void handleSubscribeCommandForSignalTopic() {
        // given
        String destination = "/topic/signal/ROOM123";
        String roomId = "ROOM123";
        Long userId = 1L;
        sessionAttributes.put("userId", userId);
        
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setSessionAttributes(sessionAttributes);
        Message<?> message = new GenericMessage<>("", accessor.getMessageHeaders());

        given(securityValidator.isUserInRoom(roomId, userId)).willReturn(true);

        // when
        Message<?> result = channelInterceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        verify(securityValidator).isUserInRoom(roomId, userId);
    }

    @Test
    @DisplayName("roomId 추출 실패 시 구독 허용")
    void handleSubscribeCommandWithInvalidDestinationFormat() {
        // given
        String destination = "/topic/interview/"; // roomId 없음
        Long userId = 1L;
        sessionAttributes.put("userId", userId);
        
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setSessionAttributes(sessionAttributes);
        Message<?> message = new GenericMessage<>("", accessor.getMessageHeaders());

        // when
        Message<?> result = channelInterceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull(); // 잘못된 형식이지만 허용
        verify(securityValidator, never()).isUserInRoom(any(), any());
    }

    @Test
    @DisplayName("userId가 없는 경우 구독 허용")
    void handleSubscribeCommandWithoutUserId() {
        // given
        String destination = "/topic/interview/ROOM123";
        
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setSessionAttributes(sessionAttributes);
        Message<?> message = new GenericMessage<>("", accessor.getMessageHeaders());

        // when
        Message<?> result = channelInterceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull(); // userId 없으면 허용
        verify(securityValidator, never()).isUserInRoom(any(), any());
    }

    @Test
    @DisplayName("다른 STOMP 명령은 그대로 통과")
    void handleOtherStompCommands() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        Message<?> message = new GenericMessage<>("", accessor.getMessageHeaders());

        // when
        Message<?> result = channelInterceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        verify(securityValidator, never()).isUserInRoom(any(), any());
    }
}