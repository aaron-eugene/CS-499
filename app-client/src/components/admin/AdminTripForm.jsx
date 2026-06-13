/**
 * Displays the admin create/edit trip form.
 *
 * @param {Object} props component properties
 * @param {string} props.mode create or edit mode
 * @param {Object} props.formData current form values
 * @param {boolean} props.isSaving whether an admin request is active
 * @param {Function} props.onInputChange input change callback
 * @param {Function} props.onSubmit form submit callback
 * @param {Function} props.onCancel cancel callback
 * @returns {JSX.Element} rendered trip form
 */
function AdminTripForm({
	mode,
	formData,
	isSaving,
	onInputChange,
	onSubmit,
	onCancel,
}) {
	return (
		<form className="admin-form" onSubmit={onSubmit}>
			<h2>{mode === 'create' ? 'Add Trip' : 'Edit Trip'}</h2>

			<label>
				Code
				<input
					type="text"
					name="code"
					value={formData.code}
					readOnly={mode === 'edit'}
					onChange={onInputChange}
				/>
			</label>

			<label>
				Name
				<input
					type="text"
					name="name"
					value={formData.name}
					onChange={onInputChange}
				/>
			</label>

			<label>
				Duration Days
				<input
					type="number"
					name="durationDays"
					min="1"
					max="60"
					value={formData.durationDays}
					onChange={onInputChange}
				/>
			</label>

			<label>
				Start Date
				<input
					type="date"
					name="startDate"
					value={formData.startDate}
					onChange={onInputChange}
				/>
			</label>

			<label>
				Resort
				<input
					type="text"
					name="resort"
					value={formData.resort}
					onChange={onInputChange}
				/>
			</label>

			<label>
				Price Per Person
				<input
					type="number"
					name="pricePerPerson"
					min="0"
					step="0.01"
					value={formData.pricePerPerson}
					onChange={onInputChange}
				/>
			</label>

			<label>
				Image Name
				<input
					type="text"
					name="imageName"
					placeholder="reef1.jpg"
					value={formData.imageName}
					onChange={onInputChange}
				/>
			</label>

			<label>
				Description
				<textarea
					name="description"
					rows="5"
					value={formData.description}
					onChange={onInputChange}
				/>
			</label>

			<div className="admin-form__actions">
				<button type="submit" disabled={isSaving}>
					{isSaving
						? 'Saving...'
						: mode === 'create'
							? 'Save'
							: 'Save Changes'}
				</button>
				<button type="button" onClick={onCancel} disabled={isSaving}>
					Cancel
				</button>
			</div>
		</form>
	);
}

export default AdminTripForm;
