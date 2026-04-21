import { useCallback, useEffect, useMemo, useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import CarTable from '../components/CarTable';
import { api } from '../api';
import { mapErrorMessage } from '../utils';

function CarsPage({ onNotify }) {
  const outletContext = useOutletContext();
  const carsRefreshKey = outletContext?.carsRefreshKey ?? 0;
  const [cars, setCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState('');
  const [query, setQuery] = useState('');

  const loadCars = useCallback(async () => {
    setLoading(true);
    try {
      const response = await api.getCars();
      setCars(response);
    } catch (error) {
      onNotify('error', mapErrorMessage(error.message));
    } finally {
      setLoading(false);
    }
  }, [onNotify]);

  useEffect(() => {
    loadCars();
  }, [loadCars, carsRefreshKey]);

  const filteredCars = useMemo(() => {
    const value = query.trim().toLowerCase();
    if (!value) {
      return cars;
    }

    return cars.filter((car) => [car.id, car.brand, car.model, car.plateNumber]
      .some((field) => String(field ?? '').toLowerCase().includes(value)));
  }, [cars, query]);

  async function handleDelete(car) {
    const confirmed = window.confirm(`${car.brand} ${car.model} машиныг устгах уу?`);
    if (!confirmed) {
      return;
    }

    setDeletingId(car.id);
    try {
      await api.deleteCar(car.id);
      onNotify('success', 'Машин устгагдлаа.');
      await loadCars();
    } catch (error) {
      onNotify('error', mapErrorMessage(error.message));
    } finally {
      setDeletingId('');
    }
  }

  return (
    <>
      <section className="page-header card-panel">
        <div>
          <p className="eyebrow">Машины CRUD</p>
          <h2>Машины удирдлага</h2>
          <p className="hero-copy">Жагсаалт харах, шинэ машин нэмэх, засах, устгах, түрээслэх үйлдлийг эндээс эхлүүлнэ.</p>
        </div>
      </section>

      <section>
        <article className="card-panel">
          <div className="section-header align-start mobile-stack">
            <div>
              <p className="section-label">Read / Delete</p>
              <h3>Машины жагсаалт</h3>
            </div>
            <div className="toolbar-row">
              <input
                type="search"
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="ID, брэнд, модель, дугаар..."
              />
            </div>
          </div>
          {loading ? (
            <div className="empty-state"><p>Машинуудыг ачааллаж байна...</p></div>
          ) : (
            <CarTable cars={filteredCars} onDelete={handleDelete} deletingId={deletingId} />
          )}
        </article>
      </section>
    </>
  );
}

export default CarsPage;

