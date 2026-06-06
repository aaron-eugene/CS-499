package com.travlr.api.repository;

import com.travlr.api.model.Trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Internal Spring Data JPA repository for Trip persistence.
 *
 * This repository provides database-backed access to the trips table while the
 * application-facing TripRepository interface remains stable. It is package-
 * private so service classes depend on the project-level repository abstraction
 * instead of Spring Data directly.
 */
interface SpringDataTripRepository extends JpaRepository<Trip, Long>, JpaSpecificationExecutor<Trip> {
	/**
	 * Finds one trip by its public trip code without requiring an exact case match.
	 *
	 * @param code trip code to search for
	 * @return matching trip, if found
	 */
	Optional<Trip> findByCodeIgnoreCase(String code);

	/**
	 * Retrieves aggregate summary values from the trip catalog.
	 *
	 * @return summary projection containing catalog aggregate values
	 */
	@Query("""
			select
				count(t) as totalTrips,
				min(t.pricePerPerson) as minPrice,
				max(t.pricePerPerson) as maxPrice,
				min(t.durationDays) as minDurationDays,
				max(t.durationDays) as maxDurationDays
			from Trip t
			""")
	TripSummaryStats findTripSummaryStats();

	/**
	 * Retrieves distinct resort values from the trip catalog.
	 *
	 * @return distinct resort names
	 */
	@Query("select distinct t.resort from Trip t order by t.resort")
	List<String> findDistinctResorts();

	/**
	 * Projection interface for trip catalog aggregate values.
	 */
	interface TripSummaryStats {
		Long getTotalTrips();

		BigDecimal getMinPrice();

		BigDecimal getMaxPrice();

		Integer getMinDurationDays();

		Integer getMaxDurationDays();
	}
}
