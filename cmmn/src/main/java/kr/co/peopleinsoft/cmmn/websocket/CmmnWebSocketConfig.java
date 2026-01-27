package kr.co.peopleinsoft.cmmn.websocket;

import kr.co.peopleinsoft.cmmn.websocket.sender.CmmnWebSocketMessageSender;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class CmmnWebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
			.setAllowedOriginPatterns("*") // cors 설정 (운영시 구체적인 도메인으로 지정)
			.withSockJS(); // SockeJS fallback 옵션 (WebSocket 미지원 브라우저 대응)
	}

	/*** 메시지브로커 설정 ***/
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 클라이언트로 메시지 전송시 prefix (/topic : 전체, /queue : 개인)
		registry.enableSimpleBroker("/topic", "/queue");

		// Client 가 서버로 메시지 전송시 prefix
		registry.setApplicationDestinationPrefixes("/app");

		// 특정 사용자에게 메시지 전송 시 prefix
		registry.setUserDestinationPrefix("/user");
	}

	/*** 로그인 한 사용자만 WebSocket 사용 처리 ***/
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor stompHeaderAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				assert stompHeaderAccessor != null;
				if (StompCommand.CONNECT.equals(stompHeaderAccessor.getCommand())) {
					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
					if (authentication != null && authentication.isAuthenticated()) {
						stompHeaderAccessor.setUser(authentication);
					}
				}
				return message;
			}
		});
	}

	/**
	 * 메시지 전송을 위한 MessageSender
	 */
	@Bean
	CmmnWebSocketMessageSender webSocketMessageSender(SimpMessagingTemplate messagingTemplate) {
		return new CmmnWebSocketMessageSender(messagingTemplate);
	}
}