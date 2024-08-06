package com.projet6.PayMyBuddy.serviceImplTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import com.projet6.PayMyBuddy.Service.TransactionService;
import com.projet6.PayMyBuddy.ServiceImpl.ConnectionServiceImpl;
import com.projet6.PayMyBuddy.exception.ConnectionAlreadyExistsException;
import com.projet6.PayMyBuddy.exception.SelfConnectionException;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@ExtendWith(MockitoExtension.class)
public class ConnectionServiceImplTest {
	
	@Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService; // This is not used in the current method but is part of the class

    @InjectMocks
    private ConnectionServiceImpl connectionService;
    
    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Test
    public void testAddUserConnection_CurrentUserNotFound() {
        // Arrange
        String currentUserEmail = "currentuser@example.com";
        String relationEmail = "relationuser@example.com";

        when(userRepository.findByEmail(currentUserEmail)).thenReturn(null);

        // Act & Assert
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            connectionService.addUserConnection(currentUserEmail, relationEmail);
        });

        assertEquals("Current user not found", thrown.getMessage());
    }

    @Test
    public void testAddUserConnection_SelfConnection() {
        // Arrange
        String currentUserEmail = "currentuser@example.com";
        String relationEmail = currentUserEmail;

        User currentUser = new User();
        currentUser.setEmail(currentUserEmail);

        when(userRepository.findByEmail(currentUserEmail)).thenReturn(currentUser);

        // Act & Assert
        SelfConnectionException thrown = assertThrows(SelfConnectionException.class, () -> {
            connectionService.addUserConnection(currentUserEmail, relationEmail);
        });

        assertEquals("You cannot add yourself as a connection", thrown.getMessage());
    }

    @Test
    public void testAddUserConnection_RelationUserNotFound() {
        // Arrange
        String currentUserEmail = "currentuser@example.com";
        String relationEmail = "relationuser@example.com";

        User currentUser = new User();
        currentUser.setEmail(currentUserEmail);

        when(userRepository.findByEmail(currentUserEmail)).thenReturn(currentUser);
        when(userRepository.findByEmail(relationEmail)).thenReturn(null);

        // Act & Assert
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            connectionService.addUserConnection(currentUserEmail, relationEmail);
        });

        assertEquals("User to add not found", thrown.getMessage());
    }

    @Test
    public void testAddUserConnection_ConnectionAlreadyExists() {
        // Arrange
        String currentUserEmail = "currentuser@example.com";
        String relationEmail = "relationuser@example.com";

        User currentUser = new User();
        currentUser.setEmail(currentUserEmail);
        User relationUser = new User();
        relationUser.setEmail(relationEmail);

        Set<User> connections = new HashSet<>();
        connections.add(relationUser);
        currentUser.setConnections(connections);

        when(userRepository.findByEmail(currentUserEmail)).thenReturn(currentUser);
        when(userRepository.findByEmail(relationEmail)).thenReturn(relationUser);

        // Act & Assert
        ConnectionAlreadyExistsException thrown = assertThrows(ConnectionAlreadyExistsException.class, () -> {
            connectionService.addUserConnection(currentUserEmail, relationEmail);
        });

        assertEquals("This connection already exists", thrown.getMessage());
    }

    @Test
    public void testAddUserConnection_SuccessfulConnection() {
        // Arrange
        String currentUserEmail = "currentuser@example.com";
        String relationEmail = "relationuser@example.com";

        User currentUser = new User();
        currentUser.setEmail(currentUserEmail);
        User relationUser = new User();
        relationUser.setEmail(relationEmail);

        when(userRepository.findByEmail(currentUserEmail)).thenReturn(currentUser);
        when(userRepository.findByEmail(relationEmail)).thenReturn(relationUser);
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        // Act
        connectionService.addUserConnection(currentUserEmail, relationEmail);

        // Assert
        verify(userRepository).save(currentUser);
        assertTrue(currentUser.getConnections().contains(relationUser));
    }
    
    
    @Test
    public void testGetConnectionsToTransferAmounOfMoney_EmailFromSession() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn("user@example.com");

        User currentUser = new User();
        currentUser.setEmail("user@example.com");
        Set<User> connections = new HashSet<>();
        currentUser.setConnections(connections);

        Page<Transaction> transactionPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findByEmail("user@example.com")).thenReturn(currentUser);
        when(transactionService.getTransactionsForUser("user@example.com", 0, 5)).thenReturn(transactionPage);

        // Act
        String viewName = connectionService.getConnectionsToTransferAmounOfMoney(0, model, request);

        // Assert
        assertEquals("transfer-page", viewName);
        verify(model).addAttribute("connections", connections);
        verify(model).addAttribute("transactionPage", transactionPage);
    }

    @Test
    public void testGetConnectionsToTransferAmounOfMoney_EmailFromSecurityContext_OAuth2User() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(null);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setEmail("user@example.com");
        Set<User> connections = new HashSet<>();
        currentUser.setConnections(connections);

        // Act
        String viewName = connectionService.getConnectionsToTransferAmounOfMoney(0, model, request);

        // Assert
        assertEquals("transfer-page", viewName);

    }
    
    @Test
    public void testGetConnectionsToTransferAmounOfMoney_UserNotFound() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(null);

        // Act
        String viewName = connectionService.getConnectionsToTransferAmounOfMoney(0, model, request);

        // Assert
        assertEquals("transfer-page", viewName);
        verify(model).addAttribute("errorMessage", "Utilisateur non trouvé.");

    }
    
    @Test
    public void testGetConnectionsToTransferAmounOfMoney_EmailFromSecurityContext_UserDetails() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(null);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setEmail("user@example.com");
        Set<User> connections = new HashSet<>();
        currentUser.setConnections(connections);

        // Act
        String viewName = connectionService.getConnectionsToTransferAmounOfMoney(0, model, request);

        // Assert
        assertEquals("transfer-page", viewName);

    }  
   
}
