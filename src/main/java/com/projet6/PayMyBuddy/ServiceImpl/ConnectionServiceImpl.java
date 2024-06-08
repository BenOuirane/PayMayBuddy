package com.projet6.PayMyBuddy.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projet6.PayMyBuddy.Service.ConnectionService;
import com.projet6.PayMyBuddy.exception.ConnectionAlreadyExistsException;
import com.projet6.PayMyBuddy.exception.SelfConnectionException;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class ConnectionServiceImpl implements ConnectionService {

	@Autowired
    private UserRepository userRepository;

	public void addUserConnection(String currentUserEmail, String relationEmail) throws UserNotFoundException, SelfConnectionException, ConnectionAlreadyExistsException {
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new UserNotFoundException("Current user not found");
        }

        if (currentUserEmail.equals(relationEmail)) {
            throw new SelfConnectionException("You cannot add yourself as a connection");
        }

        User relationUser = userRepository.findByEmail(relationEmail);
        if (relationUser == null) {
            throw new UserNotFoundException("User to add not found");
        }

        if (currentUser.getConnections().contains(relationUser)) {
            throw new ConnectionAlreadyExistsException("This connection already exists");
        }

        currentUser.getConnections().add(relationUser);
        userRepository.save(currentUser);
    }

   
}
