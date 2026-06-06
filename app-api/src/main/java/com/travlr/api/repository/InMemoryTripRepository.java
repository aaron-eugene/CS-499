package com.travlr.api.repository;

import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
import com.travlr.api.model.Trip;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * In-memory trip repository available only through the memory Spring profile.
 *
 * This class preserves a simple fallback implementation of the TripRepository
 * interface for local testing or comparison. The default application profile
 * uses PostgreSQL-backed persistence.
 */
@Repository
@Profile("memory")
public class InMemoryTripRepository implements TripRepository {
	private final List<Trip> trips;
	private final Map<String, Trip> tripsByCode;

	/**
	 * Creates the in-memory repository with seed trip data and a trip-code lookup
	 * map.
	 */
	public InMemoryTripRepository() {
		this.trips = List.of(
				new Trip(
						"GALREE20270214",
						"Gale Reef",
						7,
						LocalDate.of(2027, 2, 14),
						"Emerald Bay, 3 stars",
						new BigDecimal("799.00"),
						"reef1.jpg",
						"Enjoy a week of diving, relaxation, and ocean views at Gale Reef."),
				new Trip(
						"DAWREE20270315",
						"Dawson's Reef",
						5,
						LocalDate.of(2027, 3, 15),
						"Blue Lagoon, 4 stars",
						new BigDecimal("1199.00"),
						"reef2.jpg",
						"Explore clear-water reef sites with guided excursions and resort amenities."),
				new Trip(
						"CLAREE20270621",
						"Claire's Reef",
						4,
						LocalDate.of(2027, 6, 21),
						"Coral Sands, 5 stars",
						new BigDecimal("1999.00"),
						"reef3.jpg",
						"Experience a premium reef getaway with luxury lodging and coastal activities."),
				new Trip(
						"MARISL20270910",
						"Mariner's Isle",
						6,
						LocalDate.of(2027, 9, 10),
						"Azure Cove, 4 stars",
						new BigDecimal("1499.00"),
						"kayak.jpg",
						"Spend six days kayaking, snorkeling, and relaxing along the sheltered coves of Mariner's Isle."),
				new Trip(
						"SUNCOA20271205",
						"Sunset Coast",
						3,
						LocalDate.of(2027, 12, 5),
						"Harbor Point, 3 stars",
						new BigDecimal("599.00"),
						"sea-sound.jpg",
						"Enjoy a shorter coastal escape with beach access, local dining, and sunset views."),
				new Trip(
						"TROPAL20280418",
						"Tropical Lagoon",
						10,
						LocalDate.of(2028, 4, 18),
						"Palm Vista, 5 stars",
						new BigDecimal("2499.00"),
						"deluxe.jpg",
						"Experience a longer luxury lagoon retreat with guided excursions, premium lodging, and resort activities."));

		this.tripsByCode = trips.stream()
				.collect(Collectors.toUnmodifiableMap(
						trip -> normalizeCode(trip.getCode()),
						Function.identity()));
	}

	@Override
	public List<Trip> findAll() {
		return List.copyOf(trips);
	}

	/**
	 * Retrieves all in-memory trips.
	 *
	 * Search, filtering, sorting, and pagination are implemented by the
	 * PostgreSQL-backed repository used in the default application profile. The
	 * memory profile returns the full fallback catalog.
	 *
	 * @param criteria search, filter, sort, and pagination options
	 * @return all in-memory trip records
	 */
	@Override
	public List<Trip> findAll(TripSearchCriteria criteria) {
		return findAll();
	}

	@Override
	public TripSummary getSummary() {
		if (trips.isEmpty()) {
			return new TripSummary(0, List.of(), null, null, null, null);
		}

		TreeSet<String> resorts = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		for (Trip trip : trips) {
			resorts.add(trip.getResort());
		}

		BigDecimal minPrice = trips.stream()
				.map(Trip::getPricePerPerson)
				.min(BigDecimal::compareTo)
				.orElse(null);

		BigDecimal maxPrice = trips.stream()
				.map(Trip::getPricePerPerson)
				.max(BigDecimal::compareTo)
				.orElse(null);

		IntSummaryStatistics durationStats = trips.stream()
				.mapToInt(Trip::getDurationDays)
				.summaryStatistics();

		return new TripSummary(
				trips.size(),
				List.copyOf(resorts),
				minPrice,
				maxPrice,
				durationStats.getMin(),
				durationStats.getMax());
	}

	@Override
	public Optional<Trip> findByCode(String code) {
		if (code == null || code.isBlank()) {
			return Optional.empty();
		}

		return Optional.ofNullable(tripsByCode.get(normalizeCode(code)));
	}

	/**
	 * Normalizes trip codes so lookup behavior is case-insensitive and consistent.
	 *
	 * @param code trip code to normalize
	 * @return normalized trip code
	 */
	private String normalizeCode(String code) {
		return code.trim().toUpperCase(Locale.ROOT);
	}
}
