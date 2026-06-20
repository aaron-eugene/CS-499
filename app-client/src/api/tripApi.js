const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

/**
 * Builds an HTTP Basic Authorization header value.
 *
 * Credentials are kept by the React component and passed only when an admin
 * request is made. They are not stored in localStorage.
 *
 * @param {string} username admin username
 * @param {string} password admin password
 * @returns {string} Basic authorization header value
 */
function buildBasicAuthHeader(username, password) {
	return `Basic ${btoa(`${username}:${password}`)}`;
}

/**
 * Sends a JSON request to a protected admin endpoint.
 *
 * @param {string} path admin API path
 * @param {Object} options request options
 * @param {string} options.method HTTP method
 * @param {Object} [options.body] optional request body
 * @param {string} options.username admin username
 * @param {string} options.password admin password
 * @returns {Promise<Object|null>} parsed response body or null for empty responses
 */
async function sendAdminRequest(path, { method, body, username, password }) {
	let response;

	try {
		response = await fetch(`${API_BASE_URL}${path}`, {
			method,
			headers: {
				Authorization: buildBasicAuthHeader(username, password),
				'Content-Type': 'application/json',
			},
			body: body ? JSON.stringify(body) : undefined,
		});
	} catch (error) {
		throw new Error('Unable to reach the admin API. Confirm that the backend is running and CORS is configured.');
	}

	if (!response.ok) {
		throw new Error(getAdminErrorMessage(response.status));
	}

	if (response.status === 204) {
		return null;
	}

	return response.json();
}

/**
 * Converts protected admin response codes into user-safe error messages.
 *
 * @param {number} status HTTP response status
 * @returns {string} admin error message
 */
function getAdminErrorMessage(status) {
	switch (status) {
		case 400:
			return 'Trip information did not pass validation. Check the form fields and try again.';
		case 401:
			return 'Admin credentials were not accepted.';
		case 403:
			return 'You are not authorized to perform this admin action.';
		case 404:
			return 'The selected trip was not found.';
		case 409:
			return 'A trip with this code already exists.';
		default:
			return `Admin request failed. Status: ${status}`;;
	}
}

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

/**
 * Creates a new trip through the protected admin API.
 *
 * @param {Object} trip trip create request
 * @param {Object} credentials admin credentials
 * @param {string} credentials.username admin username
 * @param {string} credentials.password admin password
 * @returns {Promise<Object>} created trip
 */
export async function createTrip(trip, credentials) {
	return sendAdminRequest('/api/admin/trips', {
		method: 'POST',
		body: trip,
		...credentials,
	});
}

/**
 * Updates an existing trip through the protected admin API.
 *
 * @param {string} code stable public trip code
 * @param {Object} trip trip update request
 * @param {Object} credentials admin credentials
 * @param {string} credentials.username admin username
 * @param {string} credentials.password admin password
 * @returns {Promise<Object>} updated trip
 */
export async function updateTrip(code, trip, credentials) {
	return sendAdminRequest(`/api/admin/trips/${encodeURIComponent(code)}`, {
		method: 'PUT',
		body: trip,
		...credentials,
	});
}

/**
 * Deletes an existing trip through the protected admin API.
 *
 * @param {string} code stable public trip code
 * @param {Object} credentials admin credentials
 * @param {string} credentials.username admin username
 * @param {string} credentials.password admin password
 * @returns {Promise<null>} empty response
 */
export async function deleteTrip(code, credentials) {
	return sendAdminRequest(`/api/admin/trips/${encodeURIComponent(code)}`, {
		method: 'DELETE',
		...credentials,
	});
}
