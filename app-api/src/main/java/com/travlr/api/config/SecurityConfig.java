package com.travlr.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures authentication and authorization rules for the Travlr API.
 *
 * Public trip browsing endpoints remain available without authentication, while
 * administrative create, update, and delete endpoints require an authenticated
 * user with the ADMIN role.
 */
@Configuration
public class SecurityConfig {
	/**
	 * Builds the API security filter chain.
	 *
	 * @param http HTTP security configuration
	 * @return configured security filter chain
	 * @throws Exception if the security configuration cannot be built
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/trips/**").permitAll()
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults());

		return http.build();
	}
}
