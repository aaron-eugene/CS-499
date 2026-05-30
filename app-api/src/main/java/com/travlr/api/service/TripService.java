package com.travlr.api.service;

import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
import com.travlr.api.model.Trip;
import com.travlr.api.repository.TripRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Provides trip-related application operations for the Travlr API.
 *
 * The service layer keeps controller logic separate from data access and is the
 * place where business rules, validation coordination, query processing, and
 * authorization checks can be added as the application grows.
 *
 * For the current milestone, this class demonstrates algorithmic processing
 * over an in-memory trip collection by applying keyword search, numeric range
 * filters, approved sort options, sort direction, bounded pagination, and
 * catalog summary calculations. When the PostgreSQL layer is added, these rules
 * can move into repository/database queries while preserving the same service
 * boundary.
 */
@Service
public class TripService {
	private static final int DEFAULT_PAGE = 0;
	private static final int DEFAULT_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 50;

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
	 * The current repository implementation uses a map-backed trip-code lookup.
	 * In the PostgreSQL milestone, this behavior should be preserved with a
	 * unique indexed trip-code column.
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
	 * Filtering is applied before sorting and pagination. When no sort option is
	 * requested, the original catalog order from the repository is preserved.
	 *
	 * @param criteria query criteria supplied by the API layer
	 * @return filtered, optionally sorted, and paginated list of trips
	 */
	public List<Trip> getTrips(TripSearchCriteria criteria) {
		TripSearchCriteria safeCriteria = criteria == null ? new TripSearchCriteria() : criteria;

		List<Trip> matchingTrips = tripRepository.findAll().stream()
				.filter(trip -> matchesKeywordSearch(trip, safeCriteria.getSearch()))
				.filter(trip -> isAtLeastMinPrice(trip, safeCriteria.getMinPrice()))
				.filter(trip -> isWithinMaxPrice(trip, safeCriteria.getMaxPrice()))
				.filter(trip -> isAtLeastMinDuration(trip, safeCriteria.getMinDays()))
				.filter(trip -> isWithinMaxDuration(trip, safeCriteria.getMaxDays()))
				.toList();

		List<Trip> orderedTrips = applyRequestedSort(
				matchingTrips,
				safeCriteria.getSort(),
				safeCriteria.getDirection());

		return applyPagination(orderedTrips, safeCriteria.getPage(), safeCriteria.getSize());
	}

	/**
	 * Computes summary metadata for the current trip catalog.
	 *
	 * This method demonstrates collection traversal and derived data generation.
	 * The summary can support frontend filter controls without requiring the
	 * client to duplicate catalog-scanning logic.
	 *
	 * @return summary values for the current trip catalog
	 */
	public TripSummary getTripSummary() {
		List<Trip> trips = tripRepository.findAll();

		if (trips.isEmpty()) {
			return new TripSummary(0, List.of(), null, null, null, null);
		}

		TreeSet<String> resorts = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		for (Trip trip : trips) {
			resorts.add(trip.getResort());
		}

		BigDecimal minPrice = trips.stream()
				.map(Trip::getPricePerPerson)
				.min(Comparator.naturalOrder())
				.orElse(null);

		BigDecimal maxPrice = trips.stream()
				.map(Trip::getPricePerPerson)
				.max(Comparator.naturalOrder())
				.orElse(null);

		IntSummaryStatistics durationStats = trips.stream()
				.mapToInt(Trip::getDurationDays)
				.summaryStatistics();

		return new TripSummary(
				trips.size(),
				List.copyOf(resorts),
				minPrice,
				maxPrice,
				durationStats.getMin(),
				durationStats.getMax());
	}

	/**
	 * Determines whether a trip matches the supplied keyword search.
	 *
	 * The keyword search checks code, name, resort, and description. This gives
	 * callers a simple catalog search without adding field-specific query options
	 * before the database layer is introduced.
	 *
	 * @param trip   trip to evaluate
	 * @param search optional keyword search text
	 * @return true if the trip should be included
	 */
	private boolean matchesKeywordSearch(Trip trip, String search) {
		if (search == null || search.isBlank()) {
			return true;
		}

		String normalizedSearch = search.trim().toLowerCase(Locale.ROOT);

		return trip.getCode().toLowerCase(Locale.ROOT).contains(normalizedSearch)
				|| trip.getName().toLowerCase(Locale.ROOT).contains(normalizedSearch)
				|| trip.getResort().toLowerCase(Locale.ROOT).contains(normalizedSearch)
				|| trip.getDescription().toLowerCase(Locale.ROOT).contains(normalizedSearch);
	}

