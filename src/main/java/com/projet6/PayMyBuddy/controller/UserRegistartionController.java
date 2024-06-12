package com.projet6.PayMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projet6.PayMyBuddy.Service.UserService;
import com.projet6.PayMyBuddy.dto.UserRegistrationDto;


@Controller
@RequestMapping("/registration")
public class UserRegistartionController {
	
	@Autowired
	UserService  userService;
	
	@ModelAttribute("user")
	public UserRegistrationDto  userRegistrationDto() {
		return new UserRegistrationDto();	}
	
	@GetMapping
	public String showRegistrationForm() {
		return "registration";	}
	
	@PostMapping
	public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
		userService.saveUser(registrationDto);
		return "redirect:/registration?success";	
		}

	 @PostMapping("/updateProfile")
	    public String updateProfile(@RequestParam("username") String username,
	                                @RequestParam("email") String email,
	                                @RequestParam("password") String password,
	                                Model model,RedirectAttributes redirectAttributes) {
		 boolean isUpdated = userService.updateProfile(username, email, password, model);
		    
		    if (isUpdated) {
		        redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès!");

		  //      model.addAttribute("successMessage", "Profil mis à jour avec succès!");
		        // Redirect to the profil endpoint to refresh the model attributes with the updated user data
		 //       return "redirect:/profil";
		    } else {
		        redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du profil.");

		        model.addAttribute("errorMessage", "Erreur lors de la mise à jour du profil.");
		        // Handle error case
		   //     return "error-page"; // You can define an error page in your application
		    }
    		return "redirect:/profil";

	 }
	
}
