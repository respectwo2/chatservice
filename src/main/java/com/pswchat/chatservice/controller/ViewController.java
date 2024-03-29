package com.pswchat.chatservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {
	@GetMapping("/mainpage")
	public ModelAndView mainPage() {
		ModelAndView modelandview = new ModelAndView();
		modelandview.setViewName("main");
		return modelandview;
	}

}
