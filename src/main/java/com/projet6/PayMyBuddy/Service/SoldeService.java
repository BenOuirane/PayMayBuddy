package com.projet6.PayMyBuddy.Service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;

@Service
public interface SoldeService {
	
	double getCurrentUserSolde();


    Page<Transaction> getTransactionsForUser(String userEmail, int page, int size);

	User getCurrentUser();

	Page<Transaction> getUserTransactions();

	void createTransaction(String description, double amount);
	
    Page<Transaction> getSelfTransactions(String userEmail, int page, int size);


}
