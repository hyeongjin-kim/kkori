package com.kkori.config;

import com.kkori.config.validator.WebSocketSecurityValidator;
import com.kkori.interceptor.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor authInterceptor;
    private final WebSocketSecurityValidator securityValidator;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(authInterceptor) // 인증 인터셉터 등록
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // topic: 브로드캐스트, queue: 개인 메시지
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    //Principal 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

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
                // HandshakeInterceptor에서 저장된 userId 가져오기
                Long userId = (Long) accessor.getSessionAttributes().get("userId");

                if (userId != null) {
                    accessor.setUser(() -> userId.toString());
//                    System.out.println("Principal 설정됨: " + userId);
                }
//                else {
//                    System.err.println("userId가 세션에 없습니다!");
//                }
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
                    log.warn("User {} tried to subscribe to room {} without being a member. Destination: {}", 
                            userId, roomId, destination);
                    return null; // 구독 거부
                }
                
                return message;
            }

            private boolean isInterviewRelatedTopic(String destination) {
                return destination.startsWith("/topic/interview/");
            }

            private String extractRoomIdFromDestination(String destination) {
                // /topic/interview/{roomId} 패턴에서 roomId 추출
                if (destination.startsWith("/topic/interview/")) {
                    return destination.substring("/topic/interview/".length());
                }
                return null;
            }

        });
    }
}