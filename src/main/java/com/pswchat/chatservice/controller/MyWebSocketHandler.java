package com.pswchat.chatservice.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // 방 ID에 따라 세션을 관리하기 위한 Map
    private Map<String, List<WebSocketSession>> roomSessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
    	String roomId = getRoomIdFromPath(session);
//        String name = (String) session.getAttributes().get("createdName");
		String name = session.getHandshakeHeaders().get("createdName").get(0);

        // 해당 room_id로 등록된 세션 리스트 가져오기, 없으면 새 리스트 생성
        List<WebSocketSession> sessions = roomSessionMap.getOrDefault(roomId, new ArrayList<>());
        sessions.add(session);
        // Map 업데이트
        roomSessionMap.put(roomId, sessions);

        System.out.println(name);
        
        
        // 같은 방의 모든 세션에 메시지 전송
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage(name + "님께서 입장하셨습니다."));
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	String roomId = getRoomIdFromPath(session);
		String name = session.getHandshakeHeaders().get("createdName").get(0);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        List<WebSocketSession> sessions = roomSessionMap.get(roomId);
        if (sessions != null) {
            for (WebSocketSession s : sessions) {
                Chat chat = gson.fromJson(message.getPayload(), Chat.class);
                String formattedDate = DateTimeUtil.formatLocalDateTime(chat.getCreatedDate());
                s.sendMessage(new TextMessage(name + " : " + chat.getContent() + " [" + formattedDate + "]"));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomIdFromPath(session); // 수정된 부분
		String name = session.getHandshakeHeaders().get("createdName").get(0);
        List<WebSocketSession> sessions = roomSessionMap.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessionMap.remove(roomId);
            } else {
                for (WebSocketSession s : sessions) {
                    s.sendMessage(new TextMessage(name + "님께서 퇴장하셨습니다."));
                }
            }
        }
    }
    
    private String getRoomIdFromPath(WebSocketSession session) {
        UriComponents uriComponents = UriComponentsBuilder.fromUri(session.getUri()).build();
        String path = uriComponents.getPath();
        String[] pathSegments = path.split("/");
        // 예를 들어, 경로가 "/chat/12345"라면, 마지막 세그먼트("12345")가 방 ID입니다.
        return pathSegments[pathSegments.length - 1];
    }
}
