package com.projet6.PayMyBuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.projet6.PayMyBuddy.ServiceImpl.UserServiceImpl.CustomUserDetails;
import com.projet6.PayMyBuddy.model.Role;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
	public String profil(Model model, @AuthenticationPrincipal OAuth2User principal) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		  // Debugging log for Authentication object
	    System.out.println("Authentication: " + authentication);
	    
	    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        User user = userDetails.getUser();
	        
	        // Debugging log for CustomUserDetails
	        System.out.println("CustomUserDetails: " + userDetails);
	        
	        
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
	        if (updatedUser != null) {
	        model.addAttribute("username", updatedUser.getUsername());
	        model.addAttribute("email", updatedUser.getEmail());
	     //   model.addAttribute("password", userDetails.getUser().getPassword()); // Ensure this is needed
	        } else {
                return "redirect:/login"; // or show an error message
            }
	        return "profil-page";
	    } else if (principal != null) {
    	
	    	   // Debugging log for OAuth2User
	        System.out.println("OAuth2User: " + principal);

	        // Log the available attributes for debugging
	        System.out.println("OAuth2User attributes: " + principal.getAttributes());

	        // Fallback email generation using username if email is null
	        String email = principal.getAttribute("email");
	        String username = principal.getAttribute("login");

	        if (email == null) {
	            email = username + "@github.com"; // Generating a fallback email address
	        }

	        // Debugging log for email and username
	        System.out.println("Email: " + email);
	        System.out.println("Username: " + username);

	        // Check if the user exists in the local database
	        User updatedUser = userRepository.findByEmail(email);

	        if (updatedUser == null) {
	            // Create a new user in the database if not exists
	        	/*
	            User newUser = new User();
	            newUser.setEmail(email);
	            newUser.setUsername(username);
	            newUser.setPassword(""); // No password for OAuth2 users
	            newUser.setRoles(Arrays.asList(new Role("ROLE_USER")));
	            userRepository.save(newUser);
                      
	            model.addAttribute("username", newUser.getUsername());
	            model.addAttribute("email", newUser.getEmail());
	             */
	        } else {
	            model.addAttribute("username", updatedUser.getUsername());
	            model.addAttribute("email", updatedUser.getEmail());
	        }
	        return "profil-page";
        }
        else  {
        	// Handle authentication failure or no user found
            System.out.println("No authenticated user found.");
	        return "redirect:/login";
	    }
            
	}

	
	@GetMapping("/ajouter-relation")
    public String ajouterRelationPage() {
        return "ajouter-relation"; // Return the name of the Thymeleaf template for the ajouter-relation page
    }
	
	
	
}
