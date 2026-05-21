package com.travlr.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a travel package displayed by the Travlr application.
 *
 * This enhanced model replaces display-oriented string fields from the
 * original MEAN implementation with typed fields that support filtering,
 * sorting, validation, and future PostgreSQL mapping.
 */
public class Trip {
	private String code;
	private String name;
	private int durationDays;
	private LocalDate startDate;
	private String resort;
	private BigDecimal pricePerPerson;
	private String image;
	private String description;

	/**
	 * Creates a trip record with typed fields for application and API use.
	 *
	 * @param code unique trip code used as a stable public identifier
	 * @param name display name of the trip
	 * @param durationDays duration of the trip in days
	 * @param startDate starting date of the trip
	 * @param resort resort name
	 * @param pricePerPerson price per person
	 * @param image image filename
	 * @param description trip description
	 */
	public Trip(String code, String name, int durationDays, LocalDate startDate, String resort,
			BigDecimal pricePerPerson, String image, String description) {
		this.code = code;
		this.name = name;
		this.durationDays = durationDays;
		this.startDate = startDate;
		this.resort = resort;
		this.pricePerPerson = pricePerPerson;
		this.image = image;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getDurationDays() {
		return durationDays;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public String getResort() {
		return resort;
	}

	public BigDecimal getPricePerPerson() {
		return pricePerPerson;
	}

	public String getImage() {
		return image;
	}

	public String getDescription() {
		return description;
	}
}
