package com.projet6.PayMyBuddy.configuration;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.projet6.PayMyBuddy.Service.SocialNetworkUserService;
import com.projet6.PayMyBuddy.ServiceImpl.SocialNetworkUserServiceImpl;
import com.projet6.PayMyBuddy.dto.SocialNetworkUserDto;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler  {
	
	@Autowired
	UserRepository userRepo;
	DefaultConversionService userService;
	@Autowired
	SocialNetworkUserServiceImpl  socialNetworkUserServiceImpl;	
    @Autowired
    PasswordEncoder passwordEncoder;
	
	@SuppressWarnings("unused")
	private final UserRepository userRepository;
    @SuppressWarnings("unused")
	private final SocialNetworkUserService socialNetworkUserService;

    @Autowired
    public CustomSuccessHandler(UserRepository userrepository, SocialNetworkUserService socialnetworkUserService) {
        this.userRepository = userrepository;
        this.socialNetworkUserService = socialnetworkUserService;
    }	

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			                            HttpServletResponse response, 
			                            Authentication authentication)
		    throws IOException, ServletException {
		
		String redirectUrl = "/transfer/page";

        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();

            // Log all user attributes
            System.out.println("OAuth2 User Attributes: " + userDetails.getAttributes());

            String email = userDetails.getAttribute("email");
            String username = userDetails.getAttribute("login");

            System.out.println("OAuth2 User Email: " + email);
            System.out.println("OAuth2 User Login: " + username);

            // If the email is null, construct a GitHub based email
            if (email == null && username != null) {
                email = username + "@github.com";
            }

            if (email == null) {
                throw new ServletException("Email not found from OAuth2 provider");
            }

            User user = userRepo.findByEmail(email);
            if (user == null) {
                System.out.println("Creating new user with email: " + email);
                SocialNetworkUserDto newUser = new SocialNetworkUserDto();
                newUser.setEmail_id(email);
                newUser.setName(username != null ? username : "DefaultName");
                newUser.setPassword(passwordEncoder.encode("test"));
                newUser.setRole("ROLE_USER");
                socialNetworkUserServiceImpl.save(newUser);
            } else {
                System.out.println("User already exists with email: " + email);
            }

            // Save the email in the session
            HttpSession session = request.getSession();
            session.setAttribute("userEmail", email);
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);     
    }
}
