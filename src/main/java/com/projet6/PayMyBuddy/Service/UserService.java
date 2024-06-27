package com.projet6.PayMyBuddy.Service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import com.projet6.PayMyBuddy.dto.UserRegistrationDto;
import com.projet6.PayMyBuddy.model.User;

@Service
public interface UserService extends UserDetailsService{
	
	User saveUser(UserRegistrationDto userRegistrationDto);
	User findByEmail(String email);
	boolean updateProfile(String username, String email, String password, Model model);
    User findSystemAccount();
    User save(User user);

}
