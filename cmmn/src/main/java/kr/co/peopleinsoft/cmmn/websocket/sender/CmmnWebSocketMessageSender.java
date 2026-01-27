package kr.co.peopleinsoft.cmmn.websocket.sender;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class CmmnWebSocketMessageSender {
	final SimpMessagingTemplate messagingTemplate;

	public CmmnWebSocketMessageSender(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	/**
	 * /topic/public 를 구독한 사용자 전체에 메시지 전송 (전체공지)
	 */
	public void sendBroadcastMessage(String message) {
		messagingTemplate.convertAndSend("/topic/public", message);
	}

	/**
	 * 특정 사용자에게 메시지 전송
	 * 실제 메시지 전송 주소는 /user/{username}/queue/private
	 */
	public void sendPrivateMessage(String username, String message) {
		messagingTemplate.convertAndSendToUser(username, "/queue/private", message);
	}

	/**
	 * 특정 사용자에게 알림 전송
	 */
	public void sendNotificationToUser(String username, String message) {
		messagingTemplate.convertAndSendToUser(username, "/queue/notification", message);
	}
}