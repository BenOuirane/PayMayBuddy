package com.projet6.PayMyBuddy.serviceImplTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projet6.PayMyBuddy.ServiceImpl.SocialNetworkUserServiceImpl;
import com.projet6.PayMyBuddy.dto.SocialNetworkUserDto;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class SocialNetworkUserServiceImplTest {
	
	    @Mock
	    private UserRepository userRepo;

	    @InjectMocks
	    private SocialNetworkUserServiceImpl socialNetworkUserService;

	    @Test
	    public void testSave() {
	        // Arrange
	        SocialNetworkUserDto socialNetworkuserDto = new SocialNetworkUserDto();
	        socialNetworkuserDto.setEmail_id("testuser@example.com");
	        socialNetworkuserDto.setName("testuser");
	        socialNetworkuserDto.setPassword("password");

	        // Act
	        socialNetworkUserService.save(socialNetworkuserDto);

	        // Assert
	        verify(userRepo, times(1)).save(any(User.class));
	    }

}