	/**
	 * Determines whether a trip is greater than or equal to the requested minimum
	 * price.
	 *
	 * @param trip     trip to evaluate
	 * @param minPrice optional minimum price per person
	 * @return true if the trip should be included
	 */
	private boolean isAtLeastMinPrice(Trip trip, BigDecimal minPrice) {
		if (minPrice == null) {
			return true;
		}

		return trip.getPricePerPerson().compareTo(minPrice) >= 0;
	}

	/**
	 * Determines whether a trip is less than or equal to the requested maximum
	 * price.
	 *
	 * @param trip     trip to evaluate
	 * @param maxPrice optional maximum price per person
	 * @return true if the trip should be included
	 */
	private boolean isWithinMaxPrice(Trip trip, BigDecimal maxPrice) {
		if (maxPrice == null) {
			return true;
		}

		return trip.getPricePerPerson().compareTo(maxPrice) <= 0;
	}

	/**
	 * Determines whether a trip is greater than or equal to the requested minimum
	 * duration.
	 *
	 * @param trip    trip to evaluate
	 * @param minDays optional minimum trip duration
	 * @return true if the trip should be included
	 */
	private boolean isAtLeastMinDuration(Trip trip, Integer minDays) {
		if (minDays == null) {
			return true;
		}

		return trip.getDurationDays() >= minDays;
	}

	/**
	 * Determines whether a trip is less than or equal to the requested maximum
	 * duration.
	 *
	 * @param trip    trip to evaluate
	 * @param maxDays optional maximum trip duration
	 * @return true if the trip should be included
	 */
	private boolean isWithinMaxDuration(Trip trip, Integer maxDays) {
		if (maxDays == null) {
			return true;
		}

		return trip.getDurationDays() <= maxDays;
	}

	/**
	 * Applies a requested sort only when the caller supplies a sort option.
	 *
	 * Leaving unsorted results in repository order is intentional because
	 * filtering and sorting are separate operations.
	 *
	 * @param trips     filtered trip list
	 * @param sort      requested sort field
	 * @param direction requested sort direction
	 * @return sorted list when requested, otherwise original filtered order
	 */
	private List<Trip> applyRequestedSort(List<Trip> trips, String sort, String direction) {
		if (sort == null || sort.isBlank()) {
			return trips;
		}

		return trips.stream()
				.sorted(getTripComparator(sort, direction))
				.toList();
	}

	/**
	 * Selects the comparator used to sort trip results.
	 *
	 * @param sort      requested sort field
	 * @param direction requested sort direction
	 * @return comparator for trip sorting
	 */
	private Comparator<Trip> getTripComparator(String sort, String direction) {
		Comparator<Trip> comparator = switch (normalize(sort)) {
			case "price" -> Comparator.comparing(Trip::getPricePerPerson);
			case "startdate", "start-date" -> Comparator.comparing(Trip::getStartDate);
			case "duration", "durationdays", "duration-days" -> Comparator.comparingInt(Trip::getDurationDays);
			case "code" -> Comparator.comparing(Trip::getCode, String.CASE_INSENSITIVE_ORDER);
			default -> Comparator.comparing(Trip::getName, String.CASE_INSENSITIVE_ORDER);
		};

		if ("desc".equals(normalize(direction))) {
			return comparator.reversed();
		}

		return comparator;
	}

	/**
	 * Applies bounded pagination to a list of matching trips.
	 *
	 * Invalid page values fall back to the first page. Invalid size values fall
	 * back to the default size, while very large values are capped to prevent
	 * unbounded responses.
	 *
	 * @param trips         filtered and sorted trip list
	 * @param requestedPage optional zero-based page number
	 * @param requestedSize optional page size
	 * @return paginated trip list
	 */
	private List<Trip> applyPagination(List<Trip> trips, Integer requestedPage, Integer requestedSize) {
		int page = requestedPage == null || requestedPage < 0 ? DEFAULT_PAGE : requestedPage;
		int size = requestedSize == null || requestedSize < 1 ? DEFAULT_SIZE : Math.min(requestedSize, MAX_PAGE_SIZE);

		int fromIndex = page * size;

		if (fromIndex >= trips.size()) {
			return List.of();
		}

		int toIndex = Math.min(fromIndex + size, trips.size());

		return trips.subList(fromIndex, toIndex);
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
