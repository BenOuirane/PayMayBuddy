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

	    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
	        return handleAuthenticatedUser(model, (CustomUserDetails) authentication.getPrincipal());
	    } else if (principal != null) {
	        return handleOAuth2User(model, principal);
	    } else {
	        return "redirect:/login";
	    }
	}

	private String handleAuthenticatedUser(Model model, CustomUserDetails userDetails) {
	    User updatedUser = userRepository.findByEmail(userDetails.getUser().getEmail());

	    if (updatedUser != null) {
	        model.addAttribute("username", updatedUser.getUsername());
	        model.addAttribute("email", updatedUser.getEmail());
	        // Optionally add password if necessary
	    } else {
	        return "redirect:/login"; // or show an error message
	    }

	    return "profil-page";
	}

	private String handleOAuth2User(Model model, OAuth2User principal) {
	    String email = principal.getAttribute("email");
	    String username = principal.getAttribute("login");

	    if (email == null) {
	        email = username + "@github.com";
	    }

	    User updatedUser = userRepository.findByEmail(email);

	    if (updatedUser == null) {
	        User newUser = new User();
	        newUser.setEmail(email);
	        newUser.setUsername(username);
	        newUser.setPassword(""); // No password for OAuth2 users
	        newUser.setRoles(Arrays.asList(new Role("ROLE_USER")));
	        userRepository.save(newUser);

	        model.addAttribute("username", newUser.getUsername());
	        model.addAttribute("email", newUser.getEmail());
	    } else {
	        model.addAttribute("username", updatedUser.getUsername());
	        model.addAttribute("email", updatedUser.getEmail());
	    }

	    return "profil-page";
	}


	
	@GetMapping("/ajouter-relation")
    public String ajouterRelationPage() {
        return "ajouter-relation"; // Return the name of the Thymeleaf template for the ajouter-relation page
    }
	
	@GetMapping("/sold")
    public String soldePage() {
        return "solde-page"; // Return the name of the Thymeleaf template for the solde-page page
    }
	

	
}
