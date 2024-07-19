package aisaac.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		 http.cors().and().csrf().disable() // Enable CORS and disable CSRF
		    .sessionManagement()
		    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set session management to stateless
		    .and()
	        .authorizeHttpRequests()
	    	.requestMatchers("/v1/**").permitAll().requestMatchers("/actuator/**").permitAll()// Set permissions on endpoints
	    	.anyRequest().authenticated();


	    return http.build();
		
	
	}
}
