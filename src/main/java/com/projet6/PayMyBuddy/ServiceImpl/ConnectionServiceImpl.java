package com.projet6.PayMyBuddy.ServiceImpl;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import com.projet6.PayMyBuddy.Service.ConnectionService;
import com.projet6.PayMyBuddy.Service.TransactionService;
import com.projet6.PayMyBuddy.exception.ConnectionAlreadyExistsException;
import com.projet6.PayMyBuddy.exception.SelfConnectionException;
import com.projet6.PayMyBuddy.exception.UserNotFoundException;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;
import com.projet6.PayMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;


@Transactional
@Service
public class ConnectionServiceImpl implements ConnectionService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
	private TransactionService transactionService;

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
	

	@Override
	public String getConnectionsToTransferAmounOfMoney(@RequestParam(defaultValue = "0") int page, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String userEmail = authentication.getName();
	    User currentUser = userRepository.findByEmail(userEmail);
	    if (currentUser != null) {
	        Set<User> connections = currentUser.getConnections();
	        model.addAttribute("connections", connections);
	        // Afficher les connexions dans la console
	        /*
	        System.out.println("Current User: " + currentUser.getUsername());
	        System.out.println("Connections: ");
	        for (User connection : connections) {
	            System.out.println(" - " + connection.getUsername() + " (" + connection.getEmail() + ")");
	        }
	        */
	        
	     // Récupérer les transactions de l'utilisateur actuel avec pagination
            int size = 5; // Nombre de transactions par page
            Page<Transaction> transactionPage = transactionService.getTransactionsForUser(userEmail, page, size);
            model.addAttribute("transactionPage", transactionPage);
            
	    } else {
	       // System.out.println("Utilisateur non trouvé pour l'email : " + userEmail);
	        model.addAttribute("errorMessage", "Utilisateur non trouvé.");
	    }
        return "transfer-page"; // Return the name of the Thymeleaf template for the ajouter-relation page
	}

   
}
