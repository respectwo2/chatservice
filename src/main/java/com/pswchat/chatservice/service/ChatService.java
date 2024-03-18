package com.pswchat.chatservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pswchat.chatservice.domain.Chat;
import com.pswchat.chatservice.repository.ChatRepository;

@Service
public class ChatService {
	private final ChatRepository chatRepository;
	
	@Autowired
	public ChatService(ChatRepository chatrepository) {
		this.chatRepository = chatrepository;
	}
	
	public Chat createChat(Chat chat) {
		return chatRepository.save(chat);
		
	}
	public List<Chat> findChat() {
		return chatRepository.findAll();
	}
	
	
}
