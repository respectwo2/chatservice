package com.pswchat.chatservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pswchat.chatservice.domain.ChatCollections;

public interface ChatRepository extends MongoRepository<ChatCollections, String> {

}
