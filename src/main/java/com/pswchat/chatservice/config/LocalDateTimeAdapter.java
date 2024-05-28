package com.pswchat.chatservice.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

	@Override
	public void write(JsonWriter out, LocalDateTime value) throws IOException {
		// TODO Auto-generated method stub
	}
	@Override
	public LocalDateTime read(JsonReader in) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}

