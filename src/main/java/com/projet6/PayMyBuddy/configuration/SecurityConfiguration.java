package com.projet6.PayMyBuddy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.projet6.PayMyBuddy.Service.UserService;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	 @Autowired
	 private UserService userService;
	 private final PasswordEncoder passwordEncoder;
 
	 @Autowired
	    public SecurityConfiguration(UserService userService, PasswordEncoder passwordEncoder) {
	        this.userService = userService;
	        this.passwordEncoder = passwordEncoder;
	    }
	 
	    @Bean
	    public DaoAuthenticationProvider authenticationProvider() {
	    	DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
	    	auth.setUserDetailsService(userService);
	    	auth.setPasswordEncoder(passwordEncoder);
	    	return auth;
	    }
	    
	    public void configure(AuthenticationManagerBuilder auth) throws Exception{
	    	auth.authenticationProvider(authenticationProvider());
	    }
	
	 @Bean
	 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .authorizeHttpRequests(authorize -> authorize
	                .requestMatchers("/registration**", "/js/**", "/css/**", "/img/**", "/webjars/**").permitAll()
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                .loginPage("/login")
	                .defaultSuccessUrl("/profil", true) // Specify the default page after successful login
	                .permitAll()
	            )
	            .logout(logout -> logout
	                 .invalidateHttpSession(true)
	                 .clearAuthentication(true)
	                 .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	                 .logoutSuccessUrl("/login?logout")
	            	 .permitAll()
	            );

	        return http.build();
	    }

	    
}
