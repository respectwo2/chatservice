package com.pswchat.chatservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.pswchat.chatservice.controller.MyWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MyWebSocketHandler myWebSocketHandler;

    @Autowired
    public WebSocketConfig(MyWebSocketHandler myWebSocketHandler) {
        this.myWebSocketHandler = myWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    	//핸들러 설정, 와일드카드(*) 옵션으로 모든 도메인을 허용시키고
    	//withSockJS는 웹소켓을 지원하지 않는 브라우저에서도 사용가능하게만듬

    	//SocketJS 오류 발생해 주석 처리 후 해당 코드 삭제
    	//registry.addHandler(myWebSocketHandler, "/chat").setAllowedOrigins("*").withSockJS();
    	registry.addHandler(myWebSocketHandler, "/chatRoom{room_id}")
    			.setAllowedOrigins("*")
   
    			//세션끼리 연결해주는 메서드
    			.addInterceptors(new HttpHandshakeInterceptor());

    }
    
    
    
}
