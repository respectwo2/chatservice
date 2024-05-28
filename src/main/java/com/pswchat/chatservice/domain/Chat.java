package com.pswchat.chatservice.domain;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Document(collection = "chatservice")
@AllArgsConstructor
@NoArgsConstructor

public class Chat {

	@Id
	private String id;
	
	private String room_id;
	private String content;
	private String createdName;
	private LocalDateTime createdDate;
	
	}

