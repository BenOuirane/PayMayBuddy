package com.projet6.PayMyBuddy.seed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;

@Component
public class UserDataLoader implements CommandLineRunner {

	 @Autowired
	 private UserRepository userRepository;
	 @Autowired
	 private BCryptPasswordEncoder passwordEncoder; // Inject Password Encode	 

	@Override
	public void run(String... args) throws Exception {
		loadUserData();		
	}
	
	private void loadUserData() {
        String encodedPassword = passwordEncoder.encode("123");
		if (userRepository.count() == 0) {
			User user1 = new User(1L, 1000.0, "john_doe", "123@gmail.com", encodedPassword, "ROLE_USER");
			User system = new User(2L, 0.0, "system", "system@domain.com", encodedPassword, "ROLE_SYSTEM");
			userRepository.save(user1);
			userRepository.save(system);
		}
		System.out.println(userRepository.count());
	}


}