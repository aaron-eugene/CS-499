package com.travlr.api.service;

import com.travlr.api.dto.TripCreateRequest;
import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
import com.travlr.api.dto.TripUpdateRequest;
import com.travlr.api.model.Trip;
import com.travlr.api.repository.TripRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@BeforeEach
	void setUp() {
		testTrips = List.of(
				new Trip(
						"ALPREF20270110",
						"Alpha Reef",
						3,
						LocalDate.of(2027, 1, 10),
						"Coral Bay, 3 stars",
						new BigDecimal("500.00"),
						"reef1.jpg",
						"Short reef trip."),
				new Trip(
						"BRAISL20270515",
						"Bravo Island",
						7,
						LocalDate.of(2027, 5, 15),
						"Blue Harbor, 4 stars",
						new BigDecimal("1500.00"),
						"kayak.jpg",
						"Island kayaking and snorkeling."),
				new Trip(
						"CHALAG20270920",
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

	@Test
	void getTripsHandlesNullCriteria() {
		List<Trip> trips = tripService.getTrips(null);

		assertEquals(testTrips.size(), trips.size());
		assertNotNull(testRepository.lastCriteria);
	}

	@Test
	void getTripFindsTripByCodeCaseInsensitively() {
		String existingCode = testTrips.get(0).getCode().toLowerCase();

		Optional<Trip> trip = tripService.getTrip(existingCode);

		assertTrue(trip.isPresent());
		assertEquals(testTrips.get(0).getCode(), trip.get().getCode());
		assertEquals(existingCode, testRepository.lastLookupCode);
	}

	@Test
	void getTripReturnsEmptyForUnknownCode() {
		Optional<Trip> trip = tripService.getTrip("UNKTRP20990101");

		assertTrue(trip.isEmpty());
		assertEquals("UNKTRP20990101", testRepository.lastLookupCode);
	}

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

	@Test
	void createTripSavesNewTripWhenCodeDoesNotExist() {
		TripCreateRequest request = createRequest();

		Trip createdTrip = tripService.createTrip(request);

		assertEquals("TSTNEW20990101", createdTrip.getCode());
		assertEquals("Test Reef Escape", createdTrip.getName());
		assertSame(createdTrip, testRepository.lastSavedTrip);
		assertTrue(testRepository.existsByCode("TSTNEW20990101"));
	}

	@Test
	void createTripThrowsWhenCodeAlreadyExists() {
		TripCreateRequest request = createRequest();
		request.setCode(testTrips.get(0).getCode());

		assertThrows(IllegalArgumentException.class, () -> tripService.createTrip(request));
	}

	@Test
	void updateTripUpdatesEditableFieldsWhenTripExists() {
		TripUpdateRequest request = updateRequest();

		Optional<Trip> updatedTrip = tripService.updateTrip(testTrips.get(0).getCode(), request);

		assertTrue(updatedTrip.isPresent());
		assertEquals(testTrips.get(0).getCode(), updatedTrip.get().getCode());
		assertEquals("Updated Reef Escape", updatedTrip.get().getName());
		assertEquals(7, updatedTrip.get().getDurationDays());
		assertEquals("Updated Test Resort", updatedTrip.get().getResort());
		assertSame(updatedTrip.get(), testRepository.lastSavedTrip);
	}

	@Test
	void updateTripReturnsEmptyWhenTripDoesNotExist() {
		Optional<Trip> updatedTrip = tripService.updateTrip("UNKTRP20990101", updateRequest());

		assertTrue(updatedTrip.isEmpty());
	}

	@Test
	void deleteTripDeletesExistingTrip() {
		boolean deleted = tripService.deleteTrip(testTrips.get(0).getCode());

		assertTrue(deleted);
		assertEquals(testTrips.get(0).getCode(), testRepository.lastDeletedTrip.getCode());
		assertFalse(testRepository.existsByCode(testTrips.get(0).getCode()));
	}

	@Test
	void deleteTripReturnsFalseWhenTripDoesNotExist() {
		boolean deleted = tripService.deleteTrip("UNKTRP20990101");

		assertFalse(deleted);
	}

	private TripCreateRequest createRequest() {
		TripCreateRequest request = new TripCreateRequest();
		request.setCode("TSTNEW20990101");
		request.setName("Test Reef Escape");
		request.setDurationDays(5);
		request.setStartDate(LocalDate.of(2099, 1, 1));
		request.setResort("Test Resort");
		request.setPricePerPerson(new BigDecimal("1299.99"));
		request.setImageName("test_reef_escape.jpg");
		request.setDescription("A test trip used to verify service create behavior.");

		return request;
	}

	private TripUpdateRequest updateRequest() {
		TripUpdateRequest request = new TripUpdateRequest();
		request.setName("Updated Reef Escape");
		request.setDurationDays(7);
		request.setStartDate(LocalDate.of(2099, 2, 1));
		request.setResort("Updated Test Resort");
		request.setPricePerPerson(new BigDecimal("1499.99"));
		request.setImageName("updated_reef_escape.jpg");
		request.setDescription("A test trip used to verify service update behavior.");

		return request;
	}

	/**
	 * Simple repository implementation used only by service tests.
	 */
	@SuppressWarnings("null")
	private static class TestTripRepository implements TripRepository {
		private final List<Trip> trips;
		private TripSearchCriteria lastCriteria;
		private String lastLookupCode;
		private boolean summaryRequested;
		private Trip lastSavedTrip;
		private Trip lastDeletedTrip;

		TestTripRepository(List<Trip> trips) {
			this.trips = new ArrayList<>(trips);
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

		@Override
		public boolean existsByCode(String code) {
			return findByCode(code).isPresent();
		}

		@Override
		public Trip save(Trip trip) {
			this.lastSavedTrip = trip;

			if (!existsByCode(trip.getCode())) {
				trips.add(trip);
			}

			return trip;
		}

		@Override
		public void delete(Trip trip) {
			this.lastDeletedTrip = trip;
			trips.removeIf(existingTrip -> existingTrip.getCode().equalsIgnoreCase(trip.getCode()));
		}
	}
}
