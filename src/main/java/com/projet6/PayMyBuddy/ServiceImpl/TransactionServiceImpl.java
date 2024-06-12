package com.projet6.PayMyBuddy.ServiceImpl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.projet6.PayMyBuddy.Service.TransactionService;
import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService{
	
	 @Autowired
	 private TransactionRepository transactionRepository;
	 @Autowired
	 private UserService userService;

	@Override
	public void createTransaction(String receiverEmail, String description, double amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        // Récupérer les utilisateurs
        User sender = userService.findByEmail(senderEmail);
        User receiver = userService.findByEmail(receiverEmail);
        // Validation des entrées
        if (receiverEmail == null || receiverEmail.isEmpty()) {
            throw new IllegalArgumentException("Le champ 'Relation' ne peut pas être vide.");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Le champ 'Description' ne peut pas être vide.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }
        if (sender == null) {
            throw new UserNotFoundException("Expéditeur non trouvé.");
        }
        if (receiver == null) {
            throw new UserNotFoundException("Destinataire non trouvé.");
        }
        if (!sender.getConnections().contains(receiver)) {
            throw new IllegalArgumentException("Le destinataire n'est pas dans les connexions de l'expéditeur.");
        }
        if (sender.getWealth() < amount) {
            throw new IllegalArgumentException("Solde insuffisant.");
        }
        // Processus de transfert
        sender.setWealth(sender.getWealth() - amount);
        receiver.setWealth(receiver.getWealth() + amount);
        // Mise à jour des soldes
     //   userService.save(sender);
     //   userService.save(receiver);
        // Enregistrer la transaction
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setTransactionDate(new Date());
        transactionRepository.save(transaction);
    }

	@Override
	public Page<Transaction> getTransactionsForUser(String userEmail, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findBySenderEmail(userEmail, pageable);
    }

}
