package com.pswchat.chatservice.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.pswchat.chatservice.domain.Chat;
import com.pswchat.chatservice.service.ChatService;

@RestController
public class ChatController {
	private final ChatService chatservice;
	
	@Autowired
	public ChatController(ChatService chatservice) {
		this.chatservice = chatservice;
	}
	
	@PostMapping("/createchat")
	public Chat createChat(@RequestBody Chat chat) {
		Chat createChat = Chat.builder()
				.chat_id(chat.getChat_id())
				.room_id(chat.getRoom_id())
				.content(chat.getContent())
				.createdDate(LocalDateTime.now())
				.createdName(chat.getCreatedName())
				.build();
		return chatservice.createChat(createChat);
	}
	
	@GetMapping("/chatlist")
	public List<Chat> findAll(){
		return chatservice.findChat();
	}
	
	@GetMapping("/delete")
	public void deleteAll() {
		chatservice.deleteAll();
	}

}
