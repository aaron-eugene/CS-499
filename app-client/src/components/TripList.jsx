import TripCard from './TripCard';

/**
 * Displays a collection of travel packages.
 *
 * @param {Object} props component properties
 * @param {Array} props.trips trips to display
 * @returns {JSX.Element} rendered trip list
 */
function TripList({ trips }) {
	if (trips.length === 0) {
		return <p className="status-message">No trips matched the current filters.</p>;
	}

	return (
		<section className="trip-list" aria-label="Available trips">
			{trips.map((trip) => (
				<TripCard key={trip.code} trip={trip} />
			))}
		</section>
	);
}

export default TripList;
