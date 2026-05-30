package com.travlr.api.repository;

import com.travlr.api.model.Trip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the current in-memory trip repository used by the milestone
 * implementation.
 *
 * These tests verify that the repository exposes application trip data and that
 * trip-code lookup works without requiring service-layer filtering or sorting
 * logic.
 */
class InMemoryTripRepositoryTest {
	private InMemoryTripRepository tripRepository;

	/**
	 * Creates a fresh repository before each test.
	 */
	@BeforeEach
	void setUp() {
		tripRepository = new InMemoryTripRepository();
	}

	/**
	 * Verifies that the repository provides the current application trip data.
	 */
	@Test
	void findAllReturnsApplicationTrips() {
		List<Trip> trips = tripRepository.findAll();

		assertFalse(trips.isEmpty());
	}

	/**
	 * Verifies that the repository can find a trip by code without requiring an
	 * exact case match.
	 */
	@Test
	void findByCodeFindsTripCaseInsensitively() {
		List<Trip> trips = tripRepository.findAll();
		assertFalse(trips.isEmpty());

		Trip expectedTrip = trips.get(0);
		Optional<Trip> result = tripRepository.findByCode(
				expectedTrip.getCode().toLowerCase(Locale.ROOT));

		assertTrue(result.isPresent());
		assertEquals(expectedTrip.getCode(), result.get().getCode());
	}

	/**
	 * Verifies that an unknown trip code does not return a trip.
	 */
	@Test
	void findByCodeReturnsEmptyForUnknownCode() {
		Optional<Trip> result = tripRepository.findByCode("UNKNOWN999");

		assertTrue(result.isEmpty());
	}

	/**
	 * Verifies that blank trip codes do not return a trip.
	 */
	@Test
	void findByCodeReturnsEmptyForBlankCode() {
		Optional<Trip> result = tripRepository.findByCode("   ");

		assertTrue(result.isEmpty());
	}
}
