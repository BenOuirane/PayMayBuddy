package com.projet6.PayMyBuddy.ServiceImpl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projet6.PayMyBuddy.Service.SoldeService;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.TransactionRepository;
import com.projet6.PayMyBuddy.repository.UserRepository;


@Service
@Transactional
public class SoldeServiceImpl implements SoldeService{
	
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;
 

    @Override
    public double getCurrentUserSolde() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        return currentUser != null ? currentUser.getWealth() : 0;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail);
    }

    @Override
    public Page<Transaction> getUserTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Pageable pageable = PageRequest.of(0, 5); // Default page and size
        return transactionRepository.findBySenderEmail(userEmail, pageable);
    }

    @Override
    public void createTransaction(String description, double amount) {
        User currentUser = getCurrentUser();

        // Validate inputs and conditions
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Le champ 'Description' ne peut pas être vide.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }
        if (currentUser == null) {
            throw new UserNotFoundException("Utilisateur non trouvé.");
        }

        // Set the receiver as the current user
        User receiver = currentUser;

        // Create transaction object
        Transaction transaction = new Transaction();
        transaction.setSender(currentUser);
        transaction.setReceiver(receiver);
        transaction.setDescription(description);
        transaction.setTransactionDate(new Date());
        transaction.setAmount(amount);
        transaction.setFee(0); // Assuming no fee for adding money to own account

        // Save transaction to database
        transactionRepository.save(transaction);

        // Update user's wealth
        currentUser.setWealth(currentUser.getWealth() + amount);
        userRepository.save(currentUser);
    }


    @Override
    public Page<Transaction> getTransactionsForUser(String userEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findBySenderEmail(userEmail, pageable);
    }

    private User findSystemAccount() {
        return userRepository.findByEmail("system@domain.com");
    }

    @Override
    public Page<Transaction> getSelfTransactions(String userEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findBySenderEmailAndReceiverEmail(userEmail, userEmail, pageable);
    }


}
