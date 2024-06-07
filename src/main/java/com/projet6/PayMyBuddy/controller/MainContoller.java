package com.projet6.PayMyBuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.projet6.PayMyBuddy.ServiceImpl.UserServiceImpl.CustomUserDetails;
import com.projet6.PayMyBuddy.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;


@Controller
public class MainContoller {
	
	@GetMapping("/login")
	public String login() {
		return "login-page";
	}
	
	@GetMapping("/")
	public String profil(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        User user = userDetails.getUser();
	        // Log to check if username is correctly retrieved
	        /* System.out.println("Username: " + user.getUsername());
	        System.out.println("Email: " + user.getEmail()); */
	        model.addAttribute("username", user.getUsername());
	        model.addAttribute("email", user.getEmail());
	        model.addAttribute("password", userDetails.getUser().getPassword());

	    }
	    		return "profil-page";
	}

}
