import { Link } from 'react-router-dom';
import { CarFront, Edit3, Trash2 } from 'lucide-react';
import { formatCurrency, formatRentalType } from '../utils';

function CarTable({ cars, onDelete, deletingId }) {
  if (cars.length === 0) {
    return (
      <div className="empty-state">
        <strong>Машин олдсонгүй</strong>
        <p>Шүүлтүүрээ өөрчлөөд дахин оролдоно уу.</p>
      </div>
    );
  }

  return (
    <div className="table-wrapper">
      <table className="data-table">
        <thead>
          <tr>
            <th className="id-column">ID</th>
            <th className="car-column">Машин</th>
            <th className="type-column">Төрөл</th>
            <th className="price-column">Үнэ</th>
            <th className="status-column">Төлөв</th>
            <th className="actions-column">Үйлдэл</th>
          </tr>
        </thead>
        <tbody>
          {cars.map((car) => (
            <tr key={car.id}>
              <td className="id-cell">{car.id}</td>
              <td className="car-cell">
                <div className="car-name-stack">
                  <strong>{car.brand}</strong>
                  <span>{car.model}</span>
                  <small>Дугаар: {car.plateNumber || '-'}</small>
                </div>
              </td>
              <td className="type-cell">
                <span className={`pill ${String(car.rentalType).toLowerCase()}`}>
                  {formatRentalType(car.rentalType)}
                </span>
              </td>
              <td className="price-cell">{formatCurrency(car.pricePerUnit)}</td>
              <td className="status-cell">
                <span className={`status-badge ${car.available ? 'up' : 'down'}`}>
                  {car.available ? 'Бэлэн' : 'Түрээстэй'}
                </span>
              </td>
              <td className="actions-cell">
                <div className="action-row">
                  <Link
                    className="ghost-button icon-only-button"
                    to={`/cars/${car.id}/edit`}
                    title="Засах"
                    aria-label="Машины мэдээлэл засах"
                  >
                    <Edit3 size={16} />
                  </Link>
                  {car.available ? (
                    <Link
                      className="ghost-button icon-only-button"
                      to={`/cars/${car.id}/rent`}
                      title="Түрээслэх"
                      aria-label="Машин түрээслэх"
                    >
                      <CarFront size={16} />
                    </Link>
                  ) : (
                    <button
                      type="button"
                      className="ghost-button icon-only-button is-disabled"
                      disabled
                      title="Энэ машин одоогоор түрээстэй"
                      aria-label="Энэ машин одоогоор түрээслэх боломжгүй"
                    >
                      <CarFront size={16} />
                    </button>
                  )}
                  <button
                    type="button"
                    className="danger-button icon-only-button"
                    disabled={deletingId === car.id}
                    onClick={() => onDelete(car)}
                    title={deletingId === car.id ? 'Устгаж байна...' : 'Устгах'}
                    aria-label={deletingId === car.id ? 'Машин устгаж байна' : 'Машин устгах'}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default CarTable;

