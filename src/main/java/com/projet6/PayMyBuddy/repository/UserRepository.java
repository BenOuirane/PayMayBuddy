package com.projet6.PayMyBuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet6.PayMyBuddy.model.User;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
	User findByEmail(String email);

	User findByUsername(String username);

}
