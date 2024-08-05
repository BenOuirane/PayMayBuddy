package com.projet6.PayMyBuddy.controller;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.projet6.PayMyBuddy.Service.ConnectionService;
import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.ServiceImpl.TransactionServiceImpl;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
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
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String userEmail = authentication.getName();
	       User currentUser = userService.findByEmail(userEmail);

	       if (currentUser != null) {
	           Set<User> connections = currentUser.getConnections();
	           model.addAttribute("connections", connections);

	           int size = 5; // Number of transactions per page
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


 }

