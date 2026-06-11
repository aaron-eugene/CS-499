package com.travlr.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Verifies that public trip endpoints remain open while administrative trip
 * management endpoints require authentication.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
		"spring.security.user.name=admin",
		"spring.security.user.password=changeme",
		"spring.security.user.roles=ADMIN"
})
class AdminTripControllerSecurityTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void publicTripSummaryIsAvailableWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/trips/summary"))
				.andExpect(status().isOk());
	}

	@Test
	void adminDeleteIsRejectedWithoutAuthentication() throws Exception {
		mockMvc.perform(delete("/api/admin/trips/GALREE20270214"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void adminDeleteReachesControllerWithAuthentication() throws Exception {
		mockMvc.perform(delete("/api/admin/trips/NOEXST20990101")
				.header(HttpHeaders.AUTHORIZATION, basicAuthHeader("admin", "changeme")))
				.andExpect(status().isNotFound());
	}

	private String basicAuthHeader(String username, String password) {
		String credentials = username + ":" + password;
		String encodedCredentials = Base64.getEncoder()
				.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

		return "Basic " + encodedCredentials;
	}
}
