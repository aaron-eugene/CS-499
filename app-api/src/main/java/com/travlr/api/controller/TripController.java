package com.travlr.api.controller;

import com.travlr.api.model.Trip;
import com.travlr.api.service.TripService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles REST API requests for trip resources.
 */
@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "http://localhost:5173")
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
	 * Retrieves all available trips.
	 *
	 * @return list of available trips
	 */
	@GetMapping
	public List<Trip> getAllTrips() {
		return tripService.getAllTrips();
	}
}
