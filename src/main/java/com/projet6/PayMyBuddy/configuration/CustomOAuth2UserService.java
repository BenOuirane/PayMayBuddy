package com.projet6.PayMyBuddy.configuration;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.projet6.PayMyBuddy.ServiceImpl.SocialNetworkUserServiceImpl;
import com.projet6.PayMyBuddy.dto.SocialNetworkUserDto;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService{
	/*

	@Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SocialNetworkUserServiceImpl socialNetworkUserServiceImpl;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        System.out.println("Email from OAuth2 provider: " + email); // Log for debugging

        User user = userRepository.findByEmail(email);
        if (user == null) {
            SocialNetworkUserDto newUser = new SocialNetworkUserDto();
            newUser.setEmail_id(email != null ? email : "usernam" + "@github.com");
            newUser.setName("usernam");
            newUser.setPassword(passwordEncoder.encode("test"));
            newUser.setRole("ROLE_USER");
            socialNetworkUserServiceImpl.save(newUser);
            System.out.println("New user created: " + email); // Log for debugging
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "email");
    }
    */
}
