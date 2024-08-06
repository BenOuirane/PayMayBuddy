package com.projet6.PayMyBuddy.serviceImplTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import com.projet6.PayMyBuddy.ServiceImpl.UserServiceImpl;
import com.projet6.PayMyBuddy.dto.UserRegistrationDto;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Role;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
	@Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto userRegistrationDto;
    
    @Mock
    private Authentication authentication;
    
    private static final String SYSTEM_ACCOUNT_EMAIL = "system@domain.com";
    

    @BeforeEach
    public void setUp() {
        userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setUsername("testuser");
        userRegistrationDto.setEmail("testuser@example.com");
        userRegistrationDto.setPassword("password");
    }

    @Test
    public void testSaveUser() {
        User user = new User("testuser", "testuser@example.com", "encodedPassword", Arrays.asList(new Role("ROLE-USER")));
        
        when(passwordEncoder.encode(userRegistrationDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.saveUser(userRegistrationDto);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("testuser@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(1, savedUser.getRoles().size());
      //  assertEquals("ROLE-USER", savedUser.getRoles().get(0).getName());

        verify(passwordEncoder, times(1)).encode(userRegistrationDto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    public void testLoadUserByUsername_UserExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setRoles(Arrays.asList(new Role("ROLE-USER")));

        when(userRepository.findByEmail(anyString())).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("testuser@example.com");

        assertNotNull(userDetails);
        assertEquals("testuser@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistentuser@example.com");
        });
    }
    
    
    @Test
    public void testFindByEmail_UserExists() {
        // Arrange
        String email = "testuser@example.com";
        User user = new User();
        user.setEmail(email);
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByEmail(email);
    }
    
    @Test
    public void testFindByEmail_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertNull(result);
        verify(userRepository).findByEmail(email);
    }
    
    @Test
    public void testUpdateProfile_UserExists_WithPassword() {
        // Arrange
        String username = "newUsername";
        String email = "newEmail@example.com";
        String password = "newPassword";
        String currentUserEmail = "currentUser@example.com";
        
        User currentUser = new User();
        currentUser.setUsername("oldUsername");
        currentUser.setEmail(currentUserEmail);
        currentUser.setPassword("oldPassword");
        
        // Mock the SecurityContext and set it in SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUserEmail);
        when(userRepository.findByEmail(currentUserEmail)).thenReturn(currentUser);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // Act
        boolean result = userService.updateProfile(username, email, password, mock(Model.class));

        // Assert
        assertTrue(result);
        assertEquals(username, currentUser.getUsername());
        assertEquals(email, currentUser.getEmail());
        assertEquals("encodedPassword", currentUser.getPassword());
        verify(userRepository).save(currentUser);
    }

    @Test
    public void testUpdateProfile_UserExists_WithoutPassword() {
        // Arrange
        String username = "newUsername";
        String email = "newEmail@example.com";
        String password = "";
        String currentUserEmail = "currentUser@example.com";

        User currentUser = new User();
        currentUser.setUsername("oldUsername");
        currentUser.setEmail(currentUserEmail);
        currentUser.setPassword("oldPassword");
        
        // Mock the SecurityContext and set it in SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUserEmail);
        when(userRepository.findByEmail(currentUserEmail)).thenReturn(currentUser);

        // Act
        boolean result = userService.updateProfile(username, email, password, mock(Model.class));

        // Assert
        assertTrue(result);
        assertEquals(username, currentUser.getUsername());
        assertEquals(email, currentUser.getEmail());
        assertEquals("oldPassword", currentUser.getPassword());
        verify(userRepository).save(currentUser);
    }

    @Test
    public void testUpdateProfile_UserNotFound() {
        // Arrange
        String username = "newUsername";
        String email = "newEmail@example.com";
        String password = "newPassword";
        String currentUserEmail = "currentUser@example.com";
        
        // Mock the SecurityContext and set it in SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUserEmail);
        when(userRepository.findByEmail(currentUserEmail)).thenReturn(null);

        // Act
        boolean result = userService.updateProfile(username, email, password, mock(Model.class));

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }
    
    
    @Test
    public void testFindSystemAccount_UserExists() {
        // Arrange
        User systemUser = new User();
        systemUser.setEmail(SYSTEM_ACCOUNT_EMAIL);

        when(userRepository.findByEmail(SYSTEM_ACCOUNT_EMAIL)).thenReturn(systemUser);

        // Act
        User result = userService.findSystemAccount();

        // Assert
        assertNotNull(result);
        assertEquals(SYSTEM_ACCOUNT_EMAIL, result.getEmail());
    }

    @Test
    public void testFindSystemAccount_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(SYSTEM_ACCOUNT_EMAIL)).thenReturn(null);

        // Act & Assert
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            userService.findSystemAccount();
        });

        assertEquals("Compte système non trouvé.", thrown.getMessage());
    }
    
    
    @Test
    public void testSave() {
        // Arrange
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.save(user);

        // Assert
        assertNotNull(result);
        assertEquals("testuser@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
        assertEquals("password", result.getPassword());
        verify(userRepository).save(user);
    }
    

}
