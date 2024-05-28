package com.pswchat.chatservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.pswchat.chatservice.controller.MyWebSocketHandler;
import com.pswchat.chatservice.repository.ChatRepository;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MyWebSocketHandler myWebSocketHandler;

    @Autowired
    public WebSocketConfig(MyWebSocketHandler myWebSocketHandler, ChatRepository chatRepository) {
        this.myWebSocketHandler = myWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler, "/chatRoom/sockjs")
        		.setAllowedOriginPatterns("http://localhost:8080")
                .withSockJS();
    }

}
