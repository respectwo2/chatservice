package com.pswchat.chatservice.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pswchat.chatservice.config.DateTimeUtil;
import com.pswchat.chatservice.config.LocalDateTimeAdapter;
import com.pswchat.chatservice.domain.Chat;

@Controller
public class MyWebSocketHandler extends TextWebSocketHandler {
	private List<WebSocketSession> sessionList = new ArrayList<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws IOException {
		System.out.println("연결 세션 ID" + session.getId());
		String name = session.getHandshakeHeaders().get("createdName").get(0);
        sessionList.add(session); 
        
        sessionList.forEach(s-> {
            try {
                s.sendMessage(new TextMessage(name+"님께서 입장하셨습니다."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        
        
	}
        

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String name = session.getHandshakeHeaders().get("createdName").get(0);

		//LocalDateTime을 사용하기 위해 어댑터 사용 코드 추가
		Gson gson = new GsonBuilder()
	            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
	            .create();

		sessionList.forEach(s -> {
		    try {
		        Chat chat = gson.fromJson(message.getPayload(), Chat.class);
		        String formattedDate = DateTimeUtil.formatLocalDateTime(chat.getCreatedDate());
		        s.sendMessage(new TextMessage(name + " : " + chat.getContent() + " [" + formattedDate + "]"));
		    } catch (IOException e) {
		        throw new RuntimeException(e);
		    }
		});


	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessionList.remove(session);
		String name = session.getHandshakeHeaders().get("createdName").get(0);
		sessionList.forEach(s -> {
			try {
				s.sendMessage(new TextMessage(name + "님께서 퇴장하셨습니다."));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
