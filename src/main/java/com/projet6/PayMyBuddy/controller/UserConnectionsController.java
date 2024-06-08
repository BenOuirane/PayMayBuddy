package com.projet6.PayMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String addRelation(@RequestParam("relationSearch") String relationSearch, @AuthenticationPrincipal UserDetails currentUser, Model model) {
        String userEmail = currentUser.getUsername(); // Obtenir l'email de l'utilisateur connecté
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