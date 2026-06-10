package com.travlr.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures cross-origin access for Travlr API requests.
 *
 * The allowed origin is read from application configuration so local and
 * deployed environments can use different frontend URLs without changing
 * controller source code. Public browsing routes use GET requests, while
 * protected administrative routes use write methods under /api/admin.
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
	 * Authorization is still enforced by Spring Security. Allowing write methods
	 * here only permits browser preflight checks and cross-origin requests from
	 * the configured frontend origin.
	 *
	 * @param registry CORS registry used by Spring MVC
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(allowedOrigin)
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*");
	}
}
