package com.travlr.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a travel package displayed by the Travlr application.
 *
 * This enhanced model replaces display-oriented string fields from the
 * original MEAN implementation with typed fields that support filtering,
 * sorting, validation, and PostgreSQL persistence.
 *
 * The trip code remains the stable public identifier used by the API, while the
 * generated database id provides an internal primary key for relational
 * persistence.
 */
@Entity
@Table(name = "trips", uniqueConstraints = {
		@UniqueConstraint(name = "uk_trips_code", columnNames = "code")
}, indexes = {
		@Index(name = "idx_trips_code", columnList = "code"),
		@Index(name = "idx_trips_name", columnList = "name"),
		@Index(name = "idx_trips_resort", columnList = "resort"),
		@Index(name = "idx_trips_start_date", columnList = "start_date")
})
public class Trip {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 14)
	private String code;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(name = "duration_days", nullable = false)
	private int durationDays;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(nullable = false, length = 150)
	private String resort;

	@Column(name = "price_per_person", nullable = false, precision = 10, scale = 2)
	private BigDecimal pricePerPerson;

	@Column(name = "image_name", nullable = false, length = 100)
	private String imageName;

	@Column(nullable = false, length = 1000)
	private String description;

	/**
	 * Required by JPA for entity construction.
	 */
	protected Trip() {
	}

	/**
	 * Creates a trip record with typed fields for application and API use.
	 *
	 * @param code           unique trip code used as a stable public identifier
	 * @param name           display name of the trip
	 * @param durationDays   duration of the trip in days
	 * @param startDate      starting date of the trip
	 * @param resort         resort name
	 * @param pricePerPerson price per person
	 * @param imageName      controlled image filename used by the frontend
	 * @param description    plain-text trip description for display
	 */
	public Trip(String code, String name, int durationDays, LocalDate startDate, String resort,
			BigDecimal pricePerPerson, String imageName, String description) {
		this.code = code;
		this.name = name;
		this.durationDays = durationDays;
		this.startDate = startDate;
		this.resort = resort;
		this.pricePerPerson = pricePerPerson;
		this.imageName = imageName;
		this.description = description;
	}

	public Long getId() {
		return id;
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

	public String getImageName() {
		return imageName;
	}

	public String getDescription() {
		return description;
	}
}
