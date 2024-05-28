package com.pswchat.chatservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.pswchat.chatservice.domain.Chat;

@RestController
public class ViewController {
	@GetMapping("/enter")
	public ModelAndView showChatEntryForm() {
		ModelAndView modelAndView = new ModelAndView("chatRoom");
		modelAndView.addObject("chat", new Chat());
		return modelAndView;
	}

}
