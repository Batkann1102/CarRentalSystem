import { useCallback, useEffect, useState } from 'react';
import { ArrowRight, CarFront, RefreshCw } from 'lucide-react';
import { Link } from 'react-router-dom';
import SummaryCards from '../components/SummaryCards';
import { api } from '../api';
import { formatCurrency, formatDate, formatRentalType, mapErrorMessage } from '../utils';

function DashboardPage({ onNotify }) {
  const [cars, setCars] = useState([]);
  const [rentals, setRentals] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadDashboard = useCallback(async (showToast = false) => {
    setLoading(true);
    try {
      await api.getHealth();
      const [carsResponse, rentalsResponse] = await Promise.all([
        api.getCars(),
        api.getRentals()
      ]);
      setCars(carsResponse);
      setRentals(rentalsResponse);
      if (showToast) {
        onNotify('success', 'Мэдээлэл шинэчлэгдлээ.');
      }
    } catch (error) {
      onNotify('error', mapErrorMessage(error.message));
    } finally {
      setLoading(false);
    }
  }, [onNotify]);

  useEffect(() => {
    loadDashboard(false);
  }, [loadDashboard]);

  const availableCars = cars.filter((car) => car.available);

  return (
    <>
      <section className="hero card-panel">
        <div>
          <p className="eyebrow">Хяналтын самбар</p>
          <h2>CRUD-тэй шинэ урсгал</h2>
          <p className="hero-copy">
            Машин нэмэх, засах, устгах, түрээслэх үйлдлүүдийг тусдаа хуудсаар удирдахаар frontend-ийг шинэчиллээ.
          </p>
          <div className="hero-actions">
            <Link to="/cars" className="primary-button inline-button">Машинуудыг удирдах</Link>
            <button type="button" className="ghost-button inline-button" onClick={() => loadDashboard(true)} disabled={loading}>
              <RefreshCw size={16} />
              <span>Шинэчлэх</span>
            </button>
          </div>
        </div>
        <div className="hero-side card-panel nested-panel">
          <p className="section-label">Шуурхай үйлдэл</p>
          {availableCars[0] ? (
            <Link to={`/cars/${availableCars[0].id}/rent`} className="quick-link">
              <CarFront size={18} />
              <span>{availableCars[0].brand} {availableCars[0].model} түрээслэх</span>
              <ArrowRight size={16} />
            </Link>
          ) : (
            <div className="empty-inline">
              <strong>Бэлэн машин алга</strong>
              <p>Эхлээд шинэ машин нэмнэ үү.</p>
            </div>
          )}
        </div>
      </section>

      <SummaryCards cars={cars} rentals={rentals} />

      <section className="dashboard-grid">
        <article className="card-panel">
          <div className="section-header">
            <div>
              <p className="section-label">Сүүлийн машинууд</p>
              <h3>Нийт жагсаалт</h3>
            </div>
            <Link to="/cars" className="ghost-button small-button">Бүгдийг харах</Link>
          </div>
          {loading ? (
            <div className="empty-state"><p>Ачааллаж байна...</p></div>
          ) : (
            <div className="list-stack compact-list">
              {cars.slice(0, 5).map((car) => (
                <article className="list-item" key={car.id}>
                  <div>
                    <div className="title-row">
                      <CarFront size={18} />
                      <h3>{car.brand} {car.model}</h3>
                    </div>
                    <p>{car.plateNumber}</p>
                    <p className="subtle">{car.id}</p>
                  </div>
                  <div className="meta">
                    <span className={`pill ${String(car.rentalType).toLowerCase()}`}>{formatRentalType(car.rentalType)}</span>
                    <strong>{formatCurrency(car.pricePerUnit)}</strong>
                  </div>
                </article>
              ))}
            </div>
          )}
        </article>

        <article className="card-panel">
          <div className="section-header">
            <div>
              <p className="section-label">Түрээсийн түүх</p>
              <h3>Сүүлийн бүртгэл</h3>
            </div>
          </div>
          {loading ? (
            <div className="empty-state"><p>Ачааллаж байна...</p></div>
          ) : rentals.length === 0 ? (
            <div className="empty-state">
              <strong>Түүх хоосон байна</strong>
              <p>Түрээс үүсгэсний дараа энд харагдана.</p>
            </div>
          ) : (
            <div className="list-stack compact-list">
              {rentals.slice(0, 5).map((rental) => (
                <article className="list-item" key={rental.id}>
                  <div>
                    <h3>{rental.customerName}</h3>
                    <p>{rental.carId}</p>
                    <p className="subtle">{formatDate(rental.rentedAt)}</p>
                  </div>
                  <div className="meta">
                    <span className={`pill ${String(rental.rentalType).toLowerCase()}`}>{formatRentalType(rental.rentalType)}</span>
                    <strong>{formatCurrency(rental.totalPrice)}</strong>
                  </div>
                </article>
              ))}
            </div>
          )}
        </article>
      </section>
    </>
  );
}

export default DashboardPage;

