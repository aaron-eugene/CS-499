package com.travlr.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the Travlr Spring Boot API application.
 */
@SpringBootApplication
public class AppApiApplication {
	/**
	 * Application entry point.
	 *
	 * @param args command-line arguments passed to Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(AppApiApplication.class, args);
	}
}
