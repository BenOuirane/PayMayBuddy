package com.projet6.PayMyBuddy.Service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.projet6.PayMyBuddy.exception.ConnectionAlreadyExistsException;
import com.projet6.PayMyBuddy.exception.SelfConnectionException;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;


@Service
public interface ConnectionService {
	
	void addUserConnection(String userEmail, String connectionEmail) 
	        throws UserNotFoundException, SelfConnectionException, ConnectionAlreadyExistsException;
	
    public String getConnectionsToTransferAmounOfMoney(Model model) ;


}
