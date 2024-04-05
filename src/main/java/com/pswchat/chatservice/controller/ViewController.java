package com.pswchat.chatservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.pswchat.chatservice.domain.Chat;

import jakarta.servlet.http.HttpSession;

@Controller
public class ViewController {
	@GetMapping("/mainpage")
	public ModelAndView mainPage() {
		ModelAndView modelandview = new ModelAndView();
		modelandview.setViewName("main");
		return modelandview;
	}
	

	@GetMapping("/enter")
	public ModelAndView showChatEntryForm() {
	    ModelAndView modelAndView = new ModelAndView("enter");
	    modelAndView.addObject("chat", new Chat());
	    return modelAndView;
	}

    
	@PostMapping("/enterChatRoom")
	public String enterChatRoom(@RequestParam("room_id") String room_id, @RequestParam("createdName") String createdName, HttpSession session) {
	    session.setAttribute("room_id", room_id);
	    session.setAttribute("createdName", createdName);

	    return "redirect:/chatRoom" + room_id; 
	}

    
    @GetMapping("/chatRoom/{room_id}")
    public String chatRoom(@PathVariable("room_id") String room_id, Model model,HttpSession session) {
    	model.addAttribute("room_id", room_id);
        return "chatRoom"; 
    }


}
