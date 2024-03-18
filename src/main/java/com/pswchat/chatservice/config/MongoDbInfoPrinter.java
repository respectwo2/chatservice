package com.pswchat.chatservice.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoDbInfoPrinter implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("현재 MongoDB 데이터베이스 이름: " + mongoTemplate.getDb().getName());
        System.out.println("현재 MongoDB 컬렉션 목록: " + mongoTemplate.getCollectionNames());
    }
}
