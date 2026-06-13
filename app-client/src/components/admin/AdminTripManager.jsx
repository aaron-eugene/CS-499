import { useState } from 'react';
import { createTrip, deleteTrip, updateTrip } from '../../api/tripApi';
import AdminCredentials from './AdminCredentials';
import AdminMessages from './AdminMessages';
import AdminToolbar from './AdminToolbar';
import AdminTripForm from './AdminTripForm';
import AdminTripList from './AdminTripList';

const EMPTY_FORM = {
	code: '',
	name: '',
	durationDays: '',
	startDate: '',
	resort: '',
	pricePerPerson: '',
	imageName: '',
	description: '',
};

/**
 * Coordinates the admin trip-management workflow.
 *
 * This component owns admin state and API behavior while delegating display
 * sections to smaller child components.
 *
 * @param {Object} props component properties
 * @param {Array} props.trips current trip list
 * @param {Function} props.onTripsChanged callback to refresh trip data
 * @param {Function} props.onBackToTravel callback to return to public browsing
 * @returns {JSX.Element} rendered admin manager
 */
function AdminTripManager({ trips, onTripsChanged, onBackToTravel }) {
	const [username, setUsername] = useState('admin');
	const [password, setPassword] = useState('');
	const [mode, setMode] = useState('list');
	const [selectedCode, setSelectedCode] = useState('');
	const [formData, setFormData] = useState(EMPTY_FORM);
	const [statusMessage, setStatusMessage] = useState('');
	const [errorMessage, setErrorMessage] = useState('');
	const [isSaving, setIsSaving] = useState(false);

	const credentials = {
		username,
		password,
	};

	function handleInputChange(event) {
		const { name, value } = event.target;

		setFormData((currentFormData) => ({
			...currentFormData,
			[name]: value,
		}));
	}

	function handleCreateClick() {
		setMode('create');
		setSelectedCode('');
		setFormData(EMPTY_FORM);
		setStatusMessage('');
		setErrorMessage('');
	}

	function handleEditClick(trip) {
		setMode('edit');
		setSelectedCode(trip.code);
		setFormData({
			code: trip.code,
			name: trip.name,
			durationDays: String(trip.durationDays),
			startDate: trip.startDate,
			resort: trip.resort,
			pricePerPerson: String(trip.pricePerPerson),
			imageName: trip.imageName,
			description: trip.description,
		});
		setStatusMessage('');
		setErrorMessage('');
	}

	function handleCancel() {
		setMode('list');
		setSelectedCode('');
		setFormData(EMPTY_FORM);
		setErrorMessage('');
	}

	function handleLogout() {
		setPassword('');
		setMode('list');
		setSelectedCode('');
		setFormData(EMPTY_FORM);
		setStatusMessage('Admin credentials cleared.');
		setErrorMessage('');
	}

	function buildCreateRequest() {
		return {
			code: formData.code.trim(),
			name: formData.name.trim(),
			durationDays: Number(formData.durationDays),
			startDate: formData.startDate,
			resort: formData.resort.trim(),
			pricePerPerson: Number(formData.pricePerPerson),
			imageName: formData.imageName.trim(),
			description: formData.description.trim(),
		};
	}

	function buildUpdateRequest() {
		return {
			name: formData.name.trim(),
			durationDays: Number(formData.durationDays),
			startDate: formData.startDate,
			resort: formData.resort.trim(),
			pricePerPerson: Number(formData.pricePerPerson),
			imageName: formData.imageName.trim(),
			description: formData.description.trim(),
		};
	}

	async function handleSubmit(event) {
		event.preventDefault();

		if (!username || !password) {
			setErrorMessage('Enter admin credentials before saving changes.');
			return;
		}

		try {
			setIsSaving(true);
			setStatusMessage('');
			setErrorMessage('');

			if (mode === 'create') {
				await createTrip(buildCreateRequest(), credentials);
				setStatusMessage(`Created trip ${formData.code.trim()}.`);
			} else {
				await updateTrip(selectedCode, buildUpdateRequest(), credentials);
				setStatusMessage(`Updated trip ${selectedCode}.`);
			}

			setMode('list');
			setSelectedCode('');
			setFormData(EMPTY_FORM);
			await onTripsChanged();
		} catch (error) {
			setErrorMessage(error.message);
		} finally {
			setIsSaving(false);
		}
	}

	async function handleDeleteClick(trip) {
		if (!username || !password) {
			setErrorMessage('Enter admin credentials before deleting trips.');
			return;
		}

		const confirmed = window.confirm(
			`Delete ${trip.code} - ${trip.name}? This cannot be undone.`
		);

		if (!confirmed) {
			return;
		}

		try {
			setIsSaving(true);
			setStatusMessage('');
			setErrorMessage('');

			await deleteTrip(trip.code, credentials);
			setStatusMessage(`Deleted trip ${trip.code}.`);
			await onTripsChanged();
		} catch (error) {
			setErrorMessage(error.message);
		} finally {
			setIsSaving(false);
		}
	}

	return (
		<section className="admin-page" aria-label="Admin trip management">
			<AdminToolbar
				onBackToTravel={onBackToTravel}
				onLogout={handleLogout}
			/>

			<AdminCredentials
				username={username}
				password={password}
				onUsernameChange={setUsername}
				onPasswordChange={setPassword}
			/>

			<AdminMessages
				statusMessage={statusMessage}
				errorMessage={errorMessage}
			/>

			{mode === 'list' ? (
				<AdminTripList
					trips={trips}
					isSaving={isSaving}
					onCreateClick={handleCreateClick}
					onEditClick={handleEditClick}
					onDeleteClick={handleDeleteClick}
				/>
			) : (
				<AdminTripForm
					mode={mode}
					formData={formData}
					isSaving={isSaving}
					onInputChange={handleInputChange}
					onSubmit={handleSubmit}
					onCancel={handleCancel}
				/>
			)}
		</section>
	);
}

export default AdminTripManager;
