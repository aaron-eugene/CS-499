package com.travlr.api.repository;

import com.travlr.api.model.Trip;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL-backed implementation of the application trip repository.
 *
 * This adapter keeps the service layer dependent on the project-level
 * TripRepository interface while delegating persistence operations to Spring
 * Data JPA.
 */
@Repository
@Profile("!memory")
public class JpaTripRepository implements TripRepository {
	private final SpringDataTripRepository springDataTripRepository;

	/**
	 * Creates a JPA-backed trip repository.
	 *
	 * @param springDataTripRepository Spring Data repository for Trip entities
	 */
	public JpaTripRepository(SpringDataTripRepository springDataTripRepository) {
		this.springDataTripRepository = springDataTripRepository;
	}

	@Override
	public List<Trip> findAll() {
		return springDataTripRepository.findAll();
	}

	@Override
	public Optional<Trip> findByCode(String code) {
		if (code == null || code.isBlank()) {
			return Optional.empty();
		}

		return springDataTripRepository.findByCodeIgnoreCase(code.trim());
	}
}
