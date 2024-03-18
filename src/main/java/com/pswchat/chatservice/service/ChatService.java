package com.pswchat.chatservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pswchat.chatservice.domain.ChatCollections;
import com.pswchat.chatservice.repository.ChatRepository;

@Service
public class ChatService {
	private final ChatRepository chatRepository;
	
	@Autowired
	public ChatService(ChatRepository chatrepository) {
		this.chatRepository = chatrepository;
	}
	
	public ChatCollections createChat(ChatCollections chatcollections) {
		return chatRepository.save(chatcollections);
		
	}
}
