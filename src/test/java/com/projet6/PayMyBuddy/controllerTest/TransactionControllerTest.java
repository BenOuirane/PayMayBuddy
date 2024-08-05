package com.projet6.PayMyBuddy.controllerTest;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import com.projet6.PayMyBuddy.Service.ConnectionService;
import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.ServiceImpl.TransactionServiceImpl;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionServiceImpl transactionServiceImpl;

    @MockBean
    private UserService userService;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private UserRepository userRepository;

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
    public void testGetRalation_Success() throws Exception {
        // Create a mock custom User entity with necessary details
        com.projet6.PayMyBuddy.model.User mockUser = new com.projet6.PayMyBuddy.model.User();
        mockUser.setEmail("test@example.com");

        // Create another mock user to be in connections
        com.projet6.PayMyBuddy.model.User connectionUser = new com.projet6.PayMyBuddy.model.User();
        connectionUser.setEmail("connection@example.com");

        // Set the connections
        Set<com.projet6.PayMyBuddy.model.User> connections = new HashSet<>();
        connections.add(connectionUser);
        mockUser.setConnections(connections); // Set connections to mockUser

        // Mock service responses
        Page<Transaction> transactionPage = new PageImpl<>(Collections.emptyList()); // Mock empty transaction page

        when(userService.findByEmail(anyString())).thenReturn(mockUser);
        when(transactionServiceImpl.getTransactionsForUser(anyString(), anyInt(), anyInt())).thenReturn(transactionPage);

        // Perform the GET request
        mockMvc.perform(get("/transfer/page")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer-page"))
                .andExpect(model().attribute("connections", connections))
                .andExpect(model().attribute("transactionPage", transactionPage));
    }

    @Test
    public void testGetRalation_UserNotFound() throws Exception {
        // Mock behavior for user not found
        when(userService.findByEmail(anyString())).thenReturn(null);

        // Perform the GET request
        mockMvc.perform(get("/transfer/page")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer-page"))
                .andExpect(model().attribute("transactionPage", Page.empty()));
    }
    
    
    
    
    
   
    @Test
    public void testProcessTransfer_Success() throws Exception {
        // Create a mock custom User entity with necessary details
        com.projet6.PayMyBuddy.model.User mockUser = new com.projet6.PayMyBuddy.model.User();
        mockUser.setEmail("test@example.com");

        // Create another mock user to be in connections
        com.projet6.PayMyBuddy.model.User connectionUser = new com.projet6.PayMyBuddy.model.User();
        connectionUser.setEmail("connection@example.com");

        // Set the connections
        Set<com.projet6.PayMyBuddy.model.User> connections = new HashSet<>();
        connections.add(connectionUser);
        mockUser.setConnections(connections);

        // Mock service responses
        Page<Transaction> transactionPage = new PageImpl<>(Collections.emptyList());

        when(userService.findByEmail(anyString())).thenReturn(mockUser);
        when(transactionServiceImpl.getTransactionsForUser(anyString(), anyInt(), anyInt())).thenReturn(transactionPage);
        doNothing().when(transactionServiceImpl).createTransaction(anyString(), anyString(), anyDouble());

        // Perform the POST request
        mockMvc.perform(post("/processTransfer")
                .param("relationOption", "connection@example.com")
                .param("description", "Test Transfer")
                .param("amount", "100.0")
                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(view().name("transfer-page"))
                .andExpect(model().attribute("successMessage", "Transfert réussi!"))
                .andExpect(model().attribute("connections", connections))
                .andExpect(model().attribute("transactionPage", transactionPage));
    }

    
    @Test
    public void testProcessTransfer_InvalidAmount() throws Exception {
        // Perform the POST request with an invalid amount
        mockMvc.perform(post("/processTransfer")
                .param("relationOption", "connection@example.com")
                .param("description", "Test Transfer")
                .param("amount", "invalidAmount")
                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(view().name("transfer-page"))
                .andExpect(model().attribute("errorMessage", "Le montant doit être un nombre valide."));
    }

    @Test
    public void testProcessTransfer_UserNotFound() throws Exception {
        // Mock behavior for user not found
        when(userService.findByEmail(anyString())).thenReturn(null);

        // Perform the POST request
        mockMvc.perform(post("/processTransfer")
                .param("relationOption", "connection@example.com")
                .param("description", "Test Transfer")
                .param("amount", "100.0")
                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(view().name("transfer-page"))
                .andExpect(model().attribute("transactionPage", Page.empty()));
    }

    @Test
    public void testProcessTransfer_Exception() throws Exception {
        // Mock behavior for exception
        doThrow(new RuntimeException("Some error")).when(transactionServiceImpl).createTransaction(anyString(), anyString(), anyDouble());

        // Perform the POST request
        mockMvc.perform(post("/processTransfer")
                .param("relationOption", "connection@example.com")
                .param("description", "Test Transfer")
                .param("amount", "100.0")
                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(view().name("transfer-page"))
                .andExpect(model().attribute("errorMessage", "Erreur lors du transfert: Some error"));
    }

}
