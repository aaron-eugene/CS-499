package com.travlr.api.controller;

import com.travlr.api.model.Trip;
import com.travlr.api.service.TripService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles REST API requests for trip resources.
 *
 * This controller exposes read-only trip endpoints for the Milestone Two
 * implementation. Future create, update, and delete endpoints should be
 * protected with admin authorization before they are exposed.
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
	 * Retrieves available trips using optional filtering and an approved sort field.
	 *
	 * @param maxPrice optional maximum price per person
	 * @param sort optional sort field: name, price, startDate, or duration
	 * @return list of available trips
	 */
	@GetMapping
	public List<Trip> getTrips(
			@RequestParam(required = false) BigDecimal maxPrice,
			@RequestParam(required = false) String sort) {
		return tripService.getTrips(maxPrice, sort);
	}
}
