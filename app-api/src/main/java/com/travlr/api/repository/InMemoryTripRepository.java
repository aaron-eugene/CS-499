package com.travlr.api.repository;

import com.travlr.api.model.Trip;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Temporary in-memory trip repository used for the milestone 2 implementation.
 *
 * This class provides seed trip data while preserving the repository boundary
 * that will later be implemented with PostgreSQL.
 */
@Repository
public class InMemoryTripRepository implements TripRepository {
	private final List<Trip> trips = List.of(
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
					"Experience a premium reef getaway with luxury lodging and coastal activities.")
	);

	@Override
	public List<Trip> findAll() {
		return List.copyOf(trips);
	}

	@Override
	public Optional<Trip> findByCode(String code) {
		if (code == null || code.isBlank()) {
			return Optional.empty();
		}

		return trips.stream()
				.filter(trip -> trip.getCode().equalsIgnoreCase(code))
				.findFirst();
	}
}
