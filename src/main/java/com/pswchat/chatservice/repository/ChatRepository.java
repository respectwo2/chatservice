package com.pswchat.chatservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pswchat.chatservice.domain.Chat;

public interface ChatRepository extends MongoRepository<Chat, String> {

}
