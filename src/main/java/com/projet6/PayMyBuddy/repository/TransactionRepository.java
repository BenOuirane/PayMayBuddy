package com.projet6.PayMyBuddy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>{
	
    Page<Transaction> findBySenderOrReceiver(User sender, User receiver, Pageable pageable);
    Page<Transaction> findBySenderEmail(String senderEmail, Pageable pageable);
    Page<Transaction> findBySenderEmailAndReceiverEmail(String senderEmail, String receiverEmail, Pageable pageable);
    Page<Transaction> findBySenderEmailAndReceiverEmailNot(String senderEmail, String receiverEmail, Pageable pageable);
}
