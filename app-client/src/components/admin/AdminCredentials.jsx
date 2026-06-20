/**
 * Displays compact admin Basic Auth credential inputs.
 *
 * @param {Object} props component properties
 * @param {string} props.username admin username
 * @param {string} props.password admin password
 * @param {Function} props.onUsernameChange username update callback
 * @param {Function} props.onPasswordChange password update callback
 * @returns {JSX.Element} rendered credentials section
 */
function AdminCredentials({
	username,
	password,
	onUsernameChange,
	onPasswordChange,
}) {
	return (
		<section className="admin-credentials" aria-label="Admin credentials">
			<label>
				<span>Username</span>
				<input
					type="text"
					name="travlrAdminUsername"
					value={username}
					autoComplete="off"
					onChange={(event) => onUsernameChange(event.target.value)}
				/>
			</label>

			<label>
				<span>Password</span>
				<input
					type="password"
					name="travlrAdminPassword"
					value={password}
					placeholder="changeme"
					autoComplete="new-password"
					onChange={(event) => onPasswordChange(event.target.value)}
				/>
			</label>
		</section>
	);
}

export default AdminCredentials;
