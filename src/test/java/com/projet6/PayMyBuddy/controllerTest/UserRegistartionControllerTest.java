package com.projet6.PayMyBuddy.controllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.controller.UserRegistartionController;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRegistartionControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRegistartionController userRegistartionController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userRegistartionController)
                .build();
    }

    @Test
    public void testUpdateProfile_Success() throws Exception {
        String username = "testuser";
        String email = "testuser@example.com";
        String password = "newpassword";

        // Mock the UserService behavior
        when(userService.updateProfile(anyString(), anyString(), anyString(), any()))
                .thenReturn(true);

        mockMvc.perform(post("/registration/updateProfile")
                .param("username", username)
                .param("email", email)
                .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"))
                .andExpect(flash().attribute("successMessage", "Profil mis à jour avec succès!"));
    }

    @Test
    public void testUpdateProfile_Failure() throws Exception {
        String username = "testuser";
        String email = "testuser@example.com";
        String password = "newpassword";

        // Mock the UserService behavior
        when(userService.updateProfile(anyString(), anyString(), anyString(), any()))
                .thenReturn(false);

        mockMvc.perform(post("/registration/updateProfile")
                .param("username", username)
                .param("email", email)
                .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"))
                .andExpect(flash().attribute("errorMessage", "Erreur lors de la mise à jour du profil."));
    }

}
