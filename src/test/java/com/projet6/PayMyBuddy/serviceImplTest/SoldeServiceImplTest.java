package com.projet6.PayMyBuddy.serviceImplTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.projet6.PayMyBuddy.ServiceImpl.SoldeServiceImpl;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.TransactionRepository;
import com.projet6.PayMyBuddy.repository.UserRepository;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
public class SoldeServiceImplTest {

	    @Mock
	    private UserRepository userRepository;

	    @Mock
	    private TransactionRepository transactionRepository;

	    @InjectMocks
	    private SoldeServiceImpl soldeServiceImpl; // Replace with your actual service class name

	    @Test
	    public void testGetCurrentUserSolde_UserExists() {
	        // Arrange
	        String userEmail = "user@example.com";
	        double expectedWealth = 1000.0;

	        User currentUser = new User();
	        currentUser.setEmail(userEmail);
	        currentUser.setWealth(expectedWealth);

	        Authentication authentication = mock(Authentication.class);
	        when(authentication.getName()).thenReturn(userEmail);

	        SecurityContext securityContext = mock(SecurityContext.class);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        SecurityContextHolder.setContext(securityContext);

	        when(userRepository.findByEmail(userEmail)).thenReturn(currentUser);

	        // Act
	        double actualWealth = soldeServiceImpl.getCurrentUserSolde();

	        // Assert
	        assertEquals(expectedWealth, actualWealth, "The wealth should match the expected value.");
	    }

	    @Test
	    public void testGetCurrentUserSolde_UserDoesNotExist() {
	        // Arrange
	        String userEmail = "user@example.com";

	        Authentication authentication = mock(Authentication.class);
	        when(authentication.getName()).thenReturn(userEmail);

	        SecurityContext securityContext = mock(SecurityContext.class);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        SecurityContextHolder.setContext(securityContext);

	        when(userRepository.findByEmail(userEmail)).thenReturn(null);

	        // Act
	        double actualWealth = soldeServiceImpl.getCurrentUserSolde();

	        // Assert
	        assertEquals(0, actualWealth, "The wealth should be 0 when the user does not exist.");
	    }
	    
	    @Test
	    public void testGetCurrentUser_UserExists() {
	        // Arrange
	        String userEmail = "user@example.com";
	        User currentUser = new User();
	        currentUser.setEmail(userEmail);

	        Authentication authentication = mock(Authentication.class);
	        when(authentication.getName()).thenReturn(userEmail);

	        SecurityContext securityContext = mock(SecurityContext.class);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        SecurityContextHolder.setContext(securityContext);

	        when(userRepository.findByEmail(userEmail)).thenReturn(currentUser);

	        // Act
	        User actualUser = soldeServiceImpl.getCurrentUser();

	        // Assert
	        assertNotNull(actualUser, "The user should not be null.");
	        assertEquals(userEmail, actualUser.getEmail(), "The email of the retrieved user should match.");
	    }

	    @Test
	    public void testGetCurrentUser_UserDoesNotExist() {
	        // Arrange
	        String userEmail = "user@example.com";

	        Authentication authentication = mock(Authentication.class);
	        when(authentication.getName()).thenReturn(userEmail);

	        SecurityContext securityContext = mock(SecurityContext.class);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        SecurityContextHolder.setContext(securityContext);

	        when(userRepository.findByEmail(userEmail)).thenReturn(null);

	        // Act
	        User actualUser = soldeServiceImpl.getCurrentUser();

	        // Assert
	        assertNull(actualUser, "The user should be null when no user is found.");
	    }
	   
	    
	    @Test
	    public void testGetUserTransactions() {
	        // Arrange
	        String userEmail = "user@example.com";
	        Pageable pageable = PageRequest.of(0, 5);

	        User user = new User();
	        user.setEmail(userEmail);

	        Transaction transaction = new Transaction();
	        transaction.setSender(user); // Set the User object, not just email

	        Page<Transaction> transactionPage = new PageImpl<>(Collections.singletonList(transaction), pageable, 1);

	        Authentication authentication = mock(Authentication.class);
	        when(authentication.getName()).thenReturn(userEmail);

	        SecurityContext securityContext = mock(SecurityContext.class);
	        when(securityContext.getAuthentication()).thenReturn(authentication);
	        SecurityContextHolder.setContext(securityContext);

	        when(transactionRepository.findBySenderEmail(userEmail, pageable)).thenReturn(transactionPage);

	        // Act
	        Page<Transaction> result = soldeServiceImpl.getUserTransactions();

	        // Assert
	        assertNotNull(result, "The result should not be null.");
	        assertEquals(1, result.getTotalElements(), "The result should contain one transaction.");
	        assertEquals(userEmail, result.getContent().get(0).getSender().getEmail(), "The sender's email should match the current user's email.");
	    }
	    
	 
	    @Test
	    public void testCreateTransaction_ValidInput() {
	        // Arrange
	        String description = "Payment for services";
	        double amount = 100.0;

	        User currentUser = new User();
	        currentUser.setEmail("user@example.com");
	        currentUser.setWealth(500.0);

	        Transaction transaction = new Transaction();
	        transaction.setSender(currentUser);
	        transaction.setReceiver(currentUser);
	        transaction.setDescription(description);
	        transaction.setTransactionDate(new Date());
	        transaction.setAmount(amount);
	        transaction.setFee(0);

	        // Mock methods
	        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(currentUser);
	        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
	        when(userRepository.save(any(User.class))).thenReturn(currentUser);

	        // Mock getCurrentUser
	        when(soldeServiceImpl.getCurrentUser()).thenReturn(currentUser);

	        // Act
	        soldeServiceImpl.createTransaction(description, amount);

	        // Assert
	        verify(transactionRepository).save(any(Transaction.class));
	        verify(userRepository).save(currentUser);

	        assertEquals(600.0, currentUser.getWealth(), "The user's wealth should be updated.");
	    }

