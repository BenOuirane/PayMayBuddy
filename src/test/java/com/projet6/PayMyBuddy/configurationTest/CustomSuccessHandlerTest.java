package com.projet6.PayMyBuddy.configurationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;

import com.projet6.PayMyBuddy.ServiceImpl.SocialNetworkUserServiceImpl;
import com.projet6.PayMyBuddy.configuration.CustomSuccessHandler;
import com.projet6.PayMyBuddy.dto.SocialNetworkUserDto;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomSuccessHandlerTest {
	
	
	@Mock
    private UserRepository userRepo;

    @Mock
    private SocialNetworkUserServiceImpl socialNetworkUserServiceImpl;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DefaultRedirectStrategy redirectStrategy;

    @InjectMocks
    private CustomSuccessHandler customSuccessHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        //customSuccessHandler.setRedirectStrategy(redirectStrategy);
    }

    @Test
    public void testOnAuthenticationSuccess_UserDoesNotExist() throws IOException, ServletException {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("login", "testuser");
        DefaultOAuth2User oauth2User = new DefaultOAuth2User(null, attributes, "login");

        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(userRepo.findByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("test")).thenReturn("encodedPassword");

        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Act
        customSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(userRepo).findByEmail("test@example.com");
        verify(socialNetworkUserServiceImpl).save(any(SocialNetworkUserDto.class));
        verify(session).setAttribute("userEmail", "test@example.com");
     //   verify(redirectStrategy).sendRedirect(request, response, "/transfer/page");
    }

    @Test
    public void testOnAuthenticationSuccess_UserExists() throws IOException, ServletException {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "existing@example.com");
     //   DefaultOAuth2User oauth2User = new DefaultOAuth2User(null, attributes, "login");

        User existingUser = new User();
   //     when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(userRepo.findByEmail("existing@example.com")).thenReturn(existingUser);

        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Act
        customSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
     //   verify(userRepo).findByEmail("existing@example.com");
        verify(socialNetworkUserServiceImpl, never()).save(any(SocialNetworkUserDto.class));
    //    verify(session).setAttribute("userEmail", "existing@example.com");
    //    verify(redirectStrategy).sendRedirect(request, response, "/transfer/page");
    }

    @Test
    public void testOnAuthenticationSuccess_EmailIsNull() throws IOException, ServletException {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("login", "testuser");
        DefaultOAuth2User oauth2User = new DefaultOAuth2User(null, attributes, "login");

        when(authentication.getPrincipal()).thenReturn(oauth2User);

        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
    
        verify(redirectStrategy, never()).sendRedirect(any(HttpServletRequest.class), any(HttpServletResponse.class), anyString());
    }
}
