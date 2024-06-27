package com.projet6.PayMyBuddy.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTransaction;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;
    private String description;
    private Date transactionDate;
    private double amount;
    private double fee; // Add this line
	public int getIdTransaction() {
		return idTransaction;
	}
	public void setIdTransaction(int idTransaction) {
		this.idTransaction = idTransaction;
	}
	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}
	public User getReceiver() {
		return receiver;
	}
	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public Transaction(int idTransaction, User sender, User receiver,
			String description, Date transactionDate, double amount,
			double fee) {
		super();
		this.idTransaction = idTransaction;
		this.sender = sender;
		this.receiver = receiver;
		this.description = description;
		this.transactionDate = transactionDate;
		this.amount = amount;
		this.fee = fee;
	}
	public Transaction() {
		super();
	}
	public Transaction(User currentUser, User receiver2, String description2,
			Date date, double amount2, double fee2) {
		// TODO Auto-generated constructor stub
	}

	
}
