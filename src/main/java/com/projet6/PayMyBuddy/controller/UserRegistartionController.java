package com.projet6.PayMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.dto.UserRegistrationDto;

@Controller
@RequestMapping("/registration")
public class UserRegistartionController {
	
	@Autowired
	UserService  userService;
	
	@ModelAttribute("user")
	public UserRegistrationDto  userRegistrationDto() {
		return new UserRegistrationDto();	}
	
	@GetMapping
	public String showRegistrationForm() {
		return "registration";	}
	
	@PostMapping
	public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
		userService.saveUser(registrationDto);
		return "redirect:/registration?success";	
		}

}
