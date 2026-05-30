package com.travlr.api.controller;

import com.travlr.api.dto.TripSearchCriteria;
import com.travlr.api.dto.TripSummary;
import com.travlr.api.model.Trip;
import com.travlr.api.service.TripService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles REST API requests for trip resources.
 *
 * This controller exposes read-only trip endpoints for the current milestone
 * implementation. It accepts query criteria from API callers and delegates
 * search, filter, sort, pagination, lookup, and summary behavior to the service
 * layer. Future create, update, and delete endpoints should be protected with
 * admin authorization before they are exposed.
 */
@RestController
@RequestMapping("/api/trips")
public class TripController {
	private final TripService tripService;

	/**
	 * Creates a trip controller with access to trip service operations.
	 *
	 * @param tripService service used to retrieve trip data
	 */
	public TripController(TripService tripService) {
		this.tripService = tripService;
	}

	/**
	 * Retrieves computed summary metadata for the trip catalog.
	 *
	 * @return trip catalog summary
	 */
	@GetMapping("/summary")
	public TripSummary getTripSummary() {
		return tripService.getTripSummary();
	}

	/**
	 * Retrieves one trip by its stable public trip code.
	 *
	 * @param code unique trip code
	 * @return matching trip or 404 if no trip is found
	 */
	@GetMapping("/{code}")
	public ResponseEntity<Trip> getTrip(@PathVariable String code) {
		return tripService.getTrip(code)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Retrieves available trips using optional search, filter, sort, and pagination
	 * criteria.
	 *
	 * Spring binds matching query parameters into the TripSearchCriteria object.
	 * Supported parameters include search, minPrice, maxPrice, minDays, maxDays,
	 * sort, direction, page, and size.
	 *
	 * @param criteria query criteria supplied by the request
	 * @return list of matching trips
	 */
	@GetMapping
	public List<Trip> getTrips(TripSearchCriteria criteria) {
		return tripService.getTrips(criteria);
	}
}
