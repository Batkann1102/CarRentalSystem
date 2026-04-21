import { RENTAL_TYPE_LABELS } from '../constants';

function CarForm({ form, onChange, onSubmit, submitLabel, submitting, disableId = false, compact = false }) {
  const rowClassName = compact ? 'form-grid compact-form-grid' : 'two-columns';

  return (
    <form className={`form-grid ${compact ? 'compact-form' : ''}`} onSubmit={onSubmit}>
      <div className={rowClassName}>
        <label>
          <span>ID</span>
          <input
            type="text"
            value={form.id}
            onChange={(event) => onChange('id', event.target.value)}
            placeholder="CAR-005"
            disabled={disableId}
            required
          />
        </label>
        <label>
          <span>Улсын дугаар</span>
          <input
            type="text"
            value={form.plateNumber}
            onChange={(event) => onChange('plateNumber', event.target.value.toUpperCase())}
            placeholder="UBA-5555"
            required
          />
        </label>
      </div>

      <div className={rowClassName}>
        <label>
          <span>Брэнд</span>
          <input
            type="text"
            value={form.brand}
            onChange={(event) => onChange('brand', event.target.value)}
            placeholder="Toyota"
            required
          />
        </label>
        <label>
          <span>Модель</span>
          <input
            type="text"
            value={form.model}
            onChange={(event) => onChange('model', event.target.value)}
            placeholder="Prius"
            required
          />
        </label>
      </div>

      <div className={rowClassName}>
        <label>
          <span>Түрээсийн төрөл</span>
          <select value={form.rentalType} onChange={(event) => onChange('rentalType', event.target.value)} required>
            {Object.entries(RENTAL_TYPE_LABELS).map(([value, label]) => (
              <option key={value} value={value}>{label}</option>
            ))}
          </select>
        </label>
        <label>
          <span>Үнэ</span>
          <input
            type="number"
            min="1"
            value={form.pricePerUnit}
            onChange={(event) => onChange('pricePerUnit', event.target.value)}
            placeholder="120000"
            required
          />
        </label>
      </div>

      <label className="checkbox-row">
        <input
          type="checkbox"
          checked={form.available}
          onChange={(event) => onChange('available', event.target.checked)}
        />
        <span>Түрээслэх боломжтой</span>
      </label>

      <button type="submit" className="primary-button" disabled={submitting}>
        {submitting ? 'Хадгалж байна...' : submitLabel}
      </button>
    </form>
  );
}

export default CarForm;

