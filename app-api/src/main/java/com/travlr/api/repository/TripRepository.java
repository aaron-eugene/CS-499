package com.travlr.api.repository;

import com.travlr.api.model.Trip;

import java.util.List;
import java.util.Optional;

/**
 * Defines persistence operations for trip records.
 *
 * This interface separates application logic from the storage mechanism so the
 * current in-memory implementation can later be replaced with PostgreSQL-backed
 * persistence.
 */
public interface TripRepository {
	/**
	 * Retrieves all trip records from the current storage source.
	 *
	 * @return list of trip records
	 */
	List<Trip> findAll();

	/**
	 * Finds one trip by its stable public trip code.
	 *
	 * @param code trip code to search for
	 * @return matching trip, if found
	 */
	Optional<Trip> findByCode(String code);
}
