package com.projet6.PayMyBuddy.controllerTest;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.ServiceImpl.SoldeServiceImpl;
import com.projet6.PayMyBuddy.ServiceImpl.TransactionServiceImpl;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Transaction;
import org.springframework.security.core.userdetails.User;


@SpringBootTest
@AutoConfigureMockMvc
public class SoldeControllerTest {
	
	    @Autowired
	    private MockMvc mockMvc;

	    @MockBean
	    private SoldeServiceImpl soldeService;

	    @MockBean
	    private TransactionServiceImpl transactionServiceImpl;

	    @MockBean
	    private UserService userService;
	    
	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.openMocks(this);

	        // Create mock UserDetails for authentication using Spring Security's User class
	        UserDetails userDetails = User.withUsername("test@example.com")
	                                      .password("password")
	                                      .roles("USER") // Add roles if needed
	                                      .build();
	        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
	        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
	        securityContext.setAuthentication(authentication);
	        SecurityContextHolder.setContext(securityContext);
	    }
	    
	    
	    
	    @Test
	    public void testGetSoldePage_UserFound() throws Exception {
	        // Setup mock user and transactions
	        com.projet6.PayMyBuddy.model.User mockUser = new com.projet6.PayMyBuddy.model.User();
	        mockUser.setEmail("test@example.com");
	        mockUser.setWealth(1000.0);

	        Page<Transaction> transactionPage = new PageImpl<>(Collections.emptyList());

	        when(soldeService.getCurrentUser()).thenReturn(mockUser);
	        when(soldeService.getSelfTransactions(eq("test@example.com"), anyInt(), anyInt())).thenReturn(transactionPage);

	        // Perform the GET request
	        mockMvc.perform(get("/solde")
	                .param("page", "0")
	                .param("size", "5"))
	                .andExpect(status().isOk())
	                .andExpect(view().name("solde-page"))
	                .andExpect(model().attribute("solde", 1000.0))
	                .andExpect(model().attribute("transactionPage", transactionPage))
	                .andExpect(model().attribute("transactions", transactionPage.getContent()));

	        // Verify interactions
	        verify(soldeService).getCurrentUser();
	        verify(soldeService).getSelfTransactions(eq("test@example.com"), anyInt(), anyInt());
	    }

	    @Test
	    public void testGetSoldePage_UserNotFound() throws Exception {
	        // Setup mock to return null for current user
	        when(soldeService.getCurrentUser()).thenReturn(null);

	        // Perform the GET request
	        mockMvc.perform(get("/solde")
	                .param("page", "0")
	                .param("size", "5"))
	                .andExpect(status().isOk())
	                .andExpect(view().name("solde-page"))
	                .andExpect(model().attribute("solde", 0))
	                .andExpect(model().attribute("transactionPage", Page.empty()))
	                .andExpect(model().attribute("transactions", Collections.emptyList()));

	        // Verify interactions
	        verify(soldeService).getCurrentUser();
	        verify(soldeService, never()).getSelfTransactions(anyString(), anyInt(), anyInt());
	    }
	    
	    @Test
	    public void testProcessTransfer_Success() throws Exception {
	        // Mock service response for creating a transaction
	        doNothing().when(soldeService).createTransaction(anyString(), anyDouble());

	        // Mock the getSoldePage method if necessary
	        // Assuming getSoldePage returns a view name and model attributes
	        // when(soldeService.getSoldePage(0, 5, any(Model.class))).thenReturn("solde-page");

	        // Perform the POST request
	        mockMvc.perform(post("/Transfer")
	                .param("description", "Test Transfer")
	                .param("amount", "100.0")
	                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
	                .andExpect(status().isOk())
	                .andExpect(view().name("solde-page"))
	                .andExpect(model().attribute("message", "Transfer successful!"));
	    }
	    
	    
	    @Test
	    public void testProcessTransfer_InvalidAmount() throws Exception {
	        // Perform the POST request with an invalid amount
	        mockMvc.perform(post("/Transfer")
	                .param("description", "Test Transfer")
	                .param("amount", "invalidAmount")
	                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
	                .andExpect(status().isOk())
	                .andExpect(view().name("solde-page"))
	                .andExpect(model().attribute("message", "The amount must be a valid number."));
	    }

	    @Test
	    public void testProcessTransfer_Exception() throws Exception {
	        // Mock behavior for exception
	        doThrow(new RuntimeException("Some error")).when(soldeService).createTransaction(anyString(), anyDouble());

	        // Perform the POST request
	        mockMvc.perform(post("/Transfer")
	                .param("description", "Test Transfer")
	                .param("amount", "100.0")
	                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
	                .andExpect(status().isOk())
	                .andExpect(view().name("solde-page"))
	                .andExpect(model().attribute("message", "Error during the transfer: Some error"));
	    }

	    @Test
	    public void testProcessTransfer_UserNotFound() throws Exception {
	        // Mock behavior for user not found
	        doThrow(new UserNotFoundException("User not found")).when(soldeService).createTransaction(anyString(), anyDouble());

	        // Perform the POST request
	        mockMvc.perform(post("/Transfer")
	                .param("description", "Test Transfer")
	                .param("amount", "100.0")
	                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
	                .andExpect(status().isOk())
	                .andExpect(view().name("solde-page"))
	                .andExpect(model().attribute("message", "User not found"));
	    }    
	    
	
}
