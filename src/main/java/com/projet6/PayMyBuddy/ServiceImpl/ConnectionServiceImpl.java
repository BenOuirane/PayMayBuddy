package com.projet6.PayMyBuddy.ServiceImpl;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
	public String getConnectionsToTransferAmounOfMoney(@RequestParam(defaultValue = "0") int page, Model model, HttpServletRequest request) {
		 HttpSession session = request.getSession();
		    String userEmail = (String) session.getAttribute("userEmail");

		    if (userEmail == null) {
		        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		        if (authentication != null && authentication.isAuthenticated()) {
		            Object principal = authentication.getPrincipal();
		            if (principal instanceof UserDetails) {
		                userEmail = ((UserDetails) principal).getUsername();
		            } else if (principal instanceof OAuth2User) {
		                userEmail = (String) ((OAuth2User) principal).getAttributes().get("email");
		                if (userEmail == null) {
		                    userEmail = (String) ((OAuth2User) principal).getAttributes().get("login") + "@github.com";
		                }
		            }
		        }
		    }

		    if (userEmail == null) {
		        model.addAttribute("errorMessage", "Utilisateur non trouvé.");
		        return "transfer-page";
		    }

		    User currentUser = userRepository.findByEmail(userEmail);
		    if (currentUser != null) {
		        Set<User> connections = currentUser.getConnections();
		        model.addAttribute("connections", connections);

		        // Retrieve transactions of the current user with pagination
		        int size = 5; // Number of transactions per page
		        Page<Transaction> transactionPage = transactionService.getTransactionsForUser(userEmail, page, size);
		        model.addAttribute("transactionPage", transactionPage);

		    } else {
		        model.addAttribute("errorMessage", "Utilisateur non trouvé.");
		    }

		    return "transfer-page"; // Return the name of the Thymeleaf template for the ajouter-relation page
		}
	
}
