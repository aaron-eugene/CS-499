/**
 * Displays admin success and error messages.
 *
 * @param {Object} props component properties
 * @param {string} props.statusMessage success/status message
 * @param {string} props.errorMessage error message
 * @returns {JSX.Element|null} rendered messages
 */
function AdminMessages({ statusMessage, errorMessage }) {
	if (!statusMessage && !errorMessage) {
		return null;
	}

	return (
		<>
			{statusMessage && (
				<p className="status-message status-message--success">
					{statusMessage}
				</p>
			)}

			{errorMessage && (
				<p className="status-message status-message--error">
					{errorMessage}
				</p>
			)}
		</>
	);
}

export default AdminMessages;
