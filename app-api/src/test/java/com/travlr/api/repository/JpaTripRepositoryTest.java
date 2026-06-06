package com.travlr.api.repository;

import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
import com.travlr.api.model.Trip;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the PostgreSQL-backed trip repository implementation.
 *
 * These tests verify that search, filtering, sorting, pagination, lookup, and
 * summary behavior are performed through the default database-backed repository
 * path rather than the memory profile fallback.
 */
@SpringBootTest
@ActiveProfiles("test")
class JpaTripRepositoryTest {
	@Autowired
	private TripRepository tripRepository;

	/**
	 * Verifies that Flyway seed data is available through the repository.
	 */
	@Test
	void findAllReturnsSeededTrips() {
		List<Trip> trips = tripRepository.findAll();

		assertFalse(trips.isEmpty());
		assertTrue(trips.stream().anyMatch(trip -> "GALREE20270214".equals(trip.getCode())));
	}

	/**
	 * Verifies that trip lookup by public code is case-insensitive.
	 */
	@Test
	void findByCodeFindsTripCaseInsensitively() {
		Optional<Trip> trip = tripRepository.findByCode("galree20270214");

		assertTrue(trip.isPresent());
		assertEquals("GALREE20270214", trip.get().getCode());
	}

	/**
	 * Verifies that blank trip codes return an empty result.
	 */
	@Test
	void findByCodeReturnsEmptyForBlankCode() {
		Optional<Trip> trip = tripRepository.findByCode("   ");

		assertTrue(trip.isEmpty());
	}

	/**
	 * Verifies that unknown trip codes return an empty result.
	 */
	@Test
	void findByCodeReturnsEmptyForUnknownCode() {
		Optional<Trip> trip = tripRepository.findByCode("UNKNOWN99999999");

		assertTrue(trip.isEmpty());
	}

	/**
	 * Verifies that keyword search is applied by the database-backed query path.
	 */
	@Test
	void findAllSearchesByKeyword() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setSearch("reef");

		List<Trip> trips = tripRepository.findAll(criteria);

		assertFalse(trips.isEmpty());

		for (Trip trip : trips) {
			String searchableText = (trip.getCode() + " "
					+ trip.getName() + " "
					+ trip.getResort() + " "
					+ trip.getDescription())
					.toLowerCase(Locale.ROOT);

			assertTrue(searchableText.contains("reef"));
		}
	}

	/**
	 * Verifies that price range filters are applied by the database-backed query
	 * path.
	 */
	@Test
	void findAllFiltersByPriceRange() {
		BigDecimal minPrice = new BigDecimal("1000.00");
		BigDecimal maxPrice = new BigDecimal("2000.00");

		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setMinPrice(minPrice);
		criteria.setMaxPrice(maxPrice);

		List<Trip> trips = tripRepository.findAll(criteria);

		assertFalse(trips.isEmpty());

		for (Trip trip : trips) {
			assertTrue(trip.getPricePerPerson().compareTo(minPrice) >= 0);
			assertTrue(trip.getPricePerPerson().compareTo(maxPrice) <= 0);
		}
	}

	/**
	 * Verifies that duration range filters are applied by the database-backed query
	 * path.
	 */
	@Test
	void findAllFiltersByDurationRange() {
		int minDays = 5;
		int maxDays = 7;

		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setMinDays(minDays);
		criteria.setMaxDays(maxDays);

		List<Trip> trips = tripRepository.findAll(criteria);

		assertFalse(trips.isEmpty());

		for (Trip trip : trips) {
			assertTrue(trip.getDurationDays() >= minDays);
			assertTrue(trip.getDurationDays() <= maxDays);
		}
	}

	/**
	 * Verifies that sorting by price descending returns records in the expected
	 * order.
	 */
	@Test
	void findAllSortsByPriceDescending() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setSort("price");
		criteria.setDirection("desc");

		List<Trip> trips = tripRepository.findAll(criteria);

		assertFalse(trips.isEmpty());

		for (int index = 1; index < trips.size(); index++) {
			BigDecimal previousPrice = trips.get(index - 1).getPricePerPerson();
			BigDecimal currentPrice = trips.get(index).getPricePerPerson();

			assertTrue(previousPrice.compareTo(currentPrice) >= 0);
		}
	}

	/**
	 * Verifies that pagination limits the number of returned records.
	 */
	@Test
	void findAllAppliesPaginationSizeLimit() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setPage(0);
		criteria.setSize(2);

		List<Trip> trips = tripRepository.findAll(criteria);

		assertEquals(2, trips.size());
	}

	/**
	 * Verifies that different pages return different records when the page size
	 * creates multiple pages.
	 */
	@Test
	void findAllReturnsDifferentResultsForDifferentPages() {
		TripSearchCriteria firstPageCriteria = new TripSearchCriteria();
		firstPageCriteria.setPage(0);
		firstPageCriteria.setSize(1);

		TripSearchCriteria secondPageCriteria = new TripSearchCriteria();
		secondPageCriteria.setPage(1);
		secondPageCriteria.setSize(1);

		List<Trip> firstPage = tripRepository.findAll(firstPageCriteria);
		List<Trip> secondPage = tripRepository.findAll(secondPageCriteria);

		assertEquals(1, firstPage.size());
		assertEquals(1, secondPage.size());
		assertNotEquals(firstPage.get(0).getCode(), secondPage.get(0).getCode());
	}

	/**
	 * Verifies that unsupported sort fields are handled safely.
	 */
	@Test
	void findAllHandlesUnsupportedSortField() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setSort("notARealField");

		List<Trip> trips = tripRepository.findAll(criteria);

		assertFalse(trips.isEmpty());
	}

	/**
	 * Verifies that oversized page requests are capped by the repository.
	 */
	@Test
	void findAllHandlesOversizedPageSize() {
		TripSearchCriteria criteria = new TripSearchCriteria();
		criteria.setPage(0);
		criteria.setSize(1000);

		List<Trip> trips = tripRepository.findAll(criteria);

		assertFalse(trips.isEmpty());
		assertTrue(trips.size() <= 50);
	}

	/**
	 * Verifies that catalog summary values are produced by repository aggregation.
	 */
	@Test
	void getSummaryReturnsCatalogMetadata() {
		TripSummary summary = tripRepository.getSummary();

		assertEquals(6, summary.getTotalTrips());
		assertFalse(summary.getResorts().isEmpty());
		assertEquals(new BigDecimal("599.00"), summary.getMinPrice());
		assertEquals(new BigDecimal("2499.00"), summary.getMaxPrice());
		assertEquals(3, summary.getMinDurationDays());
		assertEquals(10, summary.getMaxDurationDays());
	}
}
