package com.projet6.PayMyBuddy.serviceImplTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.ServiceImpl.TransactionServiceImpl;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {
	
	    @Mock
	    private UserService userService;

	    @Mock
	    private TransactionRepository transactionRepository;

	    @InjectMocks
	    private TransactionServiceImpl transactionService; // Use the concrete implementation

	    @BeforeEach
	    void setUp() {
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    void testCreateTransaction_Success() {
	        // Arrange
	        String senderEmail = "sender@example.com";
	        String receiverEmail = "receiver@example.com";
	        String description = "Payment for services";
	        double amount = 100.0;

	        User sender = new User();
	        sender.setEmail(senderEmail);
	        sender.setWealth(200.0);

	        User receiver = new User();
	        receiver.setEmail(receiverEmail);
	        receiver.setWealth(150.0);

	        User systemAccount = new User();
	        systemAccount.setEmail("system@example.com");
	        systemAccount.setWealth(1000.0);

	        // Set up connections
	        Set<User> connections = new HashSet<>();
	        connections.add(receiver);

	        sender.setConnections(connections);

	        Authentication authentication = mock(Authentication.class);
	        SecurityContext securityContext = mock(SecurityContext.class);
	        SecurityContextHolder.setContext(securityContext);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        when(authentication.getName()).thenReturn(senderEmail);

	        when(userService.findByEmail(senderEmail)).thenReturn(sender);
	        when(userService.findByEmail(receiverEmail)).thenReturn(receiver);
	        when(userService.findSystemAccount()).thenReturn(systemAccount);

	        // Act
	        transactionService.createTransaction(receiverEmail, description, amount);

	        // Assert
	        verify(userService).save(sender);
	        verify(userService).save(receiver);
	        verify(userService).save(systemAccount);

	        verify(transactionRepository).save(argThat(transaction ->
	            transaction.getSender().equals(sender) &&
	            transaction.getReceiver().equals(receiver) &&
	            transaction.getDescription().equals(description) &&
	            transaction.getAmount() == amount &&
	            transaction.getFee() == amount * 0.005
	        ));
	    }


	    @Test
	    void testCreateTransaction_InvalidDescription() {
	        // Arrange
	        String receiverEmail = "receiver@example.com";
	        String invalidDescription = "";
	        double amount = 100.0;

	        User sender = new User();
	        sender.setEmail("sender@example.com");
	        sender.setWealth(200.0);

	        User receiver = new User();
	        receiver.setEmail(receiverEmail);
	        receiver.setWealth(150.0);

	        // Set up connections
	        Set<User> connections = new HashSet<>();
	        connections.add(receiver);

	        sender.setConnections(connections);

	        Authentication authentication = mock(Authentication.class);
	        SecurityContext securityContext = mock(SecurityContext.class);
	        SecurityContextHolder.setContext(securityContext);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        when(authentication.getName()).thenReturn(sender.getEmail());

	        when(userService.findByEmail(sender.getEmail())).thenReturn(sender);
	        when(userService.findByEmail(receiverEmail)).thenReturn(receiver);

	        // Act & Assert
	        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
	            transactionService.createTransaction(receiverEmail, invalidDescription, amount)
	        );
	        assertEquals("Le champ 'Description' ne peut pas être vide.", thrown.getMessage());
	    }

	    @Test
	    void testCreateTransaction_AmountZeroOrNegativeAfterFee() {
	        // Arrange
	        String receiverEmail = "receiver@example.com";
	        String description = "Payment";
	        double amount = 0.0; // Amount that will result in a zero or negative balance after fee

	        User sender = new User();
	        sender.setEmail("sender@example.com");
	        sender.setWealth(10.0); // Low wealth to trigger the issue

	        User receiver = new User();
	        receiver.setEmail(receiverEmail);
	        receiver.setWealth(150.0);

	        // Set up connections
	        Set<User> connections = new HashSet<>();
	        connections.add(receiver);

	        sender.setConnections(connections);

	        Authentication authentication = mock(Authentication.class);
	        SecurityContext securityContext = mock(SecurityContext.class);
	        SecurityContextHolder.setContext(securityContext);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        when(authentication.getName()).thenReturn(sender.getEmail());

	        when(userService.findByEmail(sender.getEmail())).thenReturn(sender);
	        when(userService.findByEmail(receiverEmail)).thenReturn(receiver);

	        // Act & Assert
	        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
	            transactionService.createTransaction(receiverEmail, description, amount)
	        );
	        assertEquals("Le montant doit être supérieur à zéro.", thrown.getMessage());
	    }

	    @Test
	    void testCreateTransaction_InsufficientFunds() {
	        // Arrange
	        String receiverEmail = "receiver@example.com";
	        String description = "Payment for services";
	        double amount = 1000.0; // Greater than sender's wealth

	        User sender = new User();
	        sender.setEmail("sender@example.com");
	        sender.setWealth(500.0);

	        User receiver = new User();
	        receiver.setEmail(receiverEmail);
	        receiver.setWealth(150.0);

	        // Set up connections
	        Set<User> connections = new HashSet<>();
	        connections.add(receiver);

	        sender.setConnections(connections);

	        Authentication authentication = mock(Authentication.class);
	        SecurityContext securityContext = mock(SecurityContext.class);
	        SecurityContextHolder.setContext(securityContext);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        when(authentication.getName()).thenReturn(sender.getEmail());

	        when(userService.findByEmail(sender.getEmail())).thenReturn(sender);
	        when(userService.findByEmail(receiverEmail)).thenReturn(receiver);

	        // Act & Assert
	        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
	            transactionService.createTransaction(receiverEmail, description, amount)
	        );
	        assertEquals("Solde insuffisant.", thrown.getMessage());
	    }

	    @Test
	    void testCreateTransaction_SenderNotFound() {
	        // Arrange
	        String receiverEmail = "receiver@example.com";
	        String description = "Payment";
	        double amount = 100.0;

	        User receiver = new User();
	        receiver.setEmail(receiverEmail);
	        receiver.setWealth(150.0);

	        Authentication authentication = mock(Authentication.class);
	        SecurityContext securityContext = mock(SecurityContext.class);
	        SecurityContextHolder.setContext(securityContext);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        when(authentication.getName()).thenReturn("nonexistent@example.com");

	        when(userService.findByEmail("nonexistent@example.com")).thenReturn(null);
	        when(userService.findByEmail(receiverEmail)).thenReturn(receiver);

	        // Act & Assert
	        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
	            transactionService.createTransaction(receiverEmail, description, amount)
	        );
	        assertEquals("Expéditeur non trouvé.", thrown.getMessage());
	    }

	    @Test
	    void testCreateTransaction_ReceiverNotFound() {
	        // Arrange
	        String senderEmail = "sender@example.com";
	        String description = "Payment";
	        double amount = 100.0;

	        User sender = new User();
	        sender.setEmail(senderEmail);
	        sender.setWealth(200.0);

	        // Set up connections
	        Set<User> connections = new HashSet<>();
	        connections.add(new User()); // Adding a dummy user as a connection

	        sender.setConnections(connections);

	        Authentication authentication = mock(Authentication.class);
	        SecurityContext securityContext = mock(SecurityContext.class);
	        SecurityContextHolder.setContext(securityContext);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        when(authentication.getName()).thenReturn(senderEmail);

	        when(userService.findByEmail(senderEmail)).thenReturn(sender);
	        when(userService.findByEmail("receiver@example.com")).thenReturn(null);

	        // Act & Assert
	        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
	            transactionService.createTransaction("receiver@example.com", description, amount)
	        );
	        assertEquals("Destinataire non trouvé.", thrown.getMessage());
	    }
	    

	    @Test
	    void testGetTransactionsForUser() {
	        // Arrange
	        String userEmail = "user@example.com";
	        int page = 0;
	        int size = 10;

	        // Create User instances
	        User sender = new User();
	        sender.setEmail(userEmail);

	        User receiver1 = new User();
	        receiver1.setEmail("receiver1@example.com");

	        User receiver2 = new User();
	        receiver2.setEmail("receiver2@example.com");

	        // Create sample transactions
	        Transaction transaction1 = new Transaction();
	        transaction1.setSender(sender);
	        transaction1.setReceiver(receiver1);

	        Transaction transaction2 = new Transaction();
	        transaction2.setSender(sender);
	        transaction2.setReceiver(receiver2);

	        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
	        Page<Transaction> transactionPage = new PageImpl<>(transactions, PageRequest.of(page, size), transactions.size());

	        // Mock the repository call
	        when(transactionRepository.findBySenderEmailAndReceiverEmailNot(userEmail, userEmail, PageRequest.of(page, size)))
	                .thenReturn(transactionPage);

	        // Act
	        Page<Transaction> result = transactionService.getTransactionsForUser(userEmail, page, size);

	        // Assert
	        assertNotNull(result);
	        assertEquals(2, result.getTotalElements());
	        assertEquals(transactions, result.getContent());
	        verify(transactionRepository).findBySenderEmailAndReceiverEmailNot(userEmail, userEmail, PageRequest.of(page, size));
	    }
	
	
}
