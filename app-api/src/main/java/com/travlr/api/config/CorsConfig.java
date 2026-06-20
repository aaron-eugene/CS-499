package com.travlr.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures cross-origin access for Travlr API requests.
 *
 * The allowed origin is read from application configuration so local and
 * deployed environments can use different frontend URLs without changing
 * controller source code. CORS controls which browser origins may call the API;
 * authentication and authorization are enforced separately by Spring Security.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
	private final String allowedOrigin;

	/**
	 * Creates a CORS configuration using the configured frontend origin.
	 *
	 * @param allowedOrigin allowed frontend origin for browser API requests
	 */
	public CorsConfig(@Value("${app.cors.allowed-origin}") String allowedOrigin) {
		this.allowedOrigin = allowedOrigin;
	}

	/**
	 * Applies CORS rules to API endpoints.
	 *
	 * Write methods are allowed for the configured frontend origin so the admin UI
	 * can call protected create, update, and delete endpoints. Spring Security
	 * still enforces authentication and authorization for those requests.
	 *
	 * @param registry CORS registry used by Spring MVC
	 */
	@Override
	public void addCorsMappings(@NonNull CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(allowedOrigin)
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*");
	}
}
