package com.projet6.PayMyBuddy.Service;

import org.springframework.stereotype.Service;

import com.projet6.PayMyBuddy.dto.SocialNetworkUserDto;


@Service
public interface SocialNetworkUserService {
	
	void save(SocialNetworkUserDto socialNetworkuserDto);

}
