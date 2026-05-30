package com.travlr.api.service;

import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.model.Trip;
import com.travlr.api.repository.TripRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests defensive handling of user-controlled trip query input.
 *
 * These tests support the security mindset for the algorithms and data
 * structures enhancement by verifying that unexpected query values do not cause
 * service failures, unbounded result behavior, or unsafe lookup behavior.
 */
class TripServiceDefensiveInputTest {
	private TripService tripService;
	private List<Trip> testTrips;

	/**
	 * Creates a fresh service with controlled test data before each test.
	 */
	@BeforeEach
	void setUp() {
		testTrips = List.of(
				new Trip(
						"ALPHA001",
						"Alpha Reef",
						3,
						LocalDate.of(2027, 1, 10),
						"Coral Bay, 3 stars",
						new BigDecimal("500.00"),
						"reef1.jpg",
						"Short reef trip."),
				new Trip(
						"BRAVO002",
						"Bravo Island",
						7,
						LocalDate.of(2027, 5, 15),
						"Blue Harbor, 4 stars",
						new BigDecimal("1500.00"),
						"kayak.jpg",
						"Island kayaking and snorkeling."),
				new Trip(
						"CHARLIE003",
						"Charlie Lagoon",
						10,
						LocalDate.of(2027, 9, 20),
						"Palm Vista, 5 stars",
						new BigDecimal("2500.00"),
						"deluxe.jpg",
						"Luxury lagoon retreat."));

		tripService = new TripService(new TestTripRepository(testTrips));
	}

	/**
	 * Verifies that an unsupported sort field does not cause query processing to
	 * fail.
	 */
	@Test
	void getTripsHandlesUnsupportedSortField() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setSort("notARealField");

		List<Trip> trips = tripService.getTrips(criteria);

		assertEquals(testTrips.size(), trips.size());
	}

	/**
	 * Verifies that unexpected sort direction values do not reverse the result
	 * set.
	 */
	@Test
	void getTripsIgnoresUnsupportedSortDirection() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setSort("price");
		criteria.setDirection("sideways");

		List<Trip> trips = tripService.getTrips(criteria);

		assertEquals(testTrips.size(), trips.size());

		for (int index = 1; index < trips.size(); index++) {
			BigDecimal previousPrice = trips.get(index - 1).getPricePerPerson();
			BigDecimal currentPrice = trips.get(index).getPricePerPerson();

			assertTrue(previousPrice.compareTo(currentPrice) <= 0);
		}
	}

	/**
	 * Verifies that a negative page number is handled safely.
	 */
	@Test
	void getTripsHandlesNegativePageNumber() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setPage(-1);
		criteria.setSize(1);

		List<Trip> trips = tripService.getTrips(criteria);

		assertEquals(1, trips.size());
	}

	/**
	 * Verifies that a zero page size is replaced by a safe default.
	 */
	@Test
	void getTripsHandlesZeroPageSize() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setPage(0);
		criteria.setSize(0);

		List<Trip> trips = tripService.getTrips(criteria);

		assertEquals(testTrips.size(), trips.size());
	}

	/**
	 * Verifies that an oversized page request does not return more records than
	 * exist in the controlled data set.
	 */
	@Test
	void getTripsHandlesOversizedPageSize() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setPage(0);
		criteria.setSize(1000);

		List<Trip> trips = tripService.getTrips(criteria);

		assertEquals(testTrips.size(), trips.size());
	}

	/**
	 * Verifies that blank trip codes do not return a trip through the service
	 * layer.
	 */
	@Test
	void getTripReturnsEmptyForBlankCode() {
		Optional<Trip> trip = tripService.getTrip("   ");

		assertTrue(trip.isEmpty());
	}

	/**
	 * Verifies that unknown trip codes do not return a trip through the service
	 * layer.
	 */
	@Test
	void getTripReturnsEmptyForUnknownCode() {
		Optional<Trip> trip = tripService.getTrip("UNKNOWN999");

		assertTrue(trip.isEmpty());
	}

	/**
	 * Simple repository implementation used only by service tests.
	 */
	private static class TestTripRepository implements TripRepository {
		private final List<Trip> trips;

		/**
		 * Creates a test repository backed by controlled trip data.
		 *
		 * @param trips trips exposed to the service under test
		 */
		TestTripRepository(List<Trip> trips) {
			this.trips = trips;
		}

		@Override
		public List<Trip> findAll() {
			return List.copyOf(trips);
		}

		@Override
		public Optional<Trip> findByCode(String code) {
			if (code == null || code.isBlank()) {
				return Optional.empty();
			}

			for (Trip trip : trips) {
				if (trip.getCode().equalsIgnoreCase(code)) {
					return Optional.of(trip);
				}
			}

			return Optional.empty();
		}
	}
}
