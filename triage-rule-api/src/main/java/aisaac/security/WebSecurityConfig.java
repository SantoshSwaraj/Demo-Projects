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
	
	
	@SuppressWarnings("deprecation")
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		
		// Enable CORS and disable CSRF
	    http = http.cors().and().csrf().disable();

	    // Set session management to stateless
	    http = http
	        .sessionManagement()
	        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        .and();

	    // Set permissions on endpoints
	    http.authorizeRequests()
	    	.requestMatchers("/v1/**").permitAll()
	    	.anyRequest().authenticated();



	    return http.build();
		
	
	}
}
