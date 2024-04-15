package com.pswchat.chatservice.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Map<String, String> uriParams = getUriParams(session);
        String roomId = uriParams.get("room_id");
        String createdName = uriParams.get("createdName");
        sessions.put(session.getId(), session);
        System.out.println("새 클라이언트와 연결되었습니다. Room ID: " + roomId);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message) throws IOException {
        for (WebSocketSession webSocketSession : sessions.values()) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(message);
            }
        }

        
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) {
        sessions.remove(session.getId());
        System.out.println("클라이언트와의 연결이 해제되었습니다. Session ID: " + session.getId());
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
}
