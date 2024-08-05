package com.projet6.PayMyBuddy.controllerTest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.projet6.PayMyBuddy.Service.ConnectionService;
import com.projet6.PayMyBuddy.Service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserConnectionsControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void setupAuthenticatedUser(String username) {
        UserDetails userDetails = User.withUsername(username)
                                      .password("password")
                                      .roles("USER") // Add roles if needed
                                      .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void setupOAuth2AuthenticatedUser(String email, String login) {
        Map<String, Object> attributes = Map.of(
            "email", email,
            "login", login
        );
        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "login");
        Authentication authentication = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testAddRelation_Success() throws Exception {
        setupAuthenticatedUser("test@example.com");

        // Mock service response for adding a connection
        doNothing().when(connectionService).addUserConnection(anyString(), anyString());

        // Perform the POST request
        mockMvc.perform(post("/addRelation")
                .param("relationSearch", "relation@example.com")
                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(view().name("ajouter-relation"))
                .andExpect(model().attribute("successMessage", "Relation ajoutée avec succès."));
    }

    @Test
    public void testAddRelation_OAuth2_Success() throws Exception {
        setupOAuth2AuthenticatedUser("oauth2user@example.com", "oauth2user");

        // Mock service response for adding a connection
        doNothing().when(connectionService).addUserConnection(anyString(), anyString());

        // Perform the POST request
        mockMvc.perform(post("/addRelation")
                .param("relationSearch", "relation@example.com")
                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(view().name("ajouter-relation"))
                .andExpect(model().attribute("successMessage", "Relation ajoutée avec succès."));
    }

    @Test
    public void testAddRelation_UserNotFound() throws Exception {
        setupAuthenticatedUser("test@example.com");

        // Mock behavior for exception
        doThrow(new IllegalStateException("User not found")).when(connectionService).addUserConnection(anyString(), anyString());

        // Perform the POST request
        mockMvc.perform(post("/addRelation")
                .param("relationSearch", "relation@example.com")
                .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(view().name("ajouter-relation"))
                .andExpect(model().attribute("errorMessage", "User not found"));
    }

   

}
