package com.travlr.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents summary information for the trip catalog.
 *
 * This response DTO exposes derived catalog values, including distinct resort
 * values and numeric ranges that can be used by a client to build filtering
 * controls. In the default PostgreSQL-backed implementation, these values are
 * produced through repository/database aggregation rather than client-side
 * calculation.
 */
public class TripSummary {
	private final int totalTrips;
	private final List<String> resorts;
	private final BigDecimal minPrice;
	private final BigDecimal maxPrice;
	private final Integer minDurationDays;
	private final Integer maxDurationDays;

	/**
	 * Creates a summary of the current trip catalog.
	 *
	 * @param totalTrips      number of available trips
	 * @param resorts         distinct resort values in sorted order
	 * @param minPrice        lowest price per person
	 * @param maxPrice        highest price per person
	 * @param minDurationDays shortest trip duration
	 * @param maxDurationDays longest trip duration
	 */
	public TripSummary(int totalTrips, List<String> resorts, BigDecimal minPrice,
			BigDecimal maxPrice, Integer minDurationDays, Integer maxDurationDays) {
		this.totalTrips = totalTrips;
		this.resorts = resorts == null ? List.of() : List.copyOf(resorts);
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.minDurationDays = minDurationDays;
		this.maxDurationDays = maxDurationDays;
	}

	public int getTotalTrips() {
		return totalTrips;
	}

	public List<String> getResorts() {
		return resorts;
	}

	public BigDecimal getMinPrice() {
		return minPrice;
	}

	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	public Integer getMinDurationDays() {
		return minDurationDays;
	}

	public Integer getMaxDurationDays() {
		return maxDurationDays;
	}
}
