package com.projet6.PayMyBuddy.ServiceImpl;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projet6.PayMyBuddy.Service.SocialNetworkUserService;
import com.projet6.PayMyBuddy.dto.SocialNetworkUserDto;
import com.projet6.PayMyBuddy.model.Role;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;


@Service
public class SocialNetworkUserServiceImpl implements SocialNetworkUserService {

    @Autowired
    private UserRepository userRepo;
    /*
     *  @Autowired
    private PasswordEncoder passwordEncoder;
     */
	@Override
	public void save(SocialNetworkUserDto socialNetworkuserDto) {
		User user = new User();
        user.setEmail(socialNetworkuserDto.getEmail_id());
        user.setUsername(socialNetworkuserDto.getName());
        user.setPassword(socialNetworkuserDto.getPassword());
        user.setPassword(socialNetworkuserDto.getPassword());
        user.setRoles(Arrays.asList(new Role("ROLE-USER")));
        userRepo.save(user);		
	}

}
