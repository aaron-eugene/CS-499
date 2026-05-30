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
 * Tests the trip service query logic used by the algorithms and data structures
 * enhancement.
 *
 * These tests use a controlled in-memory repository so the service behavior can
 * be verified independently from the application's seed data.
 */
class TripServiceTest {
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
	 * Verifies that all trips are returned when no search criteria are supplied.
	 */
	@Test
	void getTripsReturnsAllTripsWhenCriteriaIsEmpty() {
		List<Trip> trips = tripService.getTrips(new TripSearchCriteria());

		assertEquals(testTrips.size(), trips.size());
	}

	/**
	 * Verifies that maximum price filtering excludes trips above the requested
	 * price.
	 */
	@Test
	void getTripsFiltersByMaximumPrice() {
		BigDecimal maxPrice = new BigDecimal("1500.00");

		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setMaxPrice(maxPrice);

		List<Trip> trips = tripService.getTrips(criteria);

		assertFalse(trips.isEmpty());

		for (Trip trip : trips) {
			assertTrue(trip.getPricePerPerson().compareTo(maxPrice) <= 0);
		}
	}

	/**
	 * Verifies that minimum and maximum price filters can be combined.
	 */
	@Test
	void getTripsFiltersByPriceRange() {
		BigDecimal minPrice = new BigDecimal("1000.00");
		BigDecimal maxPrice = new BigDecimal("2000.00");

		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setMinPrice(minPrice);
		criteria.setMaxPrice(maxPrice);

		List<Trip> trips = tripService.getTrips(criteria);

		assertFalse(trips.isEmpty());

		for (Trip trip : trips) {
			assertTrue(trip.getPricePerPerson().compareTo(minPrice) >= 0);
			assertTrue(trip.getPricePerPerson().compareTo(maxPrice) <= 0);
		}
	}

	/**
	 * Verifies that duration filters can be combined.
	 */
	@Test
	void getTripsFiltersByDurationRange() {
		int minDays = 5;
		int maxDays = 8;

		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setMinDays(minDays);
		criteria.setMaxDays(maxDays);

		List<Trip> trips = tripService.getTrips(criteria);

		assertFalse(trips.isEmpty());

		for (Trip trip : trips) {
			assertTrue(trip.getDurationDays() >= minDays);
			assertTrue(trip.getDurationDays() <= maxDays);
		}
	}

	/**
	 * Verifies that keyword search checks catalog text fields.
	 */
	@Test
	void getTripsSearchesByKeyword() {
		String search = "reef";

		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setSearch(search);

		List<Trip> trips = tripService.getTrips(criteria);

		assertFalse(trips.isEmpty());

		for (Trip trip : trips) {
			String combinedSearchText = (trip.getCode() + " "
					+ trip.getName() + " "
					+ trip.getResort() + " "
					+ trip.getDescription())
					.toLowerCase();

			assertTrue(combinedSearchText.contains(search));
		}
	}

	/**
	 * Verifies that price sorting in descending order produces a correctly ordered
	 * result list.
	 */
	@Test
	void getTripsSortsByPriceDescending() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setSort("price");
		criteria.setDirection("desc");

		List<Trip> trips = tripService.getTrips(criteria);

		assertEquals(testTrips.size(), trips.size());

		for (int index = 1; index < trips.size(); index++) {
			BigDecimal previousPrice = trips.get(index - 1).getPricePerPerson();
			BigDecimal currentPrice = trips.get(index).getPricePerPerson();

			assertTrue(previousPrice.compareTo(currentPrice) >= 0);
		}
	}

	/**
	 * Verifies that pagination returns no more than the requested page size.
	 */
	@Test
	void getTripsAppliesPaginationSizeLimit() {
		int requestedSize = 2;

		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setPage(0);
		criteria.setSize(requestedSize);

		List<Trip> trips = tripService.getTrips(criteria);

		assertTrue(trips.size() <= requestedSize);
	}

	/**
	 * Verifies that different pages do not return the same first record when the
	 * page size creates multiple pages.
	 */
	@Test
	void getTripsReturnsDifferentResultsForDifferentPages() {
		TripSearchCriteria firstPageCriteria = new TripSearchCriteria();
		firstPageCriteria.setPage(0);
		firstPageCriteria.setSize(1);

		TripSearchCriteria secondPageCriteria = new TripSearchCriteria();
		secondPageCriteria.setPage(1);
		secondPageCriteria.setSize(1);

		List<Trip> firstPage = tripService.getTrips(firstPageCriteria);
		List<Trip> secondPage = tripService.getTrips(secondPageCriteria);

		assertEquals(1, firstPage.size());
		assertEquals(1, secondPage.size());
		assertNotEquals(firstPage.get(0).getCode(), secondPage.get(0).getCode());
	}

	/**
	 * Verifies that trip lookup by code is case-insensitive.
	 */
	@Test
	void getTripFindsTripByCodeCaseInsensitively() {
		String existingCode = testTrips.get(0).getCode().toLowerCase();

		Optional<Trip> trip = tripService.getTrip(existingCode);

		assertTrue(trip.isPresent());
		assertEquals(testTrips.get(0).getCode(), trip.get().getCode());
	}

	/**
	 * Verifies that the catalog summary is derived from the repository data.
	 */
	@Test
	void getTripSummaryReturnsCatalogMetadata() {
		TripSummary summary = tripService.getTripSummary();

		assertEquals(testTrips.size(), summary.getTotalTrips());
		assertEquals(findLowestPrice(testTrips), summary.getMinPrice());
		assertEquals(findHighestPrice(testTrips), summary.getMaxPrice());
		assertEquals(findShortestDuration(testTrips), summary.getMinDurationDays());
		assertEquals(findLongestDuration(testTrips), summary.getMaxDurationDays());
	}

	private BigDecimal findLowestPrice(List<Trip> trips) {
		return trips.stream()
				.map(Trip::getPricePerPerson)
				.min(BigDecimal::compareTo)
				.orElseThrow();
	}

	private BigDecimal findHighestPrice(List<Trip> trips) {
		return trips.stream()
				.map(Trip::getPricePerPerson)
				.max(BigDecimal::compareTo)
				.orElseThrow();
	}

	private int findShortestDuration(List<Trip> trips) {
		return trips.stream()
				.mapToInt(Trip::getDurationDays)
				.min()
				.orElseThrow();
	}

	private int findLongestDuration(List<Trip> trips) {
		return trips.stream()
				.mapToInt(Trip::getDurationDays)
				.max()
				.orElseThrow();
	}

	/**
	 * Simple repository implementation used only by service tests.
	 */
	private static class TestTripRepository implements TripRepository {
		private final List<Trip> trips;

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
