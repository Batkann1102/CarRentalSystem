import { RENTAL_TYPE_LABELS } from './constants';

export function formatCurrency(value) {
  const numericValue = Number(value ?? 0);
  return `₮${numericValue.toLocaleString('en-US')}`;
}

export function formatRentalType(value) {
  return RENTAL_TYPE_LABELS[value] ?? value ?? '-';
}

export function formatDate(value) {
  if (!value) {
    return '-';
  }

  return new Date(value).toLocaleString('mn-MN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
}

export function mapErrorMessage(message) {
  if (!message) {
    return 'Алдаа гарлаа.';
  }

  if (message.includes('Failed to fetch')) {
    return 'Сервертэй холбогдож чадсангүй.';
  }

  return message;
}

export function normalizeCarPayload(form) {
  return {
    id: form.id.trim(),
    brand: form.brand.trim(),
    model: form.model.trim(),
    plateNumber: form.plateNumber.trim(),
    rentalType: form.rentalType,
    pricePerUnit: Number(form.pricePerUnit),
    available: Boolean(form.available)
  };
}

