/**
 * Displays a single travel package.
 *
 * @param {Object} props component properties
 * @param {Object} props.trip trip data to display
 * @returns {JSX.Element} rendered trip card
 */
function TripCard({ trip }) {
	return (
		<article className="trip-card">
            <div className="trip-card__image">
                <img src={`/images/${trip.imageName}`} alt={trip.name} />
            </div>

			<div className="trip-card__content">
				<p className="trip-card__code">{trip.code}</p>
				<h2>{trip.name}</h2>
				<p className="trip-card__resort">{trip.resort}</p>
				<p>{trip.description}</p>

				<div className="trip-card__details">
					<span>{trip.durationDays} days</span>
					<span>{trip.startDate}</span>
					<span>${Number(trip.pricePerPerson).toFixed(2)} per person</span>
				</div>
			</div>
		</article>
	);
}

export default TripCard;
