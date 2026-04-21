import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import RentalForm from '../components/RentalForm';
import { EMPTY_RENT_FORM } from '../constants';
import { api } from '../api';
import { mapErrorMessage } from '../utils';

function CarRentPage({ onNotify }) {
  const { carId } = useParams();
  const navigate = useNavigate();
  const [car, setCar] = useState(null);
  const [form, setForm] = useState(EMPTY_RENT_FORM);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const loadCar = useCallback(async () => {
    setLoading(true);
    try {
      const response = await api.getCar(carId);
      setCar(response);
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
      const invoice = await api.rentCar({
        carId: car.id,
        customerName: form.customerName.trim(),
        rentalType: car.rentalType,
        duration: Number(form.duration)
      });
      onNotify('success', `Түрээс амжилттай. Нэхэмжлэх: ${invoice.invoiceId}`);
      navigate('/');
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
          <p className="eyebrow">Rent</p>
          <h2>Машин түрээслэх</h2>
          <p className="hero-copy">Тухайн машины түрээсийг тусдаа хуудсаар баталгаажуулна.</p>
        </div>
        <Link to="/cars" className="ghost-button small-button">Буцах</Link>
      </div>

      {loading || !car ? (
        <div className="empty-state"><p>Машины мэдээлэл ачааллаж байна...</p></div>
      ) : !car.available ? (
        <div className="empty-state">
          <strong>Энэ машин одоогоор түрээстэй байна.</strong>
          <p>Машины мэдээллийг засах эсвэл өөр машин сонгоно уу.</p>
        </div>
      ) : (
        <RentalForm
          car={car}
          form={form}
          onChange={handleChange}
          onSubmit={handleSubmit}
          submitting={submitting}
        />
      )}
    </section>
  );
}

export default CarRentPage;

