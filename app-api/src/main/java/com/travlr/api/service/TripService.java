package com.travlr.api.service;

import com.travlr.api.model.Trip;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Provides trip data for the Travlr API.
 *
 * This service currently uses in-memory seed data so the enhanced application
 * can demonstrate layered backend structure before PostgreSQL persistence is
 * added in a later enhancement.
 */
@Service
public class TripService {
	private final List<Trip> trips = List.of(
			new Trip(
					"GALR210214",
					"Gale Reef",
					7,
					LocalDate.of(2027, 2, 14),
					"Emerald Bay, 3 stars",
					new BigDecimal("799.00"),
					"reef1.jpg",
					"Enjoy a week of diving, relaxation, and ocean views at Gale Reef."),
			new Trip(
					"DAWR210315",
					"Dawson's Reef",
					5,
					LocalDate.of(2027, 3, 15),
					"Blue Lagoon, 4 stars",
					new BigDecimal("1199.00"),
					"reef2.jpg",
					"Explore clear-water reef sites with guided excursions and resort amenities."),
			new Trip(
					"CLAR210621",
					"Claire's Reef",
					4,
					LocalDate.of(2027, 6, 21),
					"Coral Sands, 5 stars",
					new BigDecimal("1999.00"),
					"reef3.jpg",
					"Experience a premium reef getaway with luxury lodging and coastal activities.")
	);

	/**
	 * Retrieves all available trips.
	 *
	 * @return list of available trips
	 */
	public List<Trip> getAllTrips() {
		return trips;
	}
}
