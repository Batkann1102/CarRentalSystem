import { formatCurrency, formatRentalType } from '../utils';

function RentalForm({ car, form, onChange, onSubmit, submitting }) {
  const estimate = Number(car?.pricePerUnit ?? 0) * Number(form.duration || 0);

  return (
    <form className="form-grid" onSubmit={onSubmit}>
      <div className="info-panel">
        <p className="section-label">Сонгосон машин</p>
        <strong>{car.brand} {car.model}</strong>
        <div className="info-grid">
          <p><span>ID:</span> {car.id}</p>
          <p><span>Дугаар:</span> {car.plateNumber}</p>
          <p><span>Төрөл:</span> {formatRentalType(car.rentalType)}</p>
          <p><span>Үнэ:</span> {formatCurrency(car.pricePerUnit)}</p>
        </div>
      </div>

      <label>
        <span>Харилцагчийн нэр</span>
        <input
          type="text"
          value={form.customerName}
          onChange={(event) => onChange('customerName', event.target.value)}
          placeholder="Жишээ: Бат"
          minLength={2}
          required
        />
      </label>

      <div className="two-columns">
        <label>
          <span>Түрээсийн төрөл</span>
          <input type="text" value={formatRentalType(car.rentalType)} disabled />
        </label>
        <label>
          <span>Хугацаа</span>
          <input
            type="number"
            min="1"
            value={form.duration}
            onChange={(event) => onChange('duration', event.target.value)}
            required
          />
        </label>
      </div>

      <div className="info-panel compact">
        <p className="section-label">Тооцоолол</p>
        <strong>{formatCurrency(estimate)}</strong>
      </div>

      <button type="submit" className="primary-button" disabled={submitting || !car.available}>
        {submitting ? 'Бүртгэж байна...' : 'Түрээс бүртгэх'}
      </button>
    </form>
  );
}

export default RentalForm;

