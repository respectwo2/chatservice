package com.pswchat.chatservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pswchat.chatservice.domain.Chat;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

}
