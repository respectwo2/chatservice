package com.pswchat.chatservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.pswchat.chatservice.domain.ChatCollections;
import com.pswchat.chatservice.service.ChatService;

@Controller
public class ChatController {
	private final ChatService chatservice;
	
	@Autowired
	public ChatController(ChatService chatservice) {
		this.chatservice = chatservice;
	}
	
	@PostMapping("/chats")
	public ChatCollections createChat(@RequestBody ChatCollections chatcollections) {
		return chatservice.createChat(chatcollections);
	}
	
	
}
