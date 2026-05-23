const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

/**
 * Retrieves trips from the backend API.
 *
 * @param {Object} options optional query settings
 * @param {string} options.sort optional sort field
 * @param {string|number} options.maxPrice optional maximum price per person
 * @returns {Promise<Array>} list of trips
 */
export async function getTrips({ sort = '', maxPrice = '' } = {}) {
	const params = new URLSearchParams();

	if (sort) {
		params.set('sort', sort);
	}

	if (maxPrice) {
		params.set('maxPrice', maxPrice);
	}

	const query = params.toString();
	const url = `${API_BASE_URL}/api/trips${query ? `?${query}` : ''}`;

	const response = await fetch(url);

	if (!response.ok) {
		throw new Error(`Unable to retrieve trips. Status: ${response.status}`);
	}

	return response.json();
}
