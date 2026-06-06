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
 * Tests the TripService application boundary.
 *
 * Search, filtering, sorting, pagination, and summary aggregation are delegated
 * to the repository layer so the PostgreSQL-backed implementation can perform
 * those operations with database queries. These tests verify that the service
 * coordinates trip operations without depending on the storage implementation.
 */
class TripServiceTest {
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

		testRepository = new TestTripRepository(testTrips);
		tripService = new TripService(testRepository);
	}

	/**
	 * Verifies that trip criteria are delegated to the repository.
	 */
	@Test
	void getTripsDelegatesCriteriaToRepository() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setSearch("reef");
		criteria.setMinPrice(new BigDecimal("500.00"));
		criteria.setMaxPrice(new BigDecimal("2000.00"));
		criteria.setSort("price");
		criteria.setDirection("desc");
		criteria.setPage(0);
		criteria.setSize(2);

		List<Trip> trips = tripService.getTrips(criteria);

		assertEquals(testTrips.size(), trips.size());
		assertSame(criteria, testRepository.lastCriteria);
	}

	/**
	 * Verifies that null criteria are replaced with a safe empty criteria object.
	 */
	@Test
	void getTripsHandlesNullCriteria() {
		List<Trip> trips = tripService.getTrips(null);

		assertEquals(testTrips.size(), trips.size());
		assertNotNull(testRepository.lastCriteria);
	}

	/**
	 * Verifies that trip lookup by code is delegated to the repository.
	 */
	@Test
	void getTripFindsTripByCodeCaseInsensitively() {
		String existingCode = testTrips.get(0).getCode().toLowerCase();

		Optional<Trip> trip = tripService.getTrip(existingCode);

		assertTrue(trip.isPresent());
		assertEquals(testTrips.get(0).getCode(), trip.get().getCode());
		assertEquals(existingCode, testRepository.lastLookupCode);
	}

	/**
	 * Verifies that an unknown trip code returns an empty result.
	 */
	@Test
	void getTripReturnsEmptyForUnknownCode() {
		Optional<Trip> trip = tripService.getTrip("UNKNOWN999");

		assertTrue(trip.isEmpty());
		assertEquals("UNKNOWN999", testRepository.lastLookupCode);
	}

	/**
	 * Verifies that catalog summary requests are delegated to the repository.
	 */
	@Test
	void getTripSummaryDelegatesToRepository() {
		TripSummary summary = tripService.getTripSummary();

		assertTrue(testRepository.summaryRequested);
		assertEquals(testTrips.size(), summary.getTotalTrips());
		assertEquals(new BigDecimal("500.00"), summary.getMinPrice());
		assertEquals(new BigDecimal("2500.00"), summary.getMaxPrice());
		assertEquals(3, summary.getMinDurationDays());
		assertEquals(10, summary.getMaxDurationDays());
	}

	/**
	 * Simple repository implementation used only by service tests.
	 */
	private static class TestTripRepository implements TripRepository {
		private final List<Trip> trips;
		private TripSearchCriteria lastCriteria;
		private String lastLookupCode;
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
					List.of("Blue Harbor, 4 stars", "Coral Bay, 3 stars", "Palm Vista, 5 stars"),
					new BigDecimal("500.00"),
					new BigDecimal("2500.00"),
					3,
					10);
		}

		@Override
		public Optional<Trip> findByCode(String code) {
			this.lastLookupCode = code;

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
