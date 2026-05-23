package com.travlr.api.service;

import com.travlr.api.model.Trip;
import com.travlr.api.repository.TripRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Provides trip-related application operations for the Travlr API.
 *
 * The service layer keeps controller logic separate from data access and is the
 * place where business rules, validation coordination, and authorization checks
 * can be added as the application grows.
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
	 * @param code trip code to search for
	 * @return matching trip, if found
	 */
	public Optional<Trip> getTripByCode(String code) {
		return tripRepository.findByCode(code);
	}

	/**
	 * Retrieves available trips using an approved sort option.
	 *
	 * @param sort optional sort field: name, price, startDate, or duration
	 * @return sorted list of trips
	 */
	public List<Trip> getTrips(String sort) {
		return tripRepository.findAll().stream()
				.sorted(getTripComparator(sort))
				.toList();
	}

    /**
     * Retrieves available trips using optional filtering and an approved sort option.
     *
     * @param maxPrice optional maximum price per person
     * @param sort optional sort field: name, price, startDate, or duration
     * @return filtered and sorted list of trips
     */
    public List<Trip> getTrips(BigDecimal maxPrice, String sort) {
        return tripRepository.findAll().stream()
                .filter(trip -> isWithinMaxPrice(trip, maxPrice))
                .sorted(getTripComparator(sort))
                .toList();
    }

	private Comparator<Trip> getTripComparator(String sort) {
		if (sort == null) {
			return Comparator.comparing(Trip::getName);
		}

		return switch (sort.toLowerCase()) {
			case "price" -> Comparator.comparing(Trip::getPricePerPerson);
			case "startdate" -> Comparator.comparing(Trip::getStartDate);
			case "duration" -> Comparator.comparingInt(Trip::getDurationDays);
			default -> Comparator.comparing(Trip::getName);
		};
	}

    /**
     * Determines whether a trip is within the requested maximum price.
     *
     * @param trip trip to evaluate
     * @param maxPrice optional maximum price per person
     * @return true if the trip should be included
     */
    private boolean isWithinMaxPrice(Trip trip, BigDecimal maxPrice) {
        if (maxPrice == null) {
            return true;
        }

        return trip.getPricePerPerson().compareTo(maxPrice) <= 0;
    }
}
