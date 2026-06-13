package com.travlr.api.repository;

import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
import com.travlr.api.model.Trip;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TreeSet;

/**
 * PostgreSQL-backed implementation of the application trip repository.
 *
 * This adapter keeps the service layer dependent on the project-level
 * TripRepository interface while delegating persistence operations to Spring
 * Data JPA.
 */
@Repository
public class JpaTripRepository implements TripRepository {
	private static final int DEFAULT_PAGE = 0;
	private static final int DEFAULT_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 50;

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
		return springDataTripRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
	}

	@Override
	public List<Trip> findAll(TripSearchCriteria criteria) {
		TripSearchCriteria safeCriteria = criteria == null ? new TripSearchCriteria() : criteria;

		Specification<Trip> specification = buildSpecification(safeCriteria);
		Pageable pageable = buildPageable(safeCriteria);

		return springDataTripRepository.findAll(specification, pageable).getContent();
	}

	/**
	 * Computes catalog summary metadata through database-backed aggregate queries.
	 *
	 * Numeric summary values are calculated by the database, and distinct resort
	 * values are retrieved before being sorted for a stable API response.
	 *
	 * @return summary values for the trip catalog
	 */
	@Override
	public TripSummary getSummary() {
		SpringDataTripRepository.TripSummaryStats stats = springDataTripRepository.findTripSummaryStats();

		int totalTrips = stats.getTotalTrips() == null
				? 0
				: Math.toIntExact(stats.getTotalTrips());

		if (totalTrips == 0) {
			return new TripSummary(0, List.of(), null, null, null, null);
		}

		TreeSet<String> resorts = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		resorts.addAll(springDataTripRepository.findDistinctResorts());

		return new TripSummary(
				totalTrips,
				List.copyOf(resorts),
				stats.getMinPrice(),
				stats.getMaxPrice(),
				stats.getMinDurationDays(),
				stats.getMaxDurationDays());
	}

	@Override
	public Optional<Trip> findByCode(String code) {
		if (code == null || code.isBlank()) {
			return Optional.empty();
		}

		return springDataTripRepository.findByCodeIgnoreCase(code.trim());
	}

	@Override
	public boolean existsByCode(String code) {
		if (code == null || code.isBlank()) {
			return false;
		}

		return springDataTripRepository.existsByCodeIgnoreCase(code.trim());
	}

	@Override
	@NonNull
	public Trip save(@NonNull Trip trip) {
		return springDataTripRepository.save(trip);
	}

	@Override
	public void delete(@NonNull Trip trip) {
		springDataTripRepository.delete(trip);
	}

	/**
	 * Builds the database query filters for trip search criteria.
	 *
	 * @param criteria search and filter criteria
	 * @return JPA specification for matching trips
	 */
	@NonNull
	private Specification<Trip> buildSpecification(TripSearchCriteria criteria) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (criteria.getSearch() != null && !criteria.getSearch().isBlank()) {
				String searchPattern = "%" + criteria.getSearch().trim().toLowerCase(Locale.ROOT) + "%";

				Predicate codeMatches = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("code")),
						searchPattern);

				Predicate nameMatches = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("name")),
						searchPattern);

				Predicate resortMatches = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("resort")),
						searchPattern);

				Predicate descriptionMatches = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("description")),
						searchPattern);

				predicates.add(criteriaBuilder.or(
						codeMatches,
						nameMatches,
						resortMatches,
						descriptionMatches));
			}

			if (criteria.getMinPrice() != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(
						root.get("pricePerPerson"),
						criteria.getMinPrice()));
			}

			if (criteria.getMaxPrice() != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(
						root.get("pricePerPerson"),
						criteria.getMaxPrice()));
			}

			if (criteria.getMinDays() != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(
						root.get("durationDays"),
						criteria.getMinDays()));
			}

			if (criteria.getMaxDays() != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(
						root.get("durationDays"),
						criteria.getMaxDays()));
			}

			return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
		};
	}

	/**
	 * Builds bounded pagination and approved sorting for database-backed trip
	 * queries.
	 *
	 * @param criteria query criteria supplied by the API layer
	 * @return pageable request for Spring Data JPA
	 */
	@NonNull
	private Pageable buildPageable(TripSearchCriteria criteria) {
		int page = criteria.getPage() == null || criteria.getPage() < 0
				? DEFAULT_PAGE
				: criteria.getPage();

		int size = criteria.getSize() == null || criteria.getSize() < 1
				? DEFAULT_SIZE
				: Math.min(criteria.getSize(), MAX_PAGE_SIZE);

		return PageRequest.of(page, size, buildSort(criteria.getSort(), criteria.getDirection()));
	}

	/**
	 * Builds an approved sort option.
	 *
	 * Missing sort values preserve catalog order by id. Unsupported sort values
	 * fall back to name so callers cannot sort by arbitrary entity fields.
	 *
	 * @param sort      requested sort field
	 * @param direction requested direction
	 * @return approved sort
	 */
	@NonNull
	private Sort buildSort(String sort, String direction) {
		Sort.Direction sortDirection = "desc".equals(normalize(direction))
				? Sort.Direction.DESC
				: Sort.Direction.ASC;

		String sortProperty = switch (normalize(sort)) {
			case "" -> "id";
			case "price" -> "pricePerPerson";
			case "startdate", "start-date" -> "startDate";
			case "duration", "durationdays", "duration-days" -> "durationDays";
			case "code" -> "code";
			case "name" -> "name";
			default -> "name";
		};

		return Sort.by(sortDirection, sortProperty);
	}

	/**
	 * Normalizes optional string input for comparisons.
	 *
	 * @param value value to normalize
	 * @return normalized value, or an empty string when absent
	 */
	private String normalize(String value) {
		if (value == null) {
			return "";
		}

		return value.trim().toLowerCase(Locale.ROOT);
	}
}
