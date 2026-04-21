import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import CarForm from '../components/CarForm';
import { api } from '../api';
import { mapErrorMessage, normalizeCarPayload } from '../utils';

function CarEditPage({ onNotify }) {
  const { carId } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const loadCar = useCallback(async () => {
    setLoading(true);
    try {
      const car = await api.getCar(carId);
      setForm({
        id: car.id,
        brand: car.brand,
        model: car.model,
        plateNumber: car.plateNumber,
        rentalType: car.rentalType,
        pricePerUnit: String(car.pricePerUnit),
        available: Boolean(car.available)
      });
    } catch (error) {
      onNotify('error', mapErrorMessage(error.message));
      navigate('/cars');
    } finally {
      setLoading(false);
    }
  }, [carId, navigate, onNotify]);

  useEffect(() => {
    loadCar();
  }, [loadCar]);

  function handleChange(field, value) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await api.updateCar(carId, normalizeCarPayload(form));
      onNotify('success', 'Машины мэдээлэл шинэчлэгдлээ.');
      navigate('/cars');
    } catch (error) {
      onNotify('error', mapErrorMessage(error.message));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="card-panel page-header">
      <div className="section-header">
        <div>
          <p className="eyebrow">Update</p>
          <h2>Машин засварлах</h2>
          <p className="hero-copy">Машины үндсэн мэдээлэл, үнэ, түрээсийн төлөвийг энэ хуудсаас шинэчилнэ.</p>
        </div>
        <Link to="/cars" className="ghost-button small-button">Буцах</Link>
      </div>
      {loading || !form ? (
        <div className="empty-state"><p>Мэдээлэл ачааллаж байна...</p></div>
      ) : (
        <CarForm
          form={form}
          onChange={handleChange}
          onSubmit={handleSubmit}
          submitLabel="Өөрчлөлт хадгалах"
          submitting={submitting}
          disableId
        />
      )}
    </section>
  );
}

export default CarEditPage;

