package com.projet6.PayMyBuddy.Service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.projet6.PayMyBuddy.exception.ConnectionAlreadyExistsException;
import com.projet6.PayMyBuddy.exception.SelfConnectionException;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;


@Service
public interface ConnectionService {
	
	void addUserConnection(String userEmail, String connectionEmail) 
	        throws UserNotFoundException, SelfConnectionException, ConnectionAlreadyExistsException;
	
	public String getConnectionsToTransferAmounOfMoney(@RequestParam(defaultValue = "0") int page, Model model, HttpServletRequest request);


}
