package com.projet6.PayMyBuddy.Service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.projet6.PayMyBuddy.model.Transaction;

@Service
public interface TransactionService {
	
	public void createTransaction(String receiverEmail, String description, double amount);
 
	Page<Transaction> getTransactionsForUser(String userEmail, int page, int size);
	
}
