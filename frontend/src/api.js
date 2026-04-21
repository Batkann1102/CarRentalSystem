const API_BASE_URL = 'http://localhost:8080';
async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {})
    },
    ...options
  });
  const contentType = response.headers.get('content-type') ?? '';
  const payload = contentType.includes('application/json') ? await response.json() : await response.text();
  if (!response.ok) {
    const message = typeof payload === 'object' && payload !== null && 'error' in payload
      ? payload.error
      : `Request failed with status ${response.status}`;
    throw new Error(message);
  }
  return payload;
}
export const api = {
  getHealth: () => request('/api/health'),
  getCars: () => request('/api/cars'),
  getAvailableCars: () => request('/api/cars/available'),
  getCar: (carId) => request(`/api/cars/${carId}`),
  createCar: (payload) => request('/api/cars', {
    method: 'POST',
    body: JSON.stringify(payload)
  }),
  updateCar: (carId, payload) => request(`/api/cars/${carId}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  }),
  deleteCar: (carId) => request(`/api/cars/${carId}`, {
    method: 'DELETE'
  }),
  getRentals: () => request('/api/rentals'),
  rentCar: (payload) => request('/api/rentals', {
    method: 'POST',
    body: JSON.stringify(payload)
  })
};