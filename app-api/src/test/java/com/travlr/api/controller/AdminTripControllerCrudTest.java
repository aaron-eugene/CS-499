package com.travlr.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.travlr.api.repository.TripRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Verifies administrative trip create, update, and delete behavior.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
		"spring.security.user.name=admin",
		"spring.security.user.password=changeme",
		"spring.security.user.roles=ADMIN"
})
@SuppressWarnings("null")
class AdminTripControllerCrudTest {
	private static final String TEST_CODE = "TSTNEW20990101";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TripRepository tripRepository;

	@BeforeEach
	void setUp() {
		deleteTestTripIfPresent();
	}

	@AfterEach
	void tearDown() {
		deleteTestTripIfPresent();
	}

	@Test
	void createTripCreatesNewTripWhenAuthenticated() throws Exception {
		mockMvc.perform(post("/api/admin/trips")
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCreateJson()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.code").value(TEST_CODE))
				.andExpect(jsonPath("$.name").value("Test Reef Escape"))
				.andExpect(jsonPath("$.durationDays").value(5));
	}

	@Test
	void createTripReturnsConflictForDuplicateCode() throws Exception {
		mockMvc.perform(post("/api/admin/trips")
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCreateJson()))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/admin/trips")
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCreateJson()))
				.andExpect(status().isConflict());
	}

	@Test
	void createTripReturnsBadRequestForInvalidRequest() throws Exception {
		mockMvc.perform(post("/api/admin/trips")
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidCreateJson()))
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateTripUpdatesExistingTripWhenAuthenticated() throws Exception {
		mockMvc.perform(post("/api/admin/trips")
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCreateJson()))
				.andExpect(status().isCreated());

		mockMvc.perform(put("/api/admin/trips/" + TEST_CODE)
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validUpdateJson()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(TEST_CODE))
				.andExpect(jsonPath("$.name").value("Updated Reef Escape"))
				.andExpect(jsonPath("$.durationDays").value(7))
				.andExpect(jsonPath("$.resort").value("Updated Test Resort"));
	}

	@Test
	void updateTripReturnsBadRequestForInvalidRequest() throws Exception {
		mockMvc.perform(put("/api/admin/trips/" + TEST_CODE)
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidUpdateJson()))
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateTripReturnsNotFoundForUnknownCode() throws Exception {
		mockMvc.perform(put("/api/admin/trips/NOEXST20990101")
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validUpdateJson()))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteTripDeletesExistingTripWhenAuthenticated() throws Exception {
		mockMvc.perform(post("/api/admin/trips")
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCreateJson()))
				.andExpect(status().isCreated());

		mockMvc.perform(delete("/api/admin/trips/" + TEST_CODE)
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader()))
				.andExpect(status().isNoContent());

		mockMvc.perform(delete("/api/admin/trips/" + TEST_CODE)
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader()))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteTripReturnsNotFoundForUnknownCode() throws Exception {
		mockMvc.perform(delete("/api/admin/trips/NOEXST20990101")
				.header(HttpHeaders.AUTHORIZATION, adminAuthHeader()))
				.andExpect(status().isNotFound());
	}

	private void deleteTestTripIfPresent() {
		tripRepository.findByCode(TEST_CODE)
				.ifPresent(tripRepository::delete);
	}

	private String validCreateJson() {
		return """
				{
					"code": "TSTNEW20990101",
					"name": "Test Reef Escape",
					"durationDays": 5,
					"startDate": "2099-01-01",
					"resort": "Test Resort",
					"pricePerPerson": 1299.99,
					"imageName": "test_reef_escape.jpg",
					"description": "A test trip used to verify administrative create behavior."
				}
				""";
	}

	private String validUpdateJson() {
		return """
				{
					"name": "Updated Reef Escape",
					"durationDays": 7,
					"startDate": "2099-02-01",
					"resort": "Updated Test Resort",
					"pricePerPerson": 1499.99,
					"imageName": "updated_reef_escape.jpg",
					"description": "A test trip used to verify administrative update behavior."
				}
				""";
	}

	private String invalidCreateJson() {
		return """
				{
					"code": "bad-code",
					"name": "",
					"durationDays": 0,
					"startDate": "2000-01-01",
					"resort": "",
					"pricePerPerson": 0,
					"imageName": "../bad.jpg",
					"description": ""
				}
				""";
	}

	private String invalidUpdateJson() {
		return """
				{
					"name": "",
					"durationDays": 0,
					"startDate": "2000-01-01",
					"resort": "",
					"pricePerPerson": 0,
					"imageName": "../bad.jpg",
					"description": ""
				}
				""";
	}

	private String adminAuthHeader() {
		return basicAuthHeader("admin", "changeme");
	}

	private String basicAuthHeader(String username, String password) {
		String credentials = username + ":" + password;
		String encodedCredentials = Base64.getEncoder()
				.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

		return "Basic " + encodedCredentials;
	}
}
