package com.projet6.PayMyBuddy.controllerTest;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.ServiceImpl.SoldeServiceImpl;
import com.projet6.PayMyBuddy.ServiceImpl.TransactionServiceImpl;
import com.projet6.PayMyBuddy.model.Transaction;
import com.projet6.PayMyBuddy.model.User;


@SpringBootTest
@AutoConfigureMockMvc
public class SoldeControllerTest {
	
	    @Autowired
	    private MockMvc mockMvc;

	    @MockBean
	    private SoldeServiceImpl soldeService;

	    @MockBean
	    private TransactionServiceImpl transactionServiceImpl;

	    @MockBean
	    private UserService userService;
	    
	    
	    @Test
	    public void testGetSoldePage_UserFound() throws Exception {
	        // Setup mock user and transactions
	        User mockUser = new User();
	        mockUser.setEmail("test@example.com");
	        mockUser.setWealth(1000.0);

	        Page<Transaction> transactionPage = new PageImpl<>(Collections.emptyList());

	        when(soldeService.getCurrentUser()).thenReturn(mockUser);
	        when(soldeService.getSelfTransactions(eq("test@example.com"), anyInt(), anyInt())).thenReturn(transactionPage);

	        // Perform the GET request
	        mockMvc.perform(get("/solde")
	                .param("page", "0")
	                .param("size", "5"))
	                .andExpect(status().isOk())
	                .andExpect(view().name("solde-page"))
	                .andExpect(model().attribute("solde", 1000.0))
	                .andExpect(model().attribute("transactionPage", transactionPage))
	                .andExpect(model().attribute("transactions", transactionPage.getContent()));

	        // Verify interactions
	        verify(soldeService).getCurrentUser();
	        verify(soldeService).getSelfTransactions(eq("test@example.com"), anyInt(), anyInt());
	    }

	    @Test
	    public void testGetSoldePage_UserNotFound() throws Exception {
	        // Setup mock to return null for current user
	        when(soldeService.getCurrentUser()).thenReturn(null);

	        // Perform the GET request
	        mockMvc.perform(get("/solde")
	                .param("page", "0")
	                .param("size", "5"))
	                .andExpect(status().isOk())
	                .andExpect(view().name("solde-page"))
	                .andExpect(model().attribute("solde", 0))
	                .andExpect(model().attribute("transactionPage", Page.empty()))
	                .andExpect(model().attribute("transactions", Collections.emptyList()));

	        // Verify interactions
	        verify(soldeService).getCurrentUser();
	        verify(soldeService, never()).getSelfTransactions(anyString(), anyInt(), anyInt());
	    }
	    
	
}
