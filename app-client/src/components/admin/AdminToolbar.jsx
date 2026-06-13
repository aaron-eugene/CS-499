/**
 * Displays the admin page heading and navigation actions.
 *
 * @param {Object} props component properties
 * @param {Function} props.onBackToTravel callback to return to public browsing
 * @param {Function} props.onLogout callback to clear admin credentials
 * @returns {JSX.Element} rendered admin toolbar
 */
function AdminToolbar({ onBackToTravel, onLogout }) {
	return (
		<div className="admin-toolbar">
			<div>
				<p className="admin-kicker">Trips (admin)</p>
				<h1>Admin Trip Management</h1>
			</div>

			<div className="admin-toolbar__actions">
				<button type="button" onClick={onBackToTravel}>
					Back to Travel
				</button>
				<button type="button" onClick={onLogout}>
					Log Out
				</button>
			</div>
		</div>
	);
}

export default AdminToolbar;
