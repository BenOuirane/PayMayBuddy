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
	 @Transactional
	 public void createTransaction(String receiverEmail, String description, double amount) {
	     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	     String senderEmail = authentication.getName();
	     
	     // Retrieve users
	     User sender = userService.findByEmail(senderEmail);
	     User receiver = userService.findByEmail(receiverEmail);
	     
	     // Validate inputs
	     validateTransactionInputs(receiverEmail, description, amount, sender, receiver);
	     
	     // Calculate transaction fees
	     double fee = amount * 0.005;
	     double amountAfterFee = amount - fee;
	     validateAmountAfterFee(amountAfterFee);
	     
	     // Process transfer
	     processTransfer(sender, receiver, amount, fee);
	     
	     // Record transaction
	     recordTransaction(sender, receiver, description, amount, fee);
	 }

	 private void validateTransactionInputs(String receiverEmail, String description, double amount, User sender, User receiver) {
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
	 }

	 private void validateAmountAfterFee(double amountAfterFee) {
	     if (amountAfterFee <= 0) {
	         throw new IllegalArgumentException("Le montant après déduction des frais doit être supérieur à zéro.");
	     }
	 }

	 private void processTransfer(User sender, User receiver, double amount, double fee) {
	     sender.setWealth(sender.getWealth() - amount - fee);
	     receiver.setWealth(receiver.getWealth() + amount);
	     
	     User systemAccount = userService.findSystemAccount();
	     systemAccount.setWealth(systemAccount.getWealth() + fee);
	     
	     userService.save(sender);
	     userService.save(receiver);
	     userService.save(systemAccount);
	 }

	 private void recordTransaction(User sender, User receiver, String description, double amount, double fee) {
	     Transaction transaction = new Transaction();
	     transaction.setSender(sender);
	     transaction.setReceiver(receiver);
	     transaction.setDescription(description);
	     transaction.setAmount(amount);
	     transaction.setTransactionDate(new Date());
	     transaction.setFee(fee);
	     transactionRepository.save(transaction);
	 }


	 @Override
	 public Page<Transaction> getTransactionsForUser(String userEmail, int page, int size) {
	     Pageable pageable = PageRequest.of(page, size);
	     return transactionRepository.findBySenderEmailAndReceiverEmailNot(userEmail, userEmail, pageable);
	 }


}
