package com.projet6.PayMyBuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.projet6.PayMyBuddy.ServiceImpl.UserServiceImpl.CustomUserDetails;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;


@Controller
public class MainContoller {
	
	@Autowired
	UserRepository  userRepository;
	
	@GetMapping("/login")
	public String login() {
		return "login-page";
	}
	
	@GetMapping("/profil")
	public String profil(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        User user = userDetails.getUser();
	        // Log to check if username is correctly retrieved
	        /* System.out.println("Username: " + user.getUsername());
	        System.out.println("Email: " + user.getEmail()); */
	        /*
	        model.addAttribute("username", user.getUsername());
	        model.addAttribute("email", user.getEmail());
	        model.addAttribute("password", userDetails.getUser().getPassword());
	        */
	     // Fetch fresh user data from the database to ensure it's up-to-date
	        User updatedUser = userRepository.findByEmail(user.getEmail());
	        
	        model.addAttribute("username", updatedUser.getUsername());
	        model.addAttribute("email", updatedUser.getEmail());
	        model.addAttribute("password", userDetails.getUser().getPassword()); // Ensure this is needed

	        return "profil-page";
	    } else {
	        // Handle authentication failure or no user found
	        return "redirect:/login";
	    }
	}

	
	@GetMapping("/ajouter-relation")
    public String ajouterRelationPage() {
        return "ajouter-relation"; // Return the name of the Thymeleaf template for the ajouter-relation page
    }
	
	
	
}
