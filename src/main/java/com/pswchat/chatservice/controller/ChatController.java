package com.pswchat.chatservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pswchat.chatservice.domain.Chat;
import com.pswchat.chatservice.repository.ChatRepository;
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
		return chatservice.createChat(chat);
	}
	
	@GetMapping("/chatlist")
	public List<Chat> findAll(){
		return chatservice.findChat();
	}
	
	
}
