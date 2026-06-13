/**
 * Displays existing trips for admin edit/delete selection.
 *
 * @param {Object} props component properties
 * @param {Array} props.trips current trip list
 * @param {boolean} props.isSaving whether an admin request is active
 * @param {Function} props.onCreateClick create action callback
 * @param {Function} props.onEditClick edit action callback
 * @param {Function} props.onDeleteClick delete action callback
 * @returns {JSX.Element} rendered admin trip list
 */
function AdminTripList({
	trips,
	isSaving,
	onCreateClick,
	onEditClick,
	onDeleteClick,
}) {
	return (
		<section className="admin-list" aria-label="Existing trips">
			<div className="admin-list__header">
				<h2>Existing Trips</h2>
				<button type="button" onClick={onCreateClick}>
					Create New Trip
				</button>
			</div>

			{trips.length === 0 ? (
				<p className="status-message">No trips are currently available.</p>
			) : (
				<div className="admin-trip-table">
					{trips.map((trip) => (
						<div className="admin-trip-row" key={trip.code}>
							<div className="admin-trip-row__thumbnail">
								<img src={`/images/${trip.imageName}`} alt={trip.name} />
							</div>

							<div>
								<p className="admin-trip-row__code">{trip.code}</p>
								<p className="admin-trip-row__name">{trip.name}</p>
								<p className="admin-trip-row__meta">
									{trip.resort} · ${Number(trip.pricePerPerson).toFixed(2)}
								</p>
							</div>

							<div className="admin-trip-row__actions">
								<button type="button" onClick={() => onEditClick(trip)}>
									Edit
								</button>
								<button
									type="button"
									onClick={() => onDeleteClick(trip)}
									disabled={isSaving}
								>
									Delete
								</button>
							</div>
						</div>
					))}
				</div>
			)}
		</section>
	);
}

export default AdminTripList;
