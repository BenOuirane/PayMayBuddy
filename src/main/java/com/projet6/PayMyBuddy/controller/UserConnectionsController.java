package com.projet6.PayMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.projet6.PayMyBuddy.Service.ConnectionService;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class UserConnectionsController {
	
	@Autowired
    private ConnectionService connectionService;

	@PostMapping("/addRelation")
	public String addRelation(@RequestParam("relationSearch") String relationSearch, Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication == null || !authentication.isAuthenticated()) {
	        throw new IllegalStateException("User not authenticated");
	    }

	    String userEmail = null;

	    if (authentication.getPrincipal() instanceof UserDetails) {
	        // Pour les utilisateurs authentifiés non-OAuth2
	        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
	        userEmail = currentUser.getUsername();
	    } else if (authentication.getPrincipal() instanceof OAuth2User) {
	        // Pour les utilisateurs authentifiés OAuth2
	        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
	        userEmail = (String) oAuth2User.getAttribute("email");

	        // Si l'email est null, utiliser un autre attribut unique
	        if (userEmail == null) {
	            userEmail = (String) oAuth2User.getAttribute("login") + "@github.com";
	        }
	    }

	    if (userEmail == null) {
	        throw new IllegalStateException("Email not found in user attributes");
	    }

	    try {
	        connectionService.addUserConnection(userEmail, relationSearch);
	        model.addAttribute("successMessage", "Relation ajoutée avec succès.");
	    } catch (Exception e) {
	        model.addAttribute("errorMessage", e.getMessage());
	        return "ajouter-relation"; // Retourner à la même page en cas d'erreur
	    }
	    return "ajouter-relation"; // Retourner à la même page après le succès
	}

	
	
}