import { CarFront, History, WalletCards, Wrench } from 'lucide-react';
import { formatCurrency } from '../utils';

function SummaryCards({ cars, rentals }) {
  const availableCars = cars.filter((car) => car.available).length;
  const totalAmount = rentals.reduce((sum, rental) => sum + Number(rental.totalPrice ?? 0), 0);

  const items = [
    { label: 'Нийт машин', value: cars.length, icon: CarFront },
    { label: 'Бэлэн машин', value: availableCars, icon: Wrench },
    { label: 'Нийт түрээс', value: rentals.length, icon: History },
    { label: 'Нийт орлого', value: formatCurrency(totalAmount), icon: WalletCards }
  ];

  return (
    <section className="stats-grid">
      {items.map((item) => (
        <article key={item.label} className="stat-card card-panel">
          <div className="stat-heading">
            <div className="stat-icon"><item.icon size={18} /></div>
            <span className="stat-title">{item.label}</span>
          </div>
          <strong>{item.value}</strong>
        </article>
      ))}
    </section>
  );
}

export default SummaryCards;

