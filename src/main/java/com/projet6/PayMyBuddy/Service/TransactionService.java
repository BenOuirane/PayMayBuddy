package com.projet6.PayMyBuddy.Service;

import org.springframework.stereotype.Service;

@Service
public interface TransactionService {
	
	public void createTransaction(String receiverEmail, String description, double amount);

}
