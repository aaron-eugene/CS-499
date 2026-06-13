package com.travlr.api.service;

import com.travlr.api.dto.TripCreateRequest;
import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
import com.travlr.api.dto.TripUpdateRequest;
import com.travlr.api.model.Trip;
import com.travlr.api.repository.TripRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Provides trip-related application operations for the Travlr API.
 *
 * The service layer keeps controller logic separate from data access and
 * coordinates application-level trip operations such as lookup, catalog
 * queries,
 * creation, updates, deletion, and summary retrieval.
 *
 * Trip search, filtering, sorting, and pagination are delegated to the
 * repository layer so the default PostgreSQL implementation can perform those
 * operations through database-backed queries.
 */
@Service
public class TripService {
	private final TripRepository tripRepository;

	/**
	 * Creates a trip service backed by a trip repository.
	 *
	 * @param tripRepository repository used to access trip data
	 */
	public TripService(TripRepository tripRepository) {
		this.tripRepository = tripRepository;
	}

	/**
	 * Finds one trip by its stable public trip code.
	 *
	 * The repository preserves case-insensitive lookup behavior for public trip
	 * codes.
	 *
	 * @param code trip code to search for
	 * @return matching trip, if found
	 */
	public Optional<Trip> getTrip(String code) {
		return tripRepository.findByCode(code);
	}

	/**
	 * Retrieves available trips using optional search, filtering, sorting, and
	 * pagination criteria.
	 *
	 * @param criteria query criteria supplied by the API layer
	 * @return matching trip records
	 */
	public List<Trip> getTrips(TripSearchCriteria criteria) {
		TripSearchCriteria safeCriteria = criteria == null ? new TripSearchCriteria() : criteria;

		return tripRepository.findAll(safeCriteria);
	}

	/**
	 * Computes summary metadata for the current trip catalog.
	 *
	 * Summary calculation is delegated to the repository so the default
	 * PostgreSQL-backed implementation can use database aggregate queries.
	 *
	 * @return summary values for the current trip catalog
	 */
	public TripSummary getTripSummary() {
		return tripRepository.getSummary();
	}

	/**
	 * Creates a new trip from validated client-provided data.
	 *
	 * @param request create request containing the new trip details
	 * @return created trip
	 * @throws IllegalArgumentException if the trip code already exists
	 */
	public Trip createTrip(TripCreateRequest request) {
		if (tripRepository.existsByCode(request.getCode())) {
			throw new IllegalArgumentException("Trip code already exists.");
		}

		Trip trip = new Trip(
				request.getCode(),
				request.getName(),
				request.getDurationDays(),
				request.getStartDate(),
				request.getResort(),
				request.getPricePerPerson(),
				request.getImageName(),
				request.getDescription());

		return tripRepository.save(trip);
	}

	/**
	 * Updates an existing trip identified by its stable public trip code.
	 *
	 * @param code    public trip code from the route path
	 * @param request update request containing editable trip details
	 * @return updated trip, or empty if no matching trip exists
	 */
	public Optional<Trip> updateTrip(String code, TripUpdateRequest request) {
		return tripRepository.findByCode(code)
				.map(existingTrip -> {
					existingTrip.updateDetails(
							request.getName(),
							request.getDurationDays(),
							request.getStartDate(),
							request.getResort(),
							request.getPricePerPerson(),
							request.getImageName(),
							request.getDescription());

					return tripRepository.save(existingTrip);
				});
	}

	/**
	 * Deletes an existing trip identified by its stable public trip code.
	 *
	 * @param code public trip code from the route path
	 * @return true if a trip was found and deleted; otherwise false
	 */
	public boolean deleteTrip(String code) {
		Optional<Trip> trip = tripRepository.findByCode(code);

		trip.ifPresent(tripRepository::delete);

		return trip.isPresent();
	}
}
