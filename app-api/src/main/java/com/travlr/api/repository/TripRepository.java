package com.travlr.api.repository;

import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
import com.travlr.api.model.Trip;

import java.util.List;
import java.util.Optional;

/**
 * Defines application-level persistence operations for trip records.
 *
 * This interface separates trip service logic from the storage mechanism. The
 * default implementation uses PostgreSQL-backed persistence, while the memory
 * profile can still provide an in-memory fallback implementation.
 */
public interface TripRepository {
	/**
	 * Retrieves all trip records from the current storage source.
	 *
	 * This method is intended for catalog-wide operations that need complete trip
	 * records rather than filtered or paginated results.
	 *
	 * @return all trip records
	 */
	List<Trip> findAll();

	/**
	 * Retrieves trip records using optional search, filter, sort, and pagination
	 * criteria.
	 *
	 * @param criteria search, filter, sort, and pagination options
	 * @return matching trip records
	 */
	List<Trip> findAll(TripSearchCriteria criteria);

	/**
	 * Computes summary metadata for the trip catalog.
	 *
	 * @return trip catalog summary
	 */
	TripSummary getSummary();

	/**
	 * Finds one trip by its stable public trip code.
	 *
	 * @param code trip code to search for
	 * @return matching trip, if found
	 */
	Optional<Trip> findByCode(String code);
}
