package com.travlr.api.controller;

import com.travlr.api.dto.TripCreateRequest;
import com.travlr.api.dto.TripUpdateRequest;
import com.travlr.api.model.Trip;
import com.travlr.api.service.TripService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides administrative trip-management endpoints.
 *
 * Public trip browsing remains in TripController. This controller separates
 * create, update, and delete operations so they can be protected by
 * authorization rules without changing the public read-only API.
 */
@RestController
@RequestMapping("/api/admin/trips")
public class AdminTripController {
	private final TripService tripService;

	public AdminTripController(TripService tripService) {
		this.tripService = tripService;
	}

	/**
	 * Creates a new trip.
	 *
	 * @param request validated trip creation data
	 * @return created trip with HTTP 201, or HTTP 409 if the trip code already
	 *         exists
	 */
	@PostMapping
	public ResponseEntity<Trip> createTrip(@Valid @RequestBody TripCreateRequest request) {
		try {
			Trip createdTrip = tripService.createTrip(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdTrip);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	/**
	 * Updates an existing trip by public trip code.
	 *
	 * @param code    public trip code
	 * @param request validated trip update data
	 * @return updated trip, or HTTP 404 if no matching trip exists
	 */
	@PutMapping("/{code}")
	public ResponseEntity<Trip> updateTrip(@PathVariable String code,
			@Valid @RequestBody TripUpdateRequest request) {
		return tripService.updateTrip(code, request)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * Deletes an existing trip by public trip code.
	 *
	 * @param code public trip code
	 * @return HTTP 204 if deleted, or HTTP 404 if no matching trip exists
	 */
	@DeleteMapping("/{code}")
	public ResponseEntity<Void> deleteTrip(@PathVariable String code) {
		if (!tripService.deleteTrip(code)) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.noContent().build();
	}
}
