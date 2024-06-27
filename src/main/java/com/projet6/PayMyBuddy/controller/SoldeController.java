package com.projet6.PayMyBuddy.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.ServiceImpl.SoldeServiceImpl;
import com.projet6.PayMyBuddy.ServiceImpl.TransactionServiceImpl;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;

@Controller
public class SoldeController {
	
    @Autowired
     SoldeServiceImpl  soldeService;
    
    @Autowired
	   TransactionServiceImpl transactionServiceImpl;
	   
	   @Autowired
	   UserService userService;
	   
	   @GetMapping("/solde")
	    public String getSoldePage(@RequestParam(value = "page", defaultValue = "0") int page, 
	                               @RequestParam(value = "size", defaultValue = "5") int size, 
	                               Model model) {
	        User currentUser = soldeService.getCurrentUser();

	        if (currentUser != null) {
	            double solde = currentUser.getWealth();
	            model.addAttribute("solde", solde);

	            Page<Transaction> transactionPage = soldeService.getSelfTransactions(currentUser.getEmail(), page, size);

	            model.addAttribute("transactionPage", transactionPage != null ? transactionPage : Page.empty());
	            model.addAttribute("transactions", transactionPage != null ? transactionPage.getContent() : Collections.emptyList());
	        } else {
	            model.addAttribute("solde", 0);
	            model.addAttribute("transactionPage", Page.empty());
	            model.addAttribute("transactions", Collections.emptyList());
	        }

	        return "solde-page";
	    }
		   
	    @PostMapping("/Transfer")
	    public String processTransfer(@RequestParam("description") String description,
	                                  @RequestParam("amount") String amountStr,
	                                  Model model) {
	        try {
	            double amount = Double.parseDouble(amountStr);
	            soldeService.createTransaction(description, amount);
	            model.addAttribute("message", "Transfer successful!");
	        } catch (NumberFormatException e) {
	            model.addAttribute("message", "The amount must be a valid number.");
	        } catch (IllegalArgumentException | UserNotFoundException e) {
	            model.addAttribute("message", e.getMessage());
	        } catch (Exception e) {
	            model.addAttribute("message", "Error during the transfer: " + e.getMessage());
	        }

	        return getSoldePage(0, 5, model); // Reload user data and transactions
	    }
	
	    
}
