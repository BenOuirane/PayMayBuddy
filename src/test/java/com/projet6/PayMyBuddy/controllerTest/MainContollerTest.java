package com.projet6.PayMyBuddy.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import com.projet6.PayMyBuddy.ServiceImpl.UserServiceImpl.CustomUserDetails;
import com.projet6.PayMyBuddy.controller.MainContoller;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class MainContollerTest {
	
	@Autowired
    private MockMvc mockMvc;
	
	@Mock
    private UserRepository userRepository;

    @InjectMocks
    private MainContoller mainContoller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin() {
        String result = mainContoller.login();
        assertEquals("login-page", result);
    }


    @Test
    public void testProfilWithAuthenticatedUser() {
        // Arrange
        User user = new User(); // Ensure this User has proper details set
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUser()).thenReturn(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext); // Set the SecurityContext

        // Mocking the repository to return the user when queried by email
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        Model model = mock(Model.class); // Mock Model object

        // Act
        String result = mainContoller.profil(model, null);

        // Assert
        assertEquals("profil-page", result);
    }


    @Test
    public void testProfilWithOAuth2User() {
        // Arrange
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn("test@example.com");
        when(oauth2User.getAttribute("login")).thenReturn("testuser");
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        // Act
        String result = mainContoller.profil(mock(Model.class), oauth2User);

        // Assert
        assertEquals("profil-page", result);
    }

    @Test
    public void testProfilWithUnauthenticatedUser() {
        // Act
        String result = mainContoller.profil(mock(Model.class), null);

        // Assert
        assertEquals("redirect:/login", result);
    }

    

	@Test
    @WithMockUser // Simulate an authenticated user
    void testAjouterRelationPage() throws Exception {
        // Perform GET request to /ajouter-relation
        mockMvc.perform(get("/ajouter-relation"))
            // Expect HTTP status 200 OK
            .andExpect(status().isOk())
            // Expect view name "ajouter-relation"
            .andExpect(view().name("ajouter-relation"));
    }

    @Test
    @WithMockUser // Simulate an authenticated user
    void testSoldePage() throws Exception {
        // Perform GET request to /sold
        mockMvc.perform(get("/sold"))
            // Expect HTTP status 200 OK
            .andExpect(status().isOk())
            // Expect view name "solde-page"
            .andExpect(view().name("solde-page"));
    }

}
