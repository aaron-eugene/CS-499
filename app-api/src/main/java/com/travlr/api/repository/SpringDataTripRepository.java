package com.travlr.api.repository;

import com.travlr.api.model.Trip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Trip persistence.
 *
 * This repository provides database-backed access to the trips table while the
 * application-facing TripRepository interface remains stable.
 */
interface SpringDataTripRepository extends JpaRepository<Trip, Long> {
	/**
	 * Finds one trip by its public trip code without requiring an exact case match.
	 *
	 * @param code trip code to search for
	 * @return matching trip, if found
	 */
	Optional<Trip> findByCodeIgnoreCase(String code);
}
