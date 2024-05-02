package com.pswchat.chatservice.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pswchat.chatservice.config.DateTimeUtil;
import com.pswchat.chatservice.config.LocalDateTimeAdapter;
import com.pswchat.chatservice.domain.Chat;

@Controller
public class MyWebSocketHandler extends TextWebSocketHandler {
	private Map<String, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();


	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
	    Map<String, String> uriParams = getUriParams(session);
	    String roomId = uriParams.get("room_id");
	    addSessionToRoom(roomId, session);
	    System.out.println("새 연결 Room ID: " + roomId);
	}



	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
	    Map<String, String> uriParams = getUriParams(session);
	    String roomId = uriParams.get("room_id");
	    
	    List<WebSocketSession> sessionsInRoom = roomSessions.get(roomId);
	    if (sessionsInRoom != null) {
	        for (WebSocketSession webSocketSession : sessionsInRoom) {
	            if (webSocketSession.isOpen()) {
	                webSocketSession.sendMessage(message);
	            }
	        }
	    }
	}
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
	    Map<String, String> uriParams = getUriParams(session);
	    String roomId = uriParams.get("room_id");
	    List<WebSocketSession> sessionsInRoom = roomSessions.get(roomId);
	    if (sessionsInRoom != null) {
	    	//닫힌 세션 제거
	        sessionsInRoom.remove(session);
	        if (sessionsInRoom.isEmpty()) {
	        	//방에 연결된 세션이 없다면 방 삭제
	            roomSessions.remove(roomId);
	        }
	    }
	    System.out.println("연결 해제 Room ID: " + roomId);
	}

    
    
    private Map<String, String> getUriParams(WebSocketSession session) {
        UriComponents uriComponents = UriComponentsBuilder.fromUri(session.getUri()).build();
        Map<String, String> params = new HashMap<>();
        uriComponents.getQueryParams().forEach((key, values) -> {
            if (!values.isEmpty()) {
                params.put(key, values.get(0));
            }
        });
        return params;
    }
    
    public void addSessionToRoom(String roomId, WebSocketSession newSession) {
        List<WebSocketSession> sessionsInRoom = roomSessions.get(roomId);
        if (sessionsInRoom == null) {
            sessionsInRoom = new ArrayList<>();
            roomSessions.put(roomId, sessionsInRoom);
        }

        boolean sessionAlreadyExists = sessionsInRoom.stream()
                                         .anyMatch(s -> s.getId().equals(newSession.getId()));

        if (!sessionAlreadyExists) {
            sessionsInRoom.add(newSession);
        }
    }

}
