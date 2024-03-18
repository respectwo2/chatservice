package com.pswchat.chatservice.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "chatservice")
public class Chat {

	@Id
	private String chat_id;
	
	private String room_id;
	private String content;
	private String createdName;
	private Date createdDate;
	
	
}
