package com.projet6.PayMyBuddy.controller;

import java.util.Arrays;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.projet6.PayMyBuddy.Service.ConnectionService;
import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.ServiceImpl.TransactionServiceImpl;
import com.projet6.PayMyBuddy.ServiceImpl.UserServiceImpl.CustomUserDetails;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Role;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class TransactionController {
	
	   @Autowired
	   TransactionServiceImpl transactionServiceImpl;
	   
	   @Autowired
	   UserService userService;
	   
	   @Autowired
	   ConnectionService connectionService;	 
	   
	   @Autowired
	   UserRepository userRepository;
	   
	   
	   @GetMapping("/transfer/page")
	   public String getRalation(@RequestParam(defaultValue = "0") int page, Model model, HttpServletRequest request) {
	       return connectionService.getConnectionsToTransferAmounOfMoney(page, model, request);
	   }

	    
	    @PostMapping("/processTransfer")
	    public String processTransfer(@RequestParam("relationOption") String receiverEmail,
	                                  @RequestParam("description") String description,
	                                  @RequestParam("amount") String amountStr,
	                                  Model model) {
	        try {
	            double amount = Double.parseDouble(amountStr);
	            transactionServiceImpl.createTransaction(receiverEmail, description, amount);
	            model.addAttribute("successMessage", "Transfert réussi!");
	        } catch (NumberFormatException e) {
	            model.addAttribute("errorMessage", "Le montant doit être un nombre valide.");
	        } catch (IllegalArgumentException | UserNotFoundException e) {
	            model.addAttribute("errorMessage", e.getMessage());
	        } catch (Exception e) {
	            model.addAttribute("errorMessage", "Erreur lors du transfert: " + e.getMessage());
	        }
	        
	     // Recharge les connexions pour les afficher à nouveau après le transfert
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String userEmail = authentication.getName();
	        User currentUser = userService.findByEmail(userEmail);

	        if (currentUser != null) {
	            Set<User> connections = currentUser.getConnections();
	            model.addAttribute("connections", connections);

	            int page = 0; // Default page number
	            int size = 5; // Nombre de transactions par page
	            Page<Transaction> transactionPage = transactionServiceImpl.getTransactionsForUser(userEmail, page, size);
	            
	            // Check if transactionPage is null and handle accordingly
	            if (transactionPage != null) {
	                model.addAttribute("transactionPage", transactionPage);
	            } else {
	                model.addAttribute("transactionPage", Page.empty());
	            }
	        } else {
	            model.addAttribute("transactionPage", Page.empty());
	        }

		        return "transfer-page";

	    }
	    
	    private String addConnectionsToModel(int page, Model model) {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        User currentUser = getCurrentUser(authentication);

	        if (currentUser != null) {
	            Set<User> connections = currentUser.getConnections();
	            model.addAttribute("connections", connections);

	            int size = 5; // Nombre de transactions par page
	            Page<Transaction> transactionPage = transactionServiceImpl.getTransactionsForUser(currentUser.getEmail(), page, size);
	            if (transactionPage != null) {
	                model.addAttribute("transactionPage", transactionPage);
	            } else {
	                model.addAttribute("transactionPage", Page.empty());
	            }
	        } else {
	            model.addAttribute("transactionPage", Page.empty());
	        }
	        return "transfer-page";
	    }

	    private User getCurrentUser(Authentication authentication) {
	        if (authentication != null) {
	            if (authentication.getPrincipal() instanceof CustomUserDetails) {
	                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	                return userDetails.getUser();
	            } else if (authentication.getPrincipal() instanceof OAuth2User) {
	                OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
	                String email = oauthUser.getAttribute("email");
	                String username = oauthUser.getAttribute("login");

	                // Handle email for OAuth2 users (email may be null, use username if needed)
	                if (email == null) {
	                    email = username + "@github.com";
	                }

	                User user = userRepository.findByEmail(email);
	                if (user == null) {
	                    // If user is not found in the database, create a new user entry
	                    user = new User();
	                    user.setEmail(email);
	                    user.setUsername(username);
	                    user.setPassword(""); // No password for OAuth2 users
	                    user.setRoles(Arrays.asList(new Role("ROLE_USER")));
	                    userRepository.save(user);
	                }
	                return user;
	            }
	        }
	        return null;
	    }
	    
 }

