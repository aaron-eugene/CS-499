package com.travlr.api.service;

import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
import com.travlr.api.model.Trip;
import com.travlr.api.repository.TripRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Provides trip-related application operations for the Travlr API.
 *
 * The service layer keeps controller logic separate from data access and is the
 * place where business rules, validation coordination, query processing, and
 * authorization checks can be added as the application grows.
 *
 * Trip search, filtering, sorting, and pagination are delegated to the
 * repository layer so the default PostgreSQL implementation can perform those
 * operations through database-backed queries. The service remains responsible
 * for coordinating application-level trip operations and delegating catalog
 * summary requests.
 */
@Service
public class TripService {
	private final TripRepository tripRepository;

	/**
	 * Creates a trip service backed by a trip repository.
	 *
	 * @param tripRepository repository used to retrieve trip data
	 */
	public TripService(TripRepository tripRepository) {
		this.tripRepository = tripRepository;
	}

	/**
	 * Finds one trip by its stable public trip code.
	 *
	 * The PostgreSQL-backed repository preserves case-insensitive lookup behavior
	 * with a unique indexed trip-code column.
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
}
