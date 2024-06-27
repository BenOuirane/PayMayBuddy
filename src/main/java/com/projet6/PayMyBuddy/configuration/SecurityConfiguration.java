package com.projet6.PayMyBuddy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
	 private CustomSuccessHandler customAuthenticationSuccessHandler;
	 
	 @Autowired
	    private CustomOAuth2UserService customOAuth2UserService;
 
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
	                .requestMatchers("/registration**",
	                		         "/transfer/**",
	                		         "/solde/**", 
	                		         "/addRelation/**",  
	                		         "/oauth2/authorization/**",
	                		         "/js/**", 
	                		         "/css/**", 
	                		         "/img/**", 
	                		         "/webjars/**")
	                .permitAll()
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
	            )
	            .oauth2Login(oauth2 -> oauth2
	                    .loginPage("/login")
	              //      .defaultSuccessUrl("/profil", true)
	                    .userInfoEndpoint(userInfo -> userInfo
	                        .userService(oAuth2UserService())
	                    )
	                    .successHandler(customAuthenticationSuccessHandler)
	                );
	        return http.build();
	    }
	 
	   

	 @Bean
	 public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
	        return new DefaultOAuth2UserService();
	    }
	 
	    
}
