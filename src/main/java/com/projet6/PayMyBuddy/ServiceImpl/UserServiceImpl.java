package com.projet6.PayMyBuddy.ServiceImpl;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.dto.UserRegistrationDto;
import com.projet6.PayMyBuddy.model.Role;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	UserRepository  userRepository;
	@Autowired
    private PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public User saveUser(UserRegistrationDto userRegistrationDto) {
        User user = new User(userRegistrationDto.getUsername(),
        		             userRegistrationDto.getEmail(),
        		             passwordEncoder.encode(userRegistrationDto.getPassword()),
        		             Arrays.asList(new Role("ROLE-USER")));
		return userRepository.save(user);
	}
	
	
	@Override   
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);

		if(user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
	    return new CustomUserDetails(user);
	   //return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));		
	}
	
	@SuppressWarnings("serial")
	public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
	    private User user;

	    public CustomUserDetails(User user) {
	        super(user.getEmail(), user.getPassword(), user.getRoles().stream()
	                .map(role -> new SimpleGrantedAuthority(role.getName()))
	                .collect(Collectors.toList()));
	        this.user = user;
	    }

	    public User getUser() {
	        return user;
	    }
   
	/*
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
		return roles.stream()
				    .map(role -> new SimpleGrantedAuthority(role.getName()))
				    .collect(Collectors.toList());
	}
	*/

}


}
