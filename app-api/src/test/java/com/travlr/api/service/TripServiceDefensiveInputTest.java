package com.travlr.api.service;

import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
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
 * Tests defensive handling of service-layer trip input.
 *
 * Query filtering, sorting, and pagination are now handled by the repository
 * layer so the PostgreSQL implementation can perform those operations with
 * database-backed queries. These tests focus on the service boundary: null
 * criteria handling, repository delegation, summary delegation, and safe lookup
 * behavior.
 */
class TripServiceDefensiveInputTest {
	private TripService tripService;
	private TestTripRepository testRepository;
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
						"Island kayaking and snorkeling."));

		testRepository = new TestTripRepository(testTrips);
		tripService = new TripService(testRepository);
	}

	/**
	 * Verifies that null criteria are replaced with a safe empty criteria object
	 * before being passed to the repository.
	 */
	@Test
	void getTripsHandlesNullCriteria() {
		List<Trip> trips = tripService.getTrips(null);

		assertEquals(testTrips.size(), trips.size());
		assertNotNull(testRepository.lastCriteria);
	}

	/**
	 * Verifies that supplied criteria are delegated to the repository.
	 */
	@Test
	void getTripsDelegatesCriteriaToRepository() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setSearch("reef");
		criteria.setSort("price");
		criteria.setDirection("desc");

		List<Trip> trips = tripService.getTrips(criteria);

		assertEquals(testTrips.size(), trips.size());
		assertSame(criteria, testRepository.lastCriteria);
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
	 * Verifies that summary requests are delegated to the repository.
	 */
	@Test
	void getTripSummaryDelegatesToRepository() {
		TripSummary summary = tripService.getTripSummary();

		assertEquals(testTrips.size(), summary.getTotalTrips());
		assertTrue(testRepository.summaryRequested);
	}

	/**
	 * Simple repository implementation used only by service tests.
	 */
	private static class TestTripRepository implements TripRepository {
		private final List<Trip> trips;
		private TripSearchCriteria lastCriteria;
		private boolean summaryRequested;

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
		public List<Trip> findAll(TripSearchCriteria criteria) {
			this.lastCriteria = criteria;

			return findAll();
		}

		@Override
		public TripSummary getSummary() {
			this.summaryRequested = true;

			return new TripSummary(
					trips.size(),
					List.of(),
					null,
					null,
					null,
					null);
		}

		@Override
		public boolean existsByCode(String code) {
			return false;
		}

		@Override
		public Trip save(Trip trip) {
			return trip;
		}

		@Override
		public void delete(Trip trip) {
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
