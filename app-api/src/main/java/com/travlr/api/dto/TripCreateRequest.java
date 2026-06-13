package com.travlr.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents client-provided trip data for creating a new trip.
 *
 * This DTO separates incoming create-request data from the persistent Trip
 * entity so validation can be applied at the API boundary before data reaches
 * the service or repository layers. The trip code is included here because new
 * trip records need a stable public identifier.
 */
public class TripCreateRequest {
	@NotBlank(message = "Trip code is required.")
	@Size(min = 14, max = 14, message = "Trip code must be exactly 14 characters.")
	@Pattern(regexp = "^[A-Z]{6}\\d{8}$", message = "Trip code must use six uppercase letters followed by an eight-digit date.")
	private String code;

	@NotBlank(message = "Trip name is required.")
	@Size(max = 100, message = "Trip name must be 100 characters or fewer.")
	private String name;

	@NotNull(message = "Trip duration is required.")
	@Min(value = 1, message = "Trip duration must be at least 1 day.")
	@Max(value = 30, message = "Trip duration must be 30 days or fewer.")
	private Integer durationDays;

	@NotNull(message = "Start date is required.")
	@Future(message = "Start date must be tomorrow or later.")
	private LocalDate startDate;

	@NotBlank(message = "Resort is required.")
	@Size(max = 150, message = "Resort must be 150 characters or fewer.")
	private String resort;

	@NotNull(message = "Price per person is required.")
	@DecimalMin(value = "0.01", message = "Price per person must be greater than 0.")
	@DecimalMax(value = "99999.99", message = "Price per person must be less than 100,000.")
	private BigDecimal pricePerPerson;

	@NotBlank(message = "Image filename is required.")
	@Size(max = 100, message = "Image filename must be 100 characters or fewer.")
	@Pattern(regexp = "^[a-zA-Z0-9_-]+\\.(jpg|jpeg|png|webp)$", message = "Image filename must be a JPG, PNG, or WebP file using only letters, numbers, dashes, or underscores.")
	private String imageName;

	@NotBlank(message = "Description is required.")
	@Size(max = 1000, message = "Description must be 1000 characters or fewer.")
	private String description;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDurationDays() {
		return durationDays;
	}

	public void setDurationDays(Integer durationDays) {
		this.durationDays = durationDays;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getResort() {
		return resort;
	}

	public void setResort(String resort) {
		this.resort = resort;
	}

	public BigDecimal getPricePerPerson() {
		return pricePerPerson;
	}

	public void setPricePerPerson(BigDecimal pricePerPerson) {
		this.pricePerPerson = pricePerPerson;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