	    @Test
	    public void testCreateTransaction_EmptyDescription() {
	        // Arrange
	        String description = "";
	        double amount = 100.0;

	        // Mock getCurrentUser
	        User currentUser = new User();
	        currentUser.setEmail("user@example.com");
	        currentUser.setWealth(500.0);
	        when(soldeServiceImpl.getCurrentUser()).thenReturn(currentUser);

	        // Act & Assert
	        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
	        	soldeServiceImpl.createTransaction(description, amount);
	        });

	        assertEquals("Le champ 'Description' ne peut pas être vide.", thrown.getMessage());
	    }

	    @Test
	    public void testCreateTransaction_NonPositiveAmount() {
	        // Arrange
	        String description = "Payment for services";
	        double amount = 0.0;

	        // Mock getCurrentUser
	        User currentUser = new User();
	        currentUser.setEmail("user@example.com");
	        currentUser.setWealth(500.0);
	        when(soldeServiceImpl.getCurrentUser()).thenReturn(currentUser);

	        // Act & Assert
	        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
	        	soldeServiceImpl.createTransaction(description, amount);
	        });

	        assertEquals("Le montant doit être supérieur à zéro.", thrown.getMessage());
	    }

	    @Test
	    public void testCreateTransaction_UserNotFound() {
	        // Arrange
	        String description = "Payment for services";
	        double amount = 100.0;

	        // Mock getCurrentUser to return null
	        when(soldeServiceImpl.getCurrentUser()).thenReturn(null);

	        // Act & Assert
	        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
	        	soldeServiceImpl.createTransaction(description, amount);
	        });

	        assertEquals("Utilisateur non trouvé.", thrown.getMessage());
	    }
	    
	    @Test
	    public void testGetTransactionsForUser() {
	        // Arrange
	        String userEmail = "user@example.com";
	        int page = 0;
	        int size = 5;

	        // Create a list of transactions
	        Transaction transaction1 = new Transaction();
	        transaction1.setDescription("Payment 1");
	        Transaction transaction2 = new Transaction();
	        transaction2.setDescription("Payment 2");

	        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

	        // Create a Page object containing the transactions
	        Page<Transaction> transactionPage = new PageImpl<>(transactions, PageRequest.of(page, size), transactions.size());

	        // Mock the repository method
	        when(transactionRepository.findBySenderEmail(userEmail, PageRequest.of(page, size))).thenReturn(transactionPage);

	        // Act
	        Page<Transaction> result = soldeServiceImpl.getTransactionsForUser(userEmail, page, size);

	        // Assert
	        assertNotNull(result);
	        assertEquals(2, result.getTotalElements());
	        assertEquals(2, result.getContent().size());
	        assertEquals("Payment 1", result.getContent().get(0).getDescription());
	        assertEquals("Payment 2", result.getContent().get(1).getDescription());

	        // Verify that the repository method was called with the correct arguments
	        verify(transactionRepository).findBySenderEmail(userEmail, PageRequest.of(page, size));
	    }
	    
	    @Test
	    public void testGetSelfTransactions() {
	        // Arrange
	        String userEmail = "user@example.com";
	        int page = 0;
	        int size = 5;

	        // Create a list of transactions where the user is both sender and receiver
	        Transaction transaction1 = new Transaction();
	        transaction1.setDescription("Self Payment 1");
	        Transaction transaction2 = new Transaction();
	        transaction2.setDescription("Self Payment 2");

	        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

	        // Create a Page object containing the transactions
	        Page<Transaction> transactionPage = new PageImpl<>(transactions, PageRequest.of(page, size), transactions.size());

	        // Mock the repository method
	        when(transactionRepository.findBySenderEmailAndReceiverEmail(userEmail, userEmail, PageRequest.of(page, size))).thenReturn(transactionPage);

	        // Act
	        Page<Transaction> result = soldeServiceImpl.getSelfTransactions(userEmail, page, size);

	        // Assert
	        assertNotNull(result);
	        assertEquals(2, result.getTotalElements());
	        assertEquals(2, result.getContent().size());
	        assertEquals("Self Payment 1", result.getContent().get(0).getDescription());
	        assertEquals("Self Payment 2", result.getContent().get(1).getDescription());

	        // Verify that the repository method was called with the correct arguments
	        verify(transactionRepository).findBySenderEmailAndReceiverEmail(userEmail, userEmail, PageRequest.of(page, size));
	    }
	    
}
