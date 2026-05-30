package com.travlr.api.dto;

import java.math.BigDecimal;

/**
 * Represents optional query criteria for searching, filtering, sorting, and
 * paginating trip results.
 *
 * The search field is treated as a general catalog keyword. It is matched
 * against trip code, name, resort, and description so callers can search
 * visible trip information without selecting a specific field. Numeric
 * criteria support price and duration range filtering, while sort,
 * direction, page, and size control result ordering and bounded pagination.
 *
 * This DTO keeps HTTP query parameters out of the service method signature so
 * additional criteria can be added without creating a long parameter list.
 */
public class TripSearchCriteria {
	private String search;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private Integer minDays;
	private Integer maxDays;
	private String sort;
	private String direction;
	private Integer page;
	private Integer size;

	/**
	 * Creates an empty criteria object.
	 *
	 * Empty criteria means the service should use its default query behavior.
	 */
	public TripSearchCriteria() {
	}

	/**
	 * Creates a criteria object with all supported query options.
	 *
	 * @param search    optional text search value
	 * @param minPrice  optional minimum price per person
	 * @param maxPrice  optional maximum price per person
	 * @param minDays   optional minimum trip duration in days
	 * @param maxDays   optional maximum trip duration in days
	 * @param sort      optional sort field
	 * @param direction optional sort direction
	 * @param page      optional zero-based page number
	 * @param size      optional page size
	 */
	public TripSearchCriteria(String search, BigDecimal minPrice, BigDecimal maxPrice,
			Integer minDays, Integer maxDays, String sort, String direction,
			Integer page, Integer size) {
		this.search = search;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.minDays = minDays;
		this.maxDays = maxDays;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.size = size;
	}

	public String getSearch() {
		return search;
	}

	public BigDecimal getMinPrice() {
		return minPrice;
	}

	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	public Integer getMinDays() {
		return minDays;
	}

	public Integer getMaxDays() {
		return maxDays;
	}

	public String getSort() {
		return sort;
	}

	public String getDirection() {
		return direction;
	}

	public Integer getPage() {
		return page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}

	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	public void setMinDays(Integer minDays) {
		this.minDays = minDays;
	}

	public void setMaxDays(Integer maxDays) {
		this.maxDays = maxDays;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
}
